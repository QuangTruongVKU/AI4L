package com.example.app.adapter


import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app.databinding.ResultItemBinding


class ResultAdapter(
    private val data: List<Pair<String, String>>
) : RecyclerView.Adapter<ResultAdapter.ResultVỉewsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultVỉewsHolder {
        val binding = ResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultVỉewsHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultVỉewsHolder, position: Int) {
        holder.bind(
            data[position].first,
            data[position].second
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ResultVỉewsHolder(private val binding: ResultItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String, description: String) {
            binding.textTitle.text = title // Hiển thị tiêu đề
            binding.textDescribe.text = Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT) // Hiển thị mô tả

        }
    }
}