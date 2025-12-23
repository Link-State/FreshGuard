package com.example.caps_project

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caps_project.databinding.ActivityRecentBinding
import com.example.caps_project.models.requests.RequestLoadHistory
import com.example.caps_project.models.responses.HistoryList
import com.example.caps_project.models.responses.ResponseLoadHistory
import com.example.caps_project.recyclerview.RecordListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecentActivity : AppCompatActivity() {

//    private val records = mutableListOf(
//        Record("사과", "2025.10.03", "48%", 486),
//        Record("오렌지", "2025.10.03", "100%", 1004),
//        Record("사과", "2025.10.03", "108%", 1087),
//        Record("바나나", "2025.10.03", "80%", 999)
//    )

    private lateinit var binding: ActivityRecentBinding

    override fun onResume() {
        super.onResume()

        val input = RequestLoadHistory(Session.userId)
        var retryCount = 0
        RetrofitBuilder.api.loadHistory(input).enqueue(object : Callback<ResponseLoadHistory> {
            override fun onResponse(call: Call<ResponseLoadHistory>, response: Response<ResponseLoadHistory>) {
                if (response.isSuccessful) {
                    val history = response.body()?.history!!
                    val sorted_history:List<HistoryList> = history.sortedWith( compareBy(
                        {
                            - ((it.dcm_date.subSequence(0, 4).toString().toInt() - 1970).toBigInteger() * 365.toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger()
                                    + it.dcm_date.subSequence(5, 7).toString().toInt().toBigInteger() * 31.toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger()
                                    + it.dcm_date.subSequence(8,10).toString().toInt().toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger())
                        },
                        { -(it.dcm_id) }
                    ))
                    updateRecordList(sorted_history)
                }
            }

            override fun onFailure(call: Call<ResponseLoadHistory>, t: Throwable) {
                retryCount += 1
                if (retryCount <= RetrofitBuilder.retry) {
                    call.clone().enqueue(this)
                }
            }
        })

//        val listView = findViewById<ListView>(R.id.list_recent)
//
//        val adapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_list_item_2,
//            android.R.id.text1,
//            records.map { "${it.name}  (${it.date})\n신선도: ${it.freshness} | 번호: ${it.number}" }
//        )
//        listView.adapter = adapter

        // (22) 항목 클릭 시 기록상세로 이동
//        listView.setOnItemClickListener { _, _, position, _ ->
//            val record = records[position]
//            val intent = Intent(this, RecordDetailActivity::class.java)
//            intent.putExtra("name", record.name)
//            intent.putExtra("date", record.date)
//            intent.putExtra("freshness", record.freshness)
//            intent.putExtra("number", record.number)
//            startActivity(intent)
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun updateRecordList(list: List<HistoryList>) {
        val adapter = RecordListAdapter(list)
        binding.listRecent.adapter = adapter
        binding.listRecent.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }
}
