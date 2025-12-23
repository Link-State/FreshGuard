package com.example.caps_project.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caps_project.databinding.ActivityRecipeGuideBinding
import com.example.caps_project.models.responses.RecipeGuideList

class RecipeGuideAdapter(private val list:List<RecipeGuideList>) : RecyclerView.Adapter<RecipeGuideAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: ActivityRecipeGuideBinding) : RecyclerView.ViewHolder(binding.root) {
        fun binding(item: RecipeGuideList) {
            binding.recipeGuideText.text = item.text
            Glide.with(binding.root.context).load(item.image).into(binding.recipeGuideImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeGuideAdapter.MyViewHolder {
        val binding = ActivityRecipeGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeGuideAdapter.MyViewHolder, position: Int) {
        holder.binding(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}