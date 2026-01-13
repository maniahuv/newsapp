package com.example.newsapp.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleDetailBinding
import com.example.newsapp.viewmodel.NewsViewModel
import java.util.Locale

/**
 * Fragment hiển thị chi tiết bài báo.
 * Hỗ trợ: Đọc báo (TTS), Lưu bài báo Offline (Jsoup), Chỉnh tốc độ đọc, Chia sẻ.
 */
class ArticleDetailFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ArticleDetailFragmentArgs by navArgs()
    private lateinit var viewModel: NewsViewModel

    private var tts: TextToSpeech? = null
    private var speechRate = 1.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        val article = args.article

        // 1. Khởi tạo TTS - Vô hiệu hóa nhẹ nút bấm để chờ máy đọc sẵn sàng
        _binding?.let {
            it.fabTTS.isEnabled = false
            it.fabTTS.alpha = 0.6f
        }

        // Khởi tạo TTS engine
        tts = TextToSpeech(requireContext().applicationContext, this)

        setupUI(article)

        // 2. Quan sát trạng thái Loading (Cào dữ liệu Jsoup)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            _binding?.progressBarDetail?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 3. Quan sát lỗi
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        }

        // 4. Xử lý nút Yêu thích (Lưu bài báo để đọc Offline)
        binding.fabFavorite.setOnClickListener {
            viewModel.toggleFavorite(article)
            article.isFavorite = !article.isFavorite
            updateFavoriteIcon(article.isFavorite)
        }

        // 5. Nút Đọc báo (TTS)
        binding.fabTTS.setOnClickListener {
            speakArticle(article)
        }

        // 6. Slider điều chỉnh tốc độ đọc
        binding.sliderSpeed.addOnChangeListener { _, value, _ ->
            speechRate = value
            binding.tvSpeedLabel.text = "${value}x"
            tts?.setSpeechRate(speechRate)
        }

        // 7. BỔ SUNG: Xử lý nút Chia sẻ
        binding.fabShare.setOnClickListener {
            shareArticle(article)
        }
    }

    private fun setupUI(article: com.example.newsapp.data.local.Article) {
        binding.apply {
            tvDetailTitle.text = article.title
            tvDetailAuthor.text = "Tác giả: ${article.author ?: "Ẩn danh"}"
            tvDetailPublishedAt.text = " • ${article.publishedAt}"

            if (!article.savedFullContent.isNullOrEmpty()) {
                tvDetailDescription.visibility = View.GONE
                tvDetailContent.text = article.savedFullContent
            } else {
                tvDetailDescription.visibility = View.VISIBLE
                tvDetailDescription.text = article.description
                tvDetailContent.text = article.content ?: "Nhấn trái tim để tải nội dung đầy đủ (Cần mạng)."
            }

            updateFavoriteIcon(article.isFavorite)

            Glide.with(this@ArticleDetailFragment)
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivDetailImage)

            btnOpenBrowser.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                startActivity(intent)
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val icon = if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        binding.fabFavorite.setImageResource(icon)
    }

    /**
     * BỔ SUNG: Hàm thực hiện chia sẻ bài báo qua Intent
     */
    private fun shareArticle(article: com.example.newsapp.data.local.Article) {
        val shareBody = """
            ${article.title}
            
            Xem chi tiết tại: ${article.url}
            
            (Chia sẻ từ ứng dụng NewsApp)
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, article.title)
            putExtra(Intent.EXTRA_TEXT, shareBody)
        }

        startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài báo qua:"))
    }

    private fun speakArticle(article: com.example.newsapp.data.local.Article) {
        val textToSpeak = article.savedFullContent ?: article.description ?: article.title

        if (!textToSpeak.isNullOrEmpty()) {
            tts?.setSpeechRate(speechRate)
            val maxCharLimit = 3500

            if (textToSpeak.length > maxCharLimit) {
                val chunks = textToSpeak.chunked(maxCharLimit)
                tts?.speak(chunks[0], TextToSpeech.QUEUE_FLUSH, null, "chunk_0")
                for (i in 1 until chunks.size) {
                    tts?.speak(chunks[i], TextToSpeech.QUEUE_ADD, null, "chunk_$i")
                }
                Toast.makeText(context, "Đang chuẩn bị danh sách phát bài báo dài...", Toast.LENGTH_SHORT).show()
            } else {
                tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "ArticleTTS")
            }
        }
    }

    override fun onInit(status: Int) {
        val currentBinding = _binding ?: return

        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("vi", "VN"))

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }

            tts?.setSpeechRate(speechRate)

            currentBinding.fabTTS.isEnabled = true
            currentBinding.fabTTS.alpha = 1.0f
        } else {
            context?.let {
                Toast.makeText(it, "Không thể khởi động bộ đọc giọng nói", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroyView()
        _binding = null
    }
}