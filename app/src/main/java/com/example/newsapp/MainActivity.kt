package com.example.newsapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.newsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Bật chế độ hiển thị tràn viền (Edge-to-Edge)
        enableEdgeToEdge()

        // 2. Sử dụng ViewBinding để quản lý giao diện hiệu quả hơn
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Xử lý WindowInsets để nội dung không bị lấp bởi Thanh hệ thống (Status bar, Navigation bar)
        // Lưu ý: Đảm bảo id trong layout activity_main.xml của bạn là 'main'
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 4. Thiết lập Navigation Component (Nếu bạn sử dụng Toolbar/ActionBar)
        setupNavigation()
    }

    private fun setupNavigation() {
        // Tìm NavHostFragment từ layout
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        val navController = navHostFragment?.navController

        // Nếu bạn có sử dụng Toolbar trong layout, hãy thiết lập nó ở đây
        // navController?.let { setupActionBarWithNavController(it) }
    }

    // Xử lý nút Back trên Toolbar (Nếu có)
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        return navHostFragment?.navController?.navigateUp() ?: super.onSupportNavigateUp()
    }
}