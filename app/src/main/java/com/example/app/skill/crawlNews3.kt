package com.example.app.skill

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup

class CrawlNews3 {

    // Hàm crawl tin tức, chỉ lấy 3 bài đầu tiên
    fun getTopNews(): MutableList<NewsItem> {
        val url = "https://nongnghiep.vn/gà-search/from-to-sign-/" // URL trang web tin tức
        val topNews = mutableListOf<NewsItem>()

        try {
            // Kết nối và lấy nội dung trang web
            val document = Jsoup.connect(url).get()

            // Lọc các bài viết (Thay đổi selector theo trang web cụ thể)
            val articles = document.select("li.news-home-item") // Chọn bài viết theo class

            // Lấy 3 bài viết đầu tiên
            for (article in articles.take(3)) {
                // Lấy tiêu đề bài viết từ thuộc tính "title" của thẻ <a>
                val title = article.select("div.news-info h3 a").text()

                // Lấy đường dẫn bài viết từ thuộc tính "href" của thẻ <a> trong class "tieude"
                val link = article.select("div.news-info h3 a").attr("href")

                // Xử lý link để đảm bảo là link đầy đủ
                val fullLink = if (link.startsWith("http")) link else "https://nhachannuoi.vn/$link"

                // Lấy phần mô tả bài viết từ thẻ <div> có class "nd-ngan"
                val describe = article.select("p.main-intro").text()

                // Lấy đường dẫn ảnh từ thuộc tính "src" của thẻ <img> bên trong thẻ <a>
                val image = article.select("img").attr("src")

                // Thêm bài viết vào danh sách
                topNews.add(NewsItem(title, fullLink, describe, image))
            }
        } catch (e: Exception) {
            // Xử lý lỗi khi không thể kết nối hoặc parse nội dung trang web
            println("Lỗi khi crawl: ${e.message}")
        }

        return topNews
    }
}
fun main() = runBlocking {
    val crawler = CrawlNews3()

    while (true) {
        // Lấy 3 bài viết mới nhất
        val news = crawler.getTopNews()

        // Hiển thị kết quả
        if (news.isNotEmpty()) {
            println("3 bài viết mới nhất:")
            news.forEach {
                println("Title: ${it.title}") // Hiển thị tiêu đề bài viết
                println("Description: ${it.description}") // Hiển thị mô tả bài viết
                println("Image: ${it.image}") // Hiển thị link ảnh minh họa
                println("Link: ${it.link}") // Hiển thị link bài viết
                println("-----")
            }
        } else {
            println("Không có bài viết nào.")
        }

        // Chờ 5 phút trước khi crawl lại
        delay(5 * 60 * 1000)
    }
}

