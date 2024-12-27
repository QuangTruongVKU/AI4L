package com.example.app.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.service.autofill.UserData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.adapter.MonitorAdapter
import com.example.app.database.DataHelper
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
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        btnAddMonitor.setOnClickListener {
            // Xử lý khi người dùng nhấn nút "Add Monitor"
            val name = view.findViewById<EditText>(R.id.editNameCamera).text.toString()
            val ip = view.findViewById<EditText>(R.id.editIPCamera).text.toString()
            val port = view.findViewById<EditText>(R.id.editHostCamera).text.toString()
            val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val idUser = sharedPreferences.getInt("id", -1)
            monitorsHelper.insertMonitor(name, ip, port, idUser)

            val monitorList = monitorsHelper.getMonitorsByUser(idUser).map {
                MonitorAdapter.Monitor(
                    name = it["name"] ?: "N/A",
                    ip = it["ip"] ?: "N/A",
                    port = it["port"] ?: "N/A"
                )

            }
            recyclerView.adapter = MonitorAdapter(monitorList)

//            set lại name ip port
            view.findViewById<EditText>(R.id.editNameCamera).setText("")
            view.findViewById<EditText>(R.id.editIPCamera).setText("")
            view.findViewById<EditText>(R.id.editHostCamera).setText("")


        }
        btnDelete.setOnClickListener {
            val name = view.findViewById<EditText>(R.id.editNameCamera).text.toString()
            val ip = view.findViewById<EditText>(R.id.editIPCamera).text.toString()
            val port = view.findViewById<EditText>(R.id.editHostCamera).text.toString()
            val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val idUser = sharedPreferences.getInt("id", -1)

            if (name.isBlank() || ip.isBlank() || port.isBlank()) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (idUser == -1) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rowsDeleted = monitorsHelper.deleteMonitor(name, ip, port, idUser)
            if (rowsDeleted > 0) {
                Toast.makeText(requireContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show()
                val monitorList = monitorsHelper.getMonitorsByUser(idUser).map {
                    MonitorAdapter.Monitor(
                        name = it["name"] ?: "N/A",
                        ip = it["ip"] ?: "N/A",
                        port = it["port"] ?: "N/A"
                    )
                }

                if (monitorList.isEmpty()) {
                    Toast.makeText(requireContext(), "Không còn monitor nào!", Toast.LENGTH_SHORT).show()
                } else {
                    recyclerView.adapter = MonitorAdapter(monitorList)
                    view.findViewById<EditText>(R.id.editNameCamera).setText("")
                    view.findViewById<EditText>(R.id.editIPCamera).setText("")
                    view.findViewById<EditText>(R.id.editHostCamera).setText("")
                }
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy monitor để xóa!", Toast.LENGTH_SHORT).show()
            }
        }


        recyclerView = view.findViewById(R.id.recyclerViewMonitors)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Lấy ID người dùng từ SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", -1)
        println(userId)// Giá trị mặc định là -1 nếu không tìm thấy

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
