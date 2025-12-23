package com.example.caps_project.recyclerview

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caps_project.Constant
import com.example.caps_project.DetailActivity
import com.example.caps_project.databinding.ActivityDetailStoredItemBinding
import com.example.caps_project.models.responses.IngredientList
import androidx.core.graphics.toColorInt
import com.example.caps_project.R

class DetailItemAdapter(private val list:List<IngredientList>) : RecyclerView.Adapter<DetailItemAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val binding: ActivityDetailStoredItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun binding(item: IngredientList) {
            binding.detailViewMain.setOnClickListener(null)
            Glide.with(binding.root.context).clear(binding.detailViewImage)

            when (item.level) {
                3 -> {
                    binding.detailItemDotAlert.setBackgroundResource(R.drawable.dot_green)
                    binding.tvDaysLeftItem1.setTextColor("#7cb342".toColorInt())
                }
                2 -> {
                    binding.detailItemDotAlert.setBackgroundResource(R.drawable.dot_yellow)
                    binding.tvDaysLeftItem1.setTextColor("#ffcc32".toColorInt())
                }
                1 -> {
                    binding.detailItemDotAlert.setBackgroundResource(R.drawable.dot_red)
                    binding.tvDaysLeftItem1.setTextColor("#e94033".toColorInt())
                }
            }

            binding.detailViewMain.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra("MODE", 2)
                intent.putExtra("ingredient_id", item.id)
                intent.putExtra("name", Constant.Code2Name.get(item.ingre_num)!!)
                intent.putExtra("expire", item.expire)
                intent.putExtra("freshness", Constant.Level2Name.get(item.level)!!)
                intent.putExtra("date", item.created)
                intent.putExtra("image", item.image)
                binding.root.context.startActivity(intent)
            }

            Glide.with(binding.root.context).load(item.image).into(binding.detailViewImage)
            binding.detailViewName.text = Constant.Code2Name.get(item.ingre_num)!!
            binding.detailViewExpire.text = item.expire
            binding.tvDaysLeftItem1.text = Constant.Level2Name.get(item.level)!!
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ActivityDetailStoredItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}