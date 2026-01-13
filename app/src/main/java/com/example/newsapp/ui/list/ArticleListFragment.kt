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

        // 2. Thiết lập RecyclerView
        setupRecyclerView()

        // 3. Quan sát (Observe) dữ liệu và trạng thái từ ViewModel
        observeViewModel()

        // 4. Thiết lập tính năng kéo để làm mới (SwipeRefreshLayout)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshNews()
        }
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter { article ->
            // Điều hướng sang màn hình chi tiết bài báo kèm theo object Article
            val action = ArticleListFragmentDirections
                .actionArticleListFragmentToArticleDetailFragment(article)
            findNavController().navigate(action)
        }

        binding.rvArticles.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(context)
            // Tối ưu hiệu suất khi kích thước item không thay đổi
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        // Cập nhật danh sách bài báo bằng submitList (dùng cho ListAdapter)
        viewModel.allArticles.observe(viewLifecycleOwner) { articles ->
            articleAdapter.submitList(articles)

            // Ẩn/Hiện thông báo nếu danh sách rỗng (tùy chọn)
            // binding.tvEmptyMessage.visibility = if (articles.isEmpty()) View.VISIBLE else View.GONE
        }

        // Quản lý trạng thái Loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Chỉ hiện ProgressBar trung tâm khi danh sách đang trống hoàn toàn
            binding.progressBar.visibility = if (isLoading && articleAdapter.itemCount == 0)
                View.VISIBLE else View.GONE

            // Cập nhật trạng thái quay của SwipeRefreshLayout
            binding.swipeRefresh.isRefreshing = isLoading
        }

        // Hiển thị lỗi thông qua Toast
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage() // Ngăn lỗi hiển thị lại khi xoay màn hình
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Giải phóng bộ nhớ binding để tránh rò rỉ (memory leak)
        _binding = null
    }
}