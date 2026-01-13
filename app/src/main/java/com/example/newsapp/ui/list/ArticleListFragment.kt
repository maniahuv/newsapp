package com.example.newsapp.ui.list

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.NewsApplication
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleListBinding
import com.example.newsapp.viewmodel.NewsViewModel
import com.example.newsapp.viewmodel.NewsViewModelFactory
import com.google.android.material.tabs.TabLayout

/**
 * Fragment hiển thị danh sách tin tức.
 * Đã gỡ bỏ hoàn toàn hiệu ứng làm mờ (alpha) để tránh lỗi màn hình xám khi Offline.
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

        // 1. Khởi tạo ViewModel
        val app = requireActivity().application as NewsApplication
        val factory = NewsViewModelFactory(app.repository)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        // 2. Thiết lập giao diện
        setupTabLayout()
        setupRecyclerView()
        observeViewModel()

        // 3. Kéo để làm mới
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshNews()
        }

        // 4. Menu Tìm kiếm
        setupMenu()
    }

    private fun setupTabLayout() {
        val categories = listOf("General", "Business", "Entertainment", "Health", "Science", "Sports", "Technology")

        categories.forEach { categoryName ->
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(categoryName))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val category = tab?.text.toString().lowercase()

                // ĐÃ XÓA: Dòng lệnh làm mờ alpha gây lỗi màn hình xám
                viewModel.setCategory(category)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewModel.refreshNews()
            }
        })
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(
            onItemClick = { article ->
                val action = ArticleListFragmentDirections
                    .actionArticleListFragmentToArticleDetailFragment(article)
                findNavController().navigate(action)
            },
            onFavoriteClick = { article ->
                viewModel.toggleFavorite(article)
            }
        )

        binding.rvArticles.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.allArticles.observe(viewLifecycleOwner) { articles ->
            // ĐÃ XÓA: Lệnh alpha = 1.0f (Không cần thiết vì màn hình luôn sáng)

            articleAdapter.submitList(articles) {
                if (articles.isNotEmpty()) {
                    binding.rvArticles.scrollToPosition(0)
                }
            }

            binding.llEmptyState.visibility = if (articles.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Chỉ hiện ProgressBar khi danh sách đang trống để báo hiệu đang tải lần đầu
            binding.progressBar.visibility = if (isLoading && articleAdapter.itemCount == 0)
                View.VISIBLE else View.GONE
            binding.swipeRefresh.isRefreshing = isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_menu, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.queryHint = "Tìm kiếm tin tức..."

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (!query.isNullOrBlank()) {
                            // ĐÃ XÓA: Dòng lệnh làm mờ alpha
                            viewModel.searchNews(query)
                            searchView.clearFocus()
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText.isNullOrBlank()) {
                            viewModel.refreshNews()
                        }
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}