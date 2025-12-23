package com.example.caps_project.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caps_project.Constant
import com.example.caps_project.RecordDetailActivity
import com.example.caps_project.databinding.ActivityHistoryItemBinding
import com.example.caps_project.models.responses.HistoryList


class SummaryHistoryAdapter(private val list: List<HistoryList>) : RecyclerView.Adapter<SummaryHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ActivityHistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun binding(item: HistoryList) {
            binding.historyItemSummaryIconBody.setOnClickListener(null)

            if (item.dcm_id == -1 && item.ingre_num == -1 && item.level == -1) {
                binding.historyItemName.text = item.image
            } else {
                binding.historyItemName.text = Constant.Code2Name.get(item.ingre_num)!!

                binding.historyItemSummaryIconBody.setOnClickListener {
                    val intent = Intent(binding.root.context, RecordDetailActivity::class.java)
                    intent.putExtra("ingredient_name", Constant.Code2Name.get(item.ingre_num)!!)
                    intent.putExtra("discernment_date", item.dcm_date)
                    intent.putExtra("freshness", Constant.Level2Name.get(item.level)!!)
                    intent.putExtra("discernment_id", item.dcm_id)
                    intent.putExtra("image", item.image)
                    binding.root.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ActivityHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding(list[position])
    }

    override fun getItemCount(): Int = list.size
}