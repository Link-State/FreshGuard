package com.example.caps_project.recyclerview

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caps_project.B10_RecipeDetailActivity
import com.example.caps_project.databinding.ActivityStoredRecipeListBinding
import com.example.caps_project.models.responses.RecipeSummaryList

class StoredRecipeAdapter(private val list:List<RecipeSummaryList>) : RecyclerView.Adapter<StoredRecipeAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val binding: ActivityStoredRecipeListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun binding(item: RecipeSummaryList) {
            binding.btnRecipe.setOnClickListener(null)

            if (item.id == -1 && item.sequence == -1) {
                binding.btnRecipe.text = item.name
            } else {
                val title = "${item.sequence}. ${item.name}"
                binding.btnRecipe.text = title
                binding.btnRecipe.setOnClickListener {
                    val intent = Intent(binding.root.context, B10_RecipeDetailActivity::class.java)
                    intent.putExtra("name", item.name)
                    intent.putExtra("seq", item.sequence)
                    intent.putExtra("saved", true)
                    intent.putExtra("id", item.id)
                    binding.root.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoredRecipeAdapter.MyViewHolder {
        val binding = ActivityStoredRecipeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoredRecipeAdapter.MyViewHolder, position: Int) {
        holder.binding(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}