package com.example.caps_project.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caps_project.IngredientResult
import com.example.caps_project.Session
import com.example.caps_project.databinding.ActivityResultBinding

class ViewPagerAdapter(private val list:ArrayList<IngredientResult>): RecyclerView.Adapter<ViewPagerAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: ActivityResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindSlider(item:IngredientResult, pos:Int) {
            binding.cbRegister.setOnCheckedChangeListener(null)
            Glide.with(binding.root.context).clear(binding.ivResultImage)

            if (Session.save.containsKey(pos)) {
                binding.cbRegister.isChecked = true
            } else {
                binding.cbRegister.isChecked = false
            }

            if (item.dcm_id == "" && item.name == "" && item.date == "" && item.freshness == "" && item.freshnessEmoji == "" && item.image == "") {
                binding.cbRegister.contentDescription = "판별 불가"
                binding.cbRegister.visibility = View.GONE
                binding.cbRegister.isChecked = false

                val page = "( ${pos+1} / ${list.size} )"
                binding.tvPageIndicator.text = page
                binding.tvResultName.text = " 판별 불가 "
                binding.tvResultDate.text = " ─ "
                binding.tvResultFreshness.text = " ─ "
            } else {
                binding.cbRegister.contentDescription = ""
                binding.cbRegister.visibility = View.VISIBLE

                binding.cbRegister.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        Session.save.put(pos, item)
                    } else {
                        Session.save.remove(pos)
                    }
                }
                val page = "( ${pos+1} / ${list.size} )"
                binding.tvPageIndicator.text = page
                Glide.with(binding.root.context).load(item.image).into(binding.ivResultImage)
                binding.tvResultName.text = item.name
                binding.tvResultDate.text = item.date
                binding.tvResultFreshness.text = item.freshness
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ActivityResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindSlider(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}