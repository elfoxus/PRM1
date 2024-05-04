package com.example.prm1.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prm1.R
import com.example.prm1.adapters.ProductsAdapter
import com.example.prm1.data.ProductDb
import com.example.prm1.data.model.Product
import com.example.prm1.databinding.FragmentProductListBinding
import kotlin.concurrent.thread

class ProductList : Fragment() {

    private lateinit var binding: FragmentProductListBinding
    private lateinit var adapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ProductsAdapter()

        loadData()


        binding.recyclerViewProductList.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.addProductButton.setOnClickListener {
            view ->
                println("Button clicked")
                findNavController().navigate(R.id.action_productList_to_upsertProductFragment)
        }
    }

    private fun loadData() = thread {
        val products = ProductDb.open(requireContext()).productDao.getAllSortedByExpirationDate().map { entity ->
            val resId = resources.getIdentifier(entity.image, "drawable", requireContext().packageName)
            Product.fromEntity(entity, resId)
        }

        requireActivity().runOnUiThread {
            adapter.apply {
                replace(products)
            }
            binding.listSizeLabel.text = resources.getText(R.string.list_size).toString() + " " + products.size
        }
    }


    override fun onStart() {
        super.onStart()
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filterList -> {
                // Show popup menu with filter options
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}