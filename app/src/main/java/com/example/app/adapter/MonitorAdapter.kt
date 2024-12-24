package com.example.app.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app.MonitorView
import com.example.app.R

class MonitorAdapter(private val monitorList: List<Monitor>) :
    RecyclerView.Adapter<MonitorAdapter.MonitorViewHolder>() {

    data class Monitor(val name: String, val ip: String, val port: String)

    class MonitorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.monitorName)
        val ipTextView: TextView = view.findViewById(R.id.monitorIp)
        val portTextView: TextView = view.findViewById(R.id.monitorPort)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monitor, parent, false)
        return MonitorViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonitorViewHolder, position: Int) {
        val monitor = monitorList[position]
        holder.nameTextView.text = monitor.name
        holder.ipTextView.text = monitor.ip
        holder.portTextView.text = monitor.port

        // Xử lý sự kiện khi người dùng nhấn vào item
        holder.itemView.setOnClickListener {
            // Xử lý khi người dùng nhấn vào item
            val intent = Intent(holder.itemView.context, MonitorView::class.java)
            intent.apply {
                putExtra("monitorName", monitor.name)
                putExtra("monitorIp", monitor.ip)
                putExtra("monitorPort", monitor.port)
            }

            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = monitorList.size
}
