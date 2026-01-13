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
import com.example.newsapp.R
import com.example.newsapp.data.remote.RetrofitClient
import com.example.newsapp.databinding.FragmentArticleListBinding
import com.example.newsapp.viewmodel.NewsViewModel
import com.example.newsapp.viewmodel.NewsViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ArticleListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
        // Lấy repository từ NewsApplication
        val app = requireActivity().application as NewsApplication
        val factory = NewsViewModelFactory(app.repository)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        // 2. Thiết lập RecyclerView
        setupRecyclerView()

        // 3. Quan sát (Observe) dữ liệu từ ViewModel
        observeViewModel()

        // 4. Gọi lấy dữ liệu mới từ Server khi mở app
        // Lưu ý: Bạn cần thay "YOUR_API_KEY" bằng Key thực tế từ newsapi.org
        viewModel.refreshNews(RetrofitClient.instance)
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter { article ->
            // Xử lý khi click vào bài báo (ví dụ: mở màn hình chi tiết)
            Toast.makeText(context, "Bạn chọn: ${article.title}", Toast.LENGTH_SHORT).show()
        }
        binding.rvArticles.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        // Quan sát danh sách bài báo
        viewModel.allArticles.observe(viewLifecycleOwner) { articles ->
            articles?.let {
                articleAdapter.setArticles(it)
            }
        }

        // Quan sát trạng thái Loading để hiện/ẩn ProgressBar
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Quan trọng: Tránh memory leak
    }
}