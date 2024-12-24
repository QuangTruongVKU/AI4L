package com.example.app.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.adapter.MonitorAdapter
import com.example.app.database.MonitorsHelper

class MonitorFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var monitorsHelper: MonitorsHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_monitor, container, false)

        val btnAddMonitor = view.findViewById<ImageButton>(R.id.btnAddMonitor)
        btnAddMonitor.setOnClickListener {
            // Xử lý khi người dùng nhấn nút "Add Monitor"
            val ip = "192.168.1.6"
            val port = "4747"
            val name = "Monitor 1"
            val idUser = 1
            monitorsHelper.insertMonitor(name, ip, port, idUser)

        }

        recyclerView = view.findViewById(R.id.recyclerViewMonitors)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Lấy ID người dùng từ SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", -1)
        println(userId)// Giá trị mặc định là -1 nếu không tìm thấy

        val allPrefs = sharedPreferences.all
        println(allPrefs)

        if (userId == -1) {
            // Hiển thị thông báo lỗi nếu userId không hợp lệ
            return view
        }

        monitorsHelper = MonitorsHelper(requireContext())
        val monitorList = monitorsHelper.getMonitorsByUser(userId).map {
            MonitorAdapter.Monitor(
                name = it["name"] ?: "N/A",
                ip = it["ip"] ?: "N/A",
                port = it["port"] ?: "N/A"
            )
        }

        recyclerView.adapter = MonitorAdapter(monitorList)
        return view
    }
}
