package com.example.newsapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

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

        // 3. Xử lý WindowInsets (Tránh lấp nội dung bởi thanh hệ thống)
        // Lưu ý: ID 'main' phải khớp với ID của DrawerLayout trong activity_main.xml
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

        // Cập nhật Set các trang "Cấp cao nhất" (Top-level destinations)
        // Những trang này sẽ hiện nút Menu thay vì nút Back.
        // ID phải trùng khớp hoàn toàn với nav_graph.xml và activity_main_drawer.xml
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