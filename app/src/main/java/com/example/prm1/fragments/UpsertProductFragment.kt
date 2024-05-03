package com.example.prm1.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.prm1.R
import com.example.prm1.data.ProductDb
import com.example.prm1.data.entity.ProductEntity
import com.example.prm1.databinding.FragmentUpsertProductBinding
import java.time.Instant
import java.time.LocalDate
import kotlin.concurrent.thread

private const val NO_PRODUCT = -1L

class UpsertProductFragment : Fragment() {

    private lateinit var binding: FragmentUpsertProductBinding
    private val errors: HashSet<String> = java.util.HashSet()
    private var productId: Long = NO_PRODUCT

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpsertProductBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val productId: Long = requireArguments().getLong("productId")
        if (productId == NO_PRODUCT) {
            setupNewProductView()
        } else {
            setupEditedProductView(productId)
        }
    }

    private fun setupEditedProductView(productId: Long) {
        binding.disposedField.visibility = View.VISIBLE
        fillFieldsOnInit(productId)
        setupValidationBindings()
    }

    private fun fillFieldsOnInit(productId: Long) {
        thread {
            val product = ProductDb.open(requireContext()).productDao.getById(productId)
            requireActivity().runOnUiThread {
                fillFields(product)
            }
        }
    }

    private fun fillFields(product: ProductEntity) {
        binding.nameField.setText(product.name)
        binding.quantityField.setText(product.quantity.toString())
        binding.categorySpinner.setSelection(product.category)
        binding.disposedField.isChecked = product.disposed
        binding.productImageView.setImageResource(R.drawable.img)
        binding.expirationCalendar.date = product.expirationDate
    }

    private fun setupNewProductView() {
        errors.add("name")
        binding.nameField.error = "Name cannot be empty"
        binding.disposedField.visibility = View.GONE
        binding.quantityField.setText("1") // default value
        setupValidationBindings()
    }

    private fun setupValidationBindings() {
        binding.expirationCalendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            if (isBeforeToday(year, month, dayOfMonth)) {
                view.date = Instant.now().toEpochMilli()
            }
        }
        binding.quantityField.addTextChangedListener {
            if (it.toString().toInt() < 1) {
                binding.quantityField.setText("1")
            }
        }
        binding.nameField.addTextChangedListener(
            beforeTextChanged = { _, _, _, _ -> },
            onTextChanged = { _, _, _, _ -> },
            afterTextChanged = { editable ->
                if (editable.toString().isEmpty()) {
                    binding.nameField.error = context?.resources?.getString(R.string.name_error)
                    errors.add("name")
                } else {
                    binding.nameField.error = null
                    errors.remove("name")
                }
            }
        )
    }

    private fun isBeforeToday(year: Int, month: Int, dayOfMonth: Int): Boolean {
        val today = LocalDate.now()
        return LocalDate.of(year, month+1, dayOfMonth).isBefore(today)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_product -> {

                if (hasErrors()) {
                    showErrorNotification()
                    return false
                }
                // save/update and navigate back
                thread {
                    if (isNewProduct()) {
                            val product = ProductEntity(
                                name = binding.nameField.text.toString(),
                                expirationDate = binding.expirationCalendar.date,
                                category = binding.categorySpinner.selectedItemPosition,
                                quantity = binding.quantityField.text.toString().toInt(),
                                disposed = binding.disposedField.isChecked
                            )
                            ProductDb.open(requireContext()).productDao.addProduct(product)
                    } else {
                        val product = ProductEntity(
                            id = productId,
                            name = binding.nameField.text.toString(),
                            expirationDate = binding.expirationCalendar.date,
                            category = binding.categorySpinner.selectedItemPosition,
                            quantity = binding.quantityField.text.toString().toInt(),
                            disposed = binding.disposedField.isChecked
                        )
                        ProductDb.open(requireContext()).productDao.updateProduct(product)
                    }
                    // navigate back to product list after saving
                    requireActivity().runOnUiThread {
                        findNavController().navigate(R.id.action_upsertProductFragment_to_productList)
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showErrorNotification() {
        Toast.makeText(requireContext(), this.resources?.getText(R.string.form_has_errors), Toast.LENGTH_SHORT).show()
    }

    private fun isNewProduct() = productId == NO_PRODUCT

    fun hasErrors(): Boolean {
        return errors.isNotEmpty()
    }

}