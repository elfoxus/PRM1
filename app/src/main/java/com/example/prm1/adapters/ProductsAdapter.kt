package com.example.prm1.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.prm1.R
import com.example.prm1.data.ProductDb
import com.example.prm1.data.model.Product
import com.example.prm1.databinding.ProductListElementBinding
import kotlin.concurrent.thread

class ProductViewHolder (val binding: ProductListElementBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(product: Product, adapter: ProductsAdapter, db: ProductDb) {
        fillInformations(product)
        setBackground(product)
        handleEvents(product, db, adapter)
    }

    private fun fillInformations(product: Product) {
        binding.name.text = product.name
        binding.expirationDate.text = product.getDateString()
        binding.category.text = binding.root.resources.getStringArray(R.array.categories).get(product.category.getId())
        binding.quantity.text = product.quantity.toString()
        binding.imageView.setImageResource(product.resId)
    }

    private fun handleEvents(
        product: Product,
        db: ProductDb,
        adapter: ProductsAdapter
    ) {
        binding.root.setOnClickListener { _ ->
            navigateToProductDetails(product, binding.root)
        }

        binding.root.setOnLongClickListener {
            if (!product.isExpired()) {
                AlertDialog.Builder(it.context)
                    .setTitle(R.string.remove_dialog_title)
                    .setMessage(R.string.remove_dialog_body)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        thread {
                            db.productDao.removeProduct(product.toEntity(resources = it.resources))
                            // refresh the list on ui thread
                            it.post {
                                adapter.remove(product)
                            }
                        }
                    }
                    .setNegativeButton(R.string.no) { _, _ -> }
                    .show()
            } else if (!product.disposed) {
                product.disposed = true
                thread {
                    db.productDao.updateProduct(product.toEntity(resources = it.resources))
                    // refresh the list
                    it.post {
                        adapter.update(product)
                    }
                }

            }

            true
        }
    }

    private fun setBackground(product: Product) {
        if (product.isExpired()) {
            binding.cardView.setBackgroundColor(binding.root.resources.getColor(R.color.expired_bkg, null))
        }

        if (product.disposed) {
            binding.cardView.setBackgroundColor(binding.root.resources.getColor(R.color.disposed_bkg, null))
        }

        if (product.isLastDay()) {
            binding.cardView.setBackgroundColor(binding.root.resources.getColor(R.color.last_day_bkg, null))
        }
    }

    private fun navigateToProductDetails(product: Product, view: View) {
        view.findNavController().navigate(
            R.id.action_productList_to_upsertProductFragment,
            bundleOf("productId" to product.id)
        )
    }
}

class ProductsAdapter(database: ProductDb) : RecyclerView.Adapter<ProductViewHolder>() {
    private val db = database
    private val products = mutableListOf<Product>()
    var onProductRemoved: (size: Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductListElementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position], this, db)
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
        onProductRemoved(products.size)
    }

    fun update(product: Product) {
        val index = products.indexOf(product)
        products[index] = product
        notifyItemChanged(index)
    }
}