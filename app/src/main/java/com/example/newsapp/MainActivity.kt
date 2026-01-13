package com.example.newsapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.data.local.NewsDatabase
import com.example.newsapp.data.remote.RetrofitClient
import com.example.newsapp.data.repository.NewsRepository
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.viewmodel.NewsViewModel
import com.example.newsapp.viewmodel.NewsViewModelFactory

class MainActivity : AppCompatActivity() {
    // Biến public để các Fragment có thể truy cập qua (activity as MainActivity).viewModel
    lateinit var viewModel: NewsViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Bật chế độ hiển thị tràn viền (Edge-to-Edge)
        enableEdgeToEdge()

        // 2. Sử dụng ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- BỔ SUNG: KHỞI TẠO VIEWMODEL ---
        val articleDao = NewsDatabase.getDatabase(this).getArticleDao()
        val repository = NewsRepository(articleDao, RetrofitClient.apiService)
        val viewModelFactory = NewsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[NewsViewModel::class.java]
        // ---------------------------------

        // 3. Xử lý WindowInsets (Tránh lấp nội dung bởi thanh hệ thống)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 4. Thiết lập Navigation Component
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Thiết lập Toolbar thay thế cho ActionBar mặc định
        setSupportActionBar(binding.toolbar)

        // Cập nhật Set các trang "Cấp cao nhất"
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.articleListFragment,
                R.id.favoriteFragment,
                R.id.nav_business,
                R.id.nav_tech
            ),
            binding.main
        )

        // Kết nối NavController với ActionBar và Drawer
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Kết nối NavigationView để tự động chuyển Fragment khi chọn Menu
        binding.navView.setupWithNavController(navController)
    }

    // 5. Xử lý nút Back hoặc nút Menu trên Toolbar
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}