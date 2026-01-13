plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Sử dụng id trực tiếp để tránh lỗi nếu Version Catalog (libs.versions.toml) chưa có kapt
    id("kotlin-kapt")
}

android {
    namespace = "com.example.newsapp"
    compileSdk = 36 // Chỉnh về 35 để ổn định hơn (36 là bản preview)

    defaultConfig {
        applicationId = "com.example.newsapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true // Bật ViewBinding cho XML
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17" // Khuyên dùng Java 17 cho Android Studio bản mới
    }
}

dependencies {
    // Core & UI cơ bản
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)

    // Sửa lỗi báo đỏ ConstraintLayout bằng cách dùng thư viện cụ thể
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // RecyclerView & SwipeRefreshLayout (BẮT BUỘC cho màn hình danh sách)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")

    // Room Database (Lưu dữ liệu Offline)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion") // Hỗ trợ Coroutines trong Room

    // Retrofit & Gson (Lấy dữ liệu Online)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Coroutines & Lifecycle (MVVM)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.fragment:fragment-ktx:1.8.5") // Hỗ trợ khởi tạo ViewModel nhanh

    // Navigation (Chuyển màn hình)
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.3")

    // Glide (Tải ảnh từ URL)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}