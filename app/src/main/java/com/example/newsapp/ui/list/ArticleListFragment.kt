package com.example.newsapp.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.NewsApplication
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleListBinding
import com.example.newsapp.viewmodel.NewsViewModel
import com.example.newsapp.viewmodel.NewsViewModelFactory

/**
 * Fragment hiển thị danh sách tin tức.
 * Hỗ trợ kéo để làm mới, xem Offline và đánh dấu bài báo yêu thích.
 */
class ArticleListFragment : Fragment() {

    private var _binding: FragmentArticleListBinding? = null
    // Binding chỉ hợp lệ giữa onCreateView và onDestroyView
    private val binding get() = _binding!!

    private lateinit var viewModel: NewsViewModel
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Khởi tạo ViewModel thông qua Factory lấy Repository từ NewsApplication
        val app = requireActivity().application as NewsApplication
        val factory = NewsViewModelFactory(app.repository)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        // 2. Thiết lập RecyclerView với các hành động Click
        setupRecyclerView()

        // 3. Quan sát (Observe) dữ liệu và trạng thái từ ViewModel
        observeViewModel()

        // 4. Thiết lập tính năng kéo để làm mới (SwipeRefreshLayout)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshNews()
        }
    }

    private fun setupRecyclerView() {
        // Khởi tạo Adapter với 2 callback: Click xem chi tiết và Click yêu thích
        articleAdapter = ArticleAdapter(
            onItemClick = { article ->
                // Điều hướng sang màn hình chi tiết bài báo kèm theo object Article
                val action = ArticleListFragmentDirections
                    .actionArticleListFragmentToArticleDetailFragment(article)
                findNavController().navigate(action)
            },
            onFavoriteClick = { article ->
                // Gọi ViewModel để đảo trạng thái yêu thích trong Room Database
                viewModel.toggleFavorite(article)
            }
        )

        binding.rvArticles.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(context)
            // Tối ưu hiệu suất khi kích thước item không thay đổi
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        // Cập nhật danh sách bài báo từ Database (Single Source of Truth)
        viewModel.allArticles.observe(viewLifecycleOwner) { articles ->
            // Sử dụng submitList của ListAdapter để cập nhật danh sách hiệu quả hơn
            articleAdapter.submitList(articles)

            // Hiện thông báo nếu không có dữ liệu (ví dụ: lần đầu chạy chưa có mạng)
            binding.llEmptyState.visibility = if (articles.isEmpty()) View.VISIBLE else View.GONE
        }

        // Quản lý trạng thái Loading (ProgressBar và SwipeRefresh)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Chỉ hiện ProgressBar ở giữa màn hình nếu danh sách hiện đang trống
            binding.progressBar.visibility = if (isLoading && articleAdapter.itemCount == 0)
                View.VISIBLE else View.GONE

            // Cập nhật trạng thái xoay của vòng refresh
            binding.swipeRefresh.isRefreshing = isLoading
        }

        // Hiển thị thông báo lỗi (ví dụ: mất kết nối server)
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                // Xóa lỗi trong ViewModel để không bị hiện lại khi xoay màn hình
                viewModel.clearErrorMessage()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Giải phóng binding để tránh rò rỉ bộ nhớ
        _binding = null
    }
}