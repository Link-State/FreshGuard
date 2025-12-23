package com.example.caps_project.recyclerview

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caps_project.B10_RecipeDetailActivity
import com.example.caps_project.databinding.ActivityRecommendRecipeListBinding
import com.example.caps_project.models.responses.RecommendRecipeList
import androidx.core.graphics.toColorInt

class RecommendRecipeAdapter(private val list:List<RecommendRecipeList>) : RecyclerView.Adapter<RecommendRecipeAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: ActivityRecommendRecipeListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun binding(item: RecommendRecipeList) {
            binding.tvRecipe.setOnClickListener(null)

            if (item.id == -1 && item.sequence == -1 && item.saved == false) {
                binding.tvRecipe.text = item.name
            } else {
                val title = "${item.sequence}. ${item.name}"
                binding.tvRecipe.text = title

                if(item.saved) {
                    binding.tvRecipe.setTextColor("#8B8B8B".toColorInt())
                } else {
                    binding.tvRecipe.setTextColor("#006BCF".toColorInt())
                }

                binding.tvRecipe.setOnClickListener {
                    val intent = Intent(binding.root.context, B10_RecipeDetailActivity::class.java)
                    intent.putExtra("name", item.name)
                    intent.putExtra("seq", item.sequence)
                    intent.putExtra("saved", item.saved)
                    intent.putExtra("id", item.id)
                    binding.root.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendRecipeAdapter.MyViewHolder {
        val binding = ActivityRecommendRecipeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendRecipeAdapter.MyViewHolder, position: Int) {
        holder.binding(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}