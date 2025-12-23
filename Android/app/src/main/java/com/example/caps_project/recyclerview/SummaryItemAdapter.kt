package com.example.caps_project.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caps_project.Constant
import com.example.caps_project.DetailActivity
import com.example.caps_project.databinding.ActivityStoredItemBinding
import com.example.caps_project.models.responses.IngredientList


class SummaryItemAdapter(private val list: List<IngredientList>) : RecyclerView.Adapter<SummaryItemAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ActivityStoredItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun binding(item: IngredientList) {
            binding.storedItemFreshnessEmoji.text = ""
            binding.storedItemSummaryIconBody.setOnClickListener(null)

            if (item.level == -1 && item.ingre_num == -1 && item.id == -1) {
                binding.storedItemName.text = item.image
            } else {
                binding.storedItemFreshnessEmoji.text = Constant.Level2Emoji.get(item.level)
                binding.storedItemName.text = Constant.Code2Name.get(item.ingre_num)!!

                binding.storedItemSummaryIconBody.setOnClickListener {
                    val intent = Intent(binding.root.context, DetailActivity::class.java)
                    // MODE : 1 = 추가, 2 = 수정
                    intent.putExtra("MODE", 2)
                    intent.putExtra("ingredient_id", item.id)
                    intent.putExtra("name", Constant.Code2Name.get(item.ingre_num)!!)
                    intent.putExtra("expire", item.expire)
                    intent.putExtra("freshness", Constant.Level2Name.get(item.level)!!)
                    intent.putExtra("date", item.created)
                    intent.putExtra("image", item.image)
                    binding.root.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ActivityStoredItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding(list[position])
    }

    override fun getItemCount(): Int = list.size
}