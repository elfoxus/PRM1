package com.example.prm1.fragments

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prm1.R
import com.example.prm1.adapters.ProductsAdapter
import com.example.prm1.data.ProductDb
import com.example.prm1.data.entity.ProductEntity
import com.example.prm1.data.model.Product
import com.example.prm1.databinding.FragmentProductListBinding
import kotlin.concurrent.thread

class ProductList : Fragment() {

    private lateinit var binding: FragmentProductListBinding
    private lateinit var adapter: ProductsAdapter
    private lateinit var db: ProductDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = ProductDb.open(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ProductsAdapter(db)
        binding.categoryFilter.setSelection(0)
        binding.expirationFilter.setSelection(0)

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

        binding.categoryFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadData(categoryFilter(), binding.expirationFilter.selectedItemPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing, can't happen
            }
        }

        binding.expirationFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadData(categoryFilter(), binding.expirationFilter.selectedItemPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing, can't happen
            }
        }

        adapter.onProductRemoved = { itemsLeft ->
            binding.listSizeLabel.text = resources.getText(R.string.list_size).toString() + " " + itemsLeft
        }
    }

    // minus 1, cause category ids starts from 0, and here 0 is "All"
    private fun categoryFilter() :Int {
        return binding.categoryFilter.selectedItemPosition.minus(1)
    }

    private fun loadData(category: Int = -1, date: Int = 0) = thread {
        val productDao = db.productDao

        lateinit var dbResult: List<ProductEntity>
        if (all(category, date)) {
            dbResult = productDao.getAllSortedByExpirationDate()
        } else if (allDatesWithCategory(category, date)) {
            dbResult = productDao.getAllSortedByExpirationDateForCategory(category)
        } else if (expiredAllCategory(category, date)) {
            dbResult = productDao.getExpiredSortedByExpirationDate()
        } else if (expiredWithCategory(category, date)) {
            dbResult = productDao.getExpiredWithCategorySortedByExpirationDate(category)
        } else if (notExpiredAllCategory(category, date)) {
            dbResult = productDao.getNotExpiredSortedByExpirationDate()
        } else {
            dbResult = productDao.getNotExpiredSortedByExpirationDateWithCategory(category)
        }

        val products = dbResult.map { entity ->
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

    private fun notExpiredAllCategory(category: Int, date: Int) = category == -1 && date == 2

    private fun expiredWithCategory(category: Int, date: Int) = category != -1 && date == 1

    private fun expiredAllCategory(category: Int, date: Int) = category == -1 && date == 1

    private fun allDatesWithCategory(category: Int, date: Int) = category != -1 && date == 0

    private fun all(category: Int, date: Int) = category == -1 && date == 0


    override fun onStart() {
        super.onStart()
        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}