package com.example.newsapp.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.NewsApplication
import com.example.newsapp.databinding.FragmentArticleListBinding
import com.example.newsapp.viewmodel.NewsViewModel
import com.example.newsapp.viewmodel.NewsViewModelFactory

class ArticleListFragment : Fragment() {

    private var _binding: FragmentArticleListBinding? = null
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

        // 1. Khởi tạo ViewModel thông qua Factory
        val app = requireActivity().application as NewsApplication
        val factory = NewsViewModelFactory(app.repository)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        // 2. Thiết lập RecyclerView
        setupRecyclerView()

        // 3. Quan sát (Observe) dữ liệu và trạng thái từ ViewModel
        observeViewModel()

        // 4. Thiết lập tính năng kéo để làm mới (SwipeRefreshLayout - nếu có trong XML)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshNews()
        }
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter { article ->
            // Xử lý click (Có thể mở Fragment chi tiết ở đây)
            Toast.makeText(context, article.title, Toast.LENGTH_SHORT).show()
        }
        binding.rvArticles.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        // Quan sát danh sách bài báo từ Room
        viewModel.allArticles.observe(viewLifecycleOwner) { articles ->
            articles?.let {
                articleAdapter.setArticles(it)
            }
        }

        // Quan sát trạng thái Loading để hiện/ẩn ProgressBar
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Tắt hiệu ứng quay của SwipeRefreshLayout khi tải xong
            binding.swipeRefresh.isRefreshing = isLoading
        }

        // Quan sát lỗi và hiển thị Toast cho người dùng
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage() // Xóa lỗi sau khi hiển thị
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}