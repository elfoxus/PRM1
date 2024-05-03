package com.example.prm1.fragments

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.prm1.R
import com.example.prm1.data.DataSource
import com.example.prm1.data.model.Category
import com.example.prm1.data.model.Product
import com.example.prm1.databinding.FragmentUpsertProductBinding
import java.time.Instant
import java.time.ZoneId

class UpsertProductFragment : Fragment() {

    private lateinit var binding: FragmentUpsertProductBinding

    private var edited: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpsertProductBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val product: Product? = arguments?.get("product") as Product?
        if (product != null) {
            edited = true
            fillFieldsOnInit(product)
            bindFields(product)
        } else {
            edited = false
        }
    }

    private fun bindFields(product: Product) {
        binding.nameField.addTextChangedListener {
            product.name = it.toString()
        }
        binding.quantityField.addTextChangedListener {
            product.quantity = it.toString().toInt()
        }
        binding.categorySpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                product.category = Category.fromId(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }
        })
        binding.disposedField.setOnCheckedChangeListener { _, isChecked ->
            product.thrownAway = isChecked
        }
        binding.expirationCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            product.expirationDate = Instant.ofEpochMilli(binding.expirationCalendar.date)
        }
    }

    private fun fillFieldsOnInit(product: Product) {
        binding.nameField.setText(product.name)
        binding.quantityField.setText(product.quantity.toString())
        binding.categorySpinner.setSelection(product.category.ordinal)
        binding.disposedField.isChecked = product.thrownAway
        binding.productImageView.setImageResource(product.resId)
        binding.expirationCalendar.date = product.expirationDate.toEpochMilli()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_product -> {
                // save and navigate back
                // if product was not in edit view, then we need to add it, otherwise it is updated by listeners
                if (!edited) {
                    DataSource.products.add(
                        Product(
                            R.drawable.img,
                            binding.nameField.text.toString(),
                            Instant.ofEpochMilli(binding.expirationCalendar.date),
                            Category.fromId(binding.categorySpinner.selectedItemPosition),
                            binding.quantityField.text.toString().toInt(),
                            binding.disposedField.isChecked
                        )
                    )
                }
                findNavController().navigate(R.id.action_upsertProductFragment_to_productList)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}