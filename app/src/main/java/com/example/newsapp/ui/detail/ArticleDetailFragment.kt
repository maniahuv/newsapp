package com.example.newsapp.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleDetailBinding
import com.example.newsapp.viewmodel.NewsViewModel

class ArticleDetailFragment : Fragment() {

    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ArticleDetailFragmentArgs by navArgs()
    private lateinit var viewModel: NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Kết nối ViewModel từ MainActivity
        viewModel = (activity as MainActivity).viewModel
        val article = args.article

        // 2. Thiết lập giao diện ban đầu
        setupUI(article)

        // 3. Quan sát trạng thái Loading (Khi Jsoup đang tải báo offline)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 4. Quan sát lỗi nếu có
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        }

        // 5. Xử lý nút Yêu thích & Tải Offline
        binding.fabFavorite.setOnClickListener {
            viewModel.toggleFavorite(article)
            // Cập nhật lại icon ngay lập tức để tạo cảm giác mượt mà
            article.isFavorite = !article.isFavorite
            updateFavoriteIcon(article.isFavorite)
        }
    }

    private fun setupUI(article: com.example.newsapp.data.local.Article) {
        binding.apply {
            tvDetailTitle.text = article.title
            tvDetailAuthor.text = "Tác giả: ${article.author ?: "Ẩn danh"}"
            tvDetailPublishedAt.text = " • ${article.publishedAt}"

            // LOGIC QUAN TRỌNG: Ưu tiên hiển thị nội dung Offline nếu có
            if (!article.savedFullContent.isNullOrEmpty()) {
                tvDetailDescription.visibility = View.GONE // Ẩn mô tả ngắn
                tvDetailContent.text = article.savedFullContent // Hiện nội dung đầy đủ
                Toast.makeText(context, "Đang đọc ở chế độ Offline", Toast.LENGTH_SHORT).show()
            } else {
                tvDetailDescription.visibility = View.VISIBLE
                tvDetailDescription.text = article.description
                tvDetailContent.text = article.content ?: "Nhấn trái tim để tải nội dung đầy đủ."
            }

            updateFavoriteIcon(article.isFavorite)

            Glide.with(this@ArticleDetailFragment)
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivDetailImage)

            btnOpenBrowser.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                startActivity(intent)
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val icon = if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        binding.fabFavorite.setImageResource(icon)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}