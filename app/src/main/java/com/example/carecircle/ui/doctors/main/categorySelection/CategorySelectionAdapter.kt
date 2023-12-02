package com.example.carecircle.ui.doctors.main.categorySelection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carecircle.databinding.CategorySelectionItemBinding

class CategorySelectionAdapter(private val categories: List<String>) :
    RecyclerView.Adapter<CategorySelectionAdapter.ViewHolder>() {
    class ViewHolder(val binding: CategorySelectionItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CategorySelectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = categories.size

    private var userId: String? = null
    fun setUserId(userId: String) {
        this.userId = userId
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val category: String = categories[position]
            binding.categoryTxt.text = category
            if (onItemClickListener != null) {
                binding.root.setOnClickListener {
                    userId?.let { userId ->
                        onItemClickListener?.onItemClick(position, category, userId)
                    }
                }
            }
        }
    }


    var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onItemClick(position: Int, category: String, userId: String)
    }
}