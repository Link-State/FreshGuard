package com.example.caps_project

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.caps_project.databinding.ActivityRecordDetailBinding

class RecordDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityRecordDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tvName = findViewById<TextView>(R.id.tv_name)
        val tvDate = findViewById<TextView>(R.id.tv_date)
        val tvFreshness = findViewById<TextView>(R.id.tv_freshness)
        val tvNumber = findViewById<TextView>(R.id.tv_number)
        val btnBack = findViewById<Button>(R.id.btn_back)

        // B4에서 전달된 데이터 표시
        tvName.text = intent.getStringExtra("ingredient_name")
        tvDate.text = intent.getStringExtra("discernment_date")
        val freshness = intent.getStringExtra("freshness")
        val freshnessContext = "${Constant.Name2Emoji.get(freshness)} - $freshness"
        tvFreshness.text = freshnessContext
        tvNumber.text = intent.getIntExtra("discernment_id", -1).toString()
        Glide.with(this).load(intent.getStringExtra("image")).into(binding.imgRecord)

        // (999) 뒤로가기 버튼
        btnBack.setOnClickListener {
            finish()
        }
    }
}
