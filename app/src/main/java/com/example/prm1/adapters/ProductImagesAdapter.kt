package com.example.prm1.adapters

import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prm1.R
import com.example.prm1.databinding.ProductImgBinding

class ProductImagesAdapter: RecyclerView.Adapter<ProductImagesViewHolder>() {

    private var enabled: Boolean = true

    private val images = listOf(
        R.drawable.chleb,
        R.drawable.mleko,
        R.drawable.aspiryna,
        R.drawable.tusz_do_rzes,
        R.drawable.pasta_do_zebow,
    )
    private var selectedPosition: Int = 0
    val selectedIdRes: Int
        get() = images[selectedPosition]


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImagesViewHolder {
        val binding = ProductImgBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductImagesViewHolder(binding).also { vh ->
            binding.root.setOnClickListener {
                if (enabled) {
                    notifyItemChanged(selectedPosition)
                    selectedPosition = vh.layoutPosition
                    notifyItemChanged(selectedPosition)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ProductImagesViewHolder, position: Int) {
        holder.bind(images[position], position == selectedPosition)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun selectImage(resId: Int) {
        selectedPosition = images.indexOf(resId)
        notifyDataSetChanged()
    }

    fun disable() {
        enabled = false
        notifyDataSetChanged()
    }
}

class ProductImagesViewHolder(val binding: ProductImgBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(resId: Int, selected: Boolean) {
        binding.productImage.setImageResource(resId)
        binding.productImage.colorFilter = if (selected) {
            null
        } else {
            // grayscale when not selected
            // I don't know why, but couldn't manage to show frame around like in the tutorial, it was not visible,
            // so I decided to use grayscale for not selected images
            ColorMatrixColorFilter(
                floatArrayOf(
                    0.33f, 0.33f, 0.33f, 0f, 0f,
                    0.33f, 0.33f, 0.33f, 0f, 0f,
                    0.33f, 0.33f, 0.33f, 0f, 0f,
                    0f,    0f,    0f,    1f, 0f
                )
            )
        }
    }
}