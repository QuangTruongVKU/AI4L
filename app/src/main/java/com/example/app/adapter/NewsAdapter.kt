package com.example.app.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app.databinding.NewsItemBinding
import com.squareup.picasso.Picasso

class NewsAdapter(
    private var items: List<String>, // Tiêu đề
    private var descriptions: List<String>, // Mô tả
    private var images: List<String>, // URL hình ảnh
    private var links: List<String> // Link bài viết
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(
            title = items[position],
            description = descriptions[position],
            imageUrl = images[position],
            link = links[position]
        )
    }

    override fun getItemCount(): Int = items.size

    // Hàm cập nhật dữ liệu mà không khởi tạo lại adapter
    fun updateData(newItems: List<String>, newDescriptions: List<String>, newImages: List<String>, newLinks: List<String>) {
        items = newItems
        descriptions = newDescriptions
        images = newImages
        links = newLinks
        notifyDataSetChanged() // Cập nhật RecyclerView
    }

    class NewsViewHolder(private val binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String, description: String, imageUrl: String, link: String) {
            binding.textTitel.text = title // Hiển thị tiêu đề
            binding.textView.text = description // Hiển thị mô tả

            // Xử lý ảnh với Picasso và ảnh mặc định khi lỗi
            Picasso.get()
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_report_image) // Ảnh thay thế khi tải chậm
                .error(android.R.drawable.ic_menu_report_image) // Ảnh thay thế khi lỗi
                .into(binding.imageView2)

            // Mở link khi nhấn vào item
            binding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                binding.root.context.startActivity(intent)
            }
        }
    }
}
