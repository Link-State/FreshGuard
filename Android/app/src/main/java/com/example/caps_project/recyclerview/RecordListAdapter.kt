package com.example.caps_project.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caps_project.Constant
import com.example.caps_project.RecordDetailActivity
import com.example.caps_project.databinding.ActivityRecordSummaryItemBinding
import com.example.caps_project.models.responses.HistoryList

class RecordListAdapter(private val list: List<HistoryList>) : RecyclerView.Adapter<RecordListAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: ActivityRecordSummaryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun binding(item: HistoryList) {
            binding.discernmentBody.setOnClickListener(null)

            binding.discernmentBody.setOnClickListener {
                val intent = Intent(binding.root.context, RecordDetailActivity::class.java)

                intent.putExtra("discernment_id", item.dcm_id)
                intent.putExtra("ingredient_name", Constant.Code2Name.get(item.ingre_num))
                intent.putExtra("freshness", Constant.Level2Name.get(item.level))
                intent.putExtra("discernment_date", item.dcm_date)
                intent.putExtra("image", item.image)
                binding.root.context.startActivity(intent)
            }

            binding.discernmentFreshnessImoji.text = Constant.Level2Emoji.get(item.level)
            binding.discernmentName.text = Constant.Code2Name.get(item.ingre_num)
            Glide.with(binding.root.context).load(item.image).into(binding.discernmentImage)
            binding.discernmentDate.text = item.dcm_date
            binding.discernmentId.text = item.dcm_id.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordListAdapter.MyViewHolder {
        val binding = ActivityRecordSummaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordListAdapter.MyViewHolder, position: Int) {
        holder.binding(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}