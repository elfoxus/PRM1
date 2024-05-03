package com.example.prm1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.prm1.R
import com.example.prm1.data.model.Product
import com.example.prm1.databinding.ProductListElementBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ProductViewHolder (val binding: ProductListElementBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(product: Product) {
        binding.name.text = product.name
        binding.expirationDate.text = LocalDateTime.ofInstant(product.expirationDate, ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        binding.category.text = product.category.toString()
        binding.quantity.text = product.quantity.toString()
        binding.imageView.setImageResource(product.resId)

        binding.root.setOnClickListener {
            _ -> navigateToProductDetails(product, binding.root)
        }
    }

    private fun navigateToProductDetails(product: Product, view: View) {
        view.findNavController().navigate(
            R.id.action_productList_to_upsertProductFragment,
            bundleOf("product" to product)
        )
    }
}

class ProductsAdapter : RecyclerView.Adapter<ProductViewHolder>() {

    private val products = mutableListOf<Product>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductListElementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun replace(newProducts: List<Product>) {
        this.products.clear()
        this.products.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun add(product: Product) {
        products.add(product)
        notifyItemInserted(products.size - 1)
    }

    fun remove(product: Product) {
        val index = products.indexOf(product)
        products.remove(product)
        notifyItemRemoved(index)
    }

    fun update(product: Product) {
        val index = products.indexOf(product)
        products[index] = product
        notifyItemChanged(index)
    }

}