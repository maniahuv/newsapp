package com.example.newsapp.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleDetailBinding

class ArticleDetailFragment : Fragment() {

    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!

    // Sử dụng Safe Args để nhận dữ liệu từ màn hình danh sách truyền sang
    private val args: ArticleDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Sử dụng View Binding để quản lý UI
        _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy đối tượng Article từ arguments đã khai báo trong nav_graph
        val article = args.article

        // Gán dữ liệu lên các View trong layout fragment_article_detail.xml
        binding.apply {
            tvDetailTitle.text = article.title
            tvDetailAuthor.text = "Tác giả: ${article.author}"
            tvDetailPublishedAt.text = " • ${article.publishedAt}"
            tvDetailDescription.text = article.description
            tvDetailContent.text = article.content

            // Sử dụng thư viện Glide để tải ảnh bài báo
            Glide.with(this@ArticleDetailFragment)
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_launcher_background) // Ảnh chờ khi đang load
                .into(ivDetailImage)

            // Xử lý sự kiện khi nhấn nút "Xem trên trình duyệt"
            btnOpenBrowser.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Giải phóng binding để tránh rò rỉ bộ nhớ (Memory Leak)
        _binding = null
    }
}