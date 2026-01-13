package com.example.newsapp.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.MainActivity
import com.example.newsapp.databinding.FragmentFavoriteBinding
import com.example.newsapp.ui.list.ArticleAdapter

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        val viewModel = (activity as MainActivity).viewModel

        viewModel.favoriteArticles.observe(viewLifecycleOwner) { articles ->
            if (articles.isNullOrEmpty()) {
                binding.textFavorite.visibility = View.VISIBLE
                binding.recyclerViewFavorite.visibility = View.GONE
                articleAdapter.submitList(emptyList())
            } else {
                binding.textFavorite.visibility = View.GONE
                binding.recyclerViewFavorite.visibility = View.VISIBLE
                articleAdapter.submitList(articles)
            }
        }
    }

    private fun setupRecyclerView() {
        // Lấy viewModel để sử dụng trong callback
        val viewModel = (activity as MainActivity).viewModel

        // CẬP NHẬT: Truyền cả 2 tham số onItemClick và onFavoriteClick
        articleAdapter = ArticleAdapter(
            onItemClick = { article ->
                val action = FavoriteFragmentDirections.actionFavoriteFragmentToArticleDetailFragment(article)
                findNavController().navigate(action)
            },
            onFavoriteClick = { article ->
                // Gọi hàm toggleFavorite trong ViewModel để xóa khỏi mục yêu thích
                viewModel.toggleFavorite(article)
            }
        )

        binding.recyclerViewFavorite.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}