package com.example.app.Fragment

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.adapter.NewsAdapter
import com.example.app.database.NewsDataHelper
import com.example.app.databinding.FragmentHomeBinding
import com.example.app.skill.CrawlNews
import com.example.app.skill.CrawlNews2
import com.example.app.skill.CrawlNews3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var Crawler: CrawlNews
    private lateinit var Crawler2: CrawlNews2
    private lateinit var Crawler3: CrawlNews3
    private lateinit var binding: FragmentHomeBinding
    private lateinit var dbHelper: NewsDataHelper
    private var currentIndex = 0 // Chỉ mục bắt đầu
    private val pageSize = 3     // Số mục hiển thị mỗi lần

    private val titles = mutableListOf<String>()
    private val descriptions = mutableListOf<String>()
    private val images = mutableListOf<String>()
    private val links = mutableListOf<String>()

    private var recyclerViewState: Parcelable? = null // Lưu trạng thái RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = NewsDataHelper(requireContext())

        // Nếu chưa có dữ liệu, load từ database
        if (titles.isEmpty()) {
            lifecycleScope.launch {
                fetchAndStoreNews()
                loadDataFromDatabase()
                updateRecyclerView()
            }
        } else {
            updateRecyclerView() // Dùng lại dữ liệu đã có
        }

        setupButtons()
    }

    // Hàm crawl tin tức và lưu vào database (chỉ chạy một lần)
    private suspend fun fetchAndStoreNews() {
        try {
            Crawler = CrawlNews()
            Crawler2 = CrawlNews2()
            Crawler3 = CrawlNews3()

            val news1 = withContext(Dispatchers.IO) { Crawler.getTopNews() }
            val news2 = withContext(Dispatchers.IO) { Crawler2.getTopNews() }
            val news3 = withContext(Dispatchers.IO) { Crawler3.getTopNews() }
            val news = news1 + news2 + news3

            for (new in news) {
                if (!dbHelper.isNewsExists(new.title)) {
                    dbHelper.insertNews(new.title, new.image, new.description, new.link)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Load dữ liệu từ database
    private fun loadDataFromDatabase() {
        val newsList = dbHelper.getAllNews()
        titles.clear()
        descriptions.clear()
        images.clear()
        links.clear()

        for (news in newsList) {
            titles.add(news.title)
            descriptions.add(news.description)
            images.add(news.image)
            links.add(news.link)
        }
    }

    // Cập nhật RecyclerView
    private fun updateRecyclerView() {
        val limitedTitles = titles.drop(currentIndex).take(pageSize)
        val limitedDescriptions = descriptions.drop(currentIndex).take(pageSize)
        val limitedImages = images.drop(currentIndex).take(pageSize)
        val limitedLinks = links.drop(currentIndex).take(pageSize)

        val adapter = NewsAdapter(limitedTitles, limitedDescriptions, limitedImages, limitedLinks)
        binding.NewsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.NewsRecycler.adapter = adapter

        // Khôi phục trạng thái RecyclerView
        recyclerViewState?.let {
            binding.NewsRecycler.layoutManager?.onRestoreInstanceState(it)
        }
    }

    // Setup nút Next và Previous
    private fun setupButtons() {
        binding.btnPrevious.isEnabled = false

        binding.btnNext.setOnClickListener {
            if (currentIndex + pageSize < titles.size) {
                currentIndex += pageSize
                updateRecyclerView()
                binding.btnPrevious.isEnabled = true
                if (currentIndex + pageSize >= titles.size) binding.btnNext.isEnabled = false
            }
        }

        binding.btnPrevious.setOnClickListener {
            if (currentIndex - pageSize >= 0) {
                currentIndex -= pageSize
                updateRecyclerView()
                binding.btnNext.isEnabled = true
                if (currentIndex - pageSize < 0) binding.btnPrevious.isEnabled = false
            }
        }
    }

    // Lưu trạng thái RecyclerView
    override fun onPause() {
        super.onPause()
        recyclerViewState = binding.NewsRecycler.layoutManager?.onSaveInstanceState()
    }

    // Khôi phục trạng thái RecyclerView
    override fun onResume() {
        super.onResume()
        recyclerViewState?.let {
            binding.NewsRecycler.layoutManager?.onRestoreInstanceState(it)
        }
    }
}
