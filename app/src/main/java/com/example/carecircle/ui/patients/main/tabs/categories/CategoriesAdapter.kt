package com.example.carecircle.ui.patients.main.tabs.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carecircle.databinding.CategoryItemBinding
import com.example.carecircle.model.CategoryData

class CategoriesAdapter(val categories: ArrayList<CategoryData>) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {
    class ViewHolder(val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriesAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val category: CategoryData = categories[position]
            binding.categoryImg.setImageResource(category.imgId)
            binding.categoryTxt.text = category.name
        }
    }

    var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onItemClick(position: Int, category: String, userId: String)
    }

}