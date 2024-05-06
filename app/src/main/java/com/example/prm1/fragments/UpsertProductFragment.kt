package com.example.prm1.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prm1.R
import com.example.prm1.Utils
import com.example.prm1.adapters.ProductImagesAdapter
import com.example.prm1.data.ProductDb
import com.example.prm1.data.entity.ProductEntity
import com.example.prm1.databinding.FragmentUpsertProductBinding
import java.util.Calendar
import kotlin.concurrent.thread

private const val NO_PRODUCT = -1L

class UpsertProductFragment : Fragment() {

    private lateinit var db: ProductDb
    private lateinit var binding: FragmentUpsertProductBinding
    private val errors: HashSet<String> = java.util.HashSet()
    private var productId: Long = NO_PRODUCT
    private lateinit var imagesAdapter: ProductImagesAdapter
    private var expired: Boolean = false;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = ProductDb.open(requireContext())
        binding = FragmentUpsertProductBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupImages()
        val productId: Long = requireArguments().getLong("productId")
        if (productId == NO_PRODUCT) {
            setupNewProductView()
        } else {
            this.productId = productId
            setupEditedProductView(productId)
        }
    }

    private fun setupImages() {
        binding.productImages.apply {
            imagesAdapter = ProductImagesAdapter()
            adapter = imagesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupEditedProductView(productId: Long) {
        fillFieldsOnInit(productId)
        setupFields(false)
    }

    private fun fillFieldsOnInit(productId: Long) {
        thread {
            val product = db.productDao.getById(productId)
            requireActivity().runOnUiThread {
                fillFields(product)
                stopLoader()
            }
        }
    }

    private fun stopLoader() {
        binding.productScrollView.visibility = View.VISIBLE
        binding.productProgressBar.visibility = View.GONE
    }

    private fun fillFields(product: ProductEntity) {
        binding.nameField.setText(product.name)
        binding.quantityField.setText(product.quantity.toString())
        binding.categorySpinner.setSelection(product.category)
        binding.disposedField.isChecked = product.disposed
        binding.expirationCalendar.setText(millisToText(product.expirationDate))
        val resId = resources.getIdentifier(product.image, "drawable", requireContext().packageName)
        imagesAdapter.selectImage(resId)

        val calendar = Utils.millisToCalendar(product.expirationDate)
        if (calendar.isBeforeByDays(Calendar.getInstance())) {
            expired = true
            disableFields()
            showExpiredNotification()
        }
    }

    private fun disableFields() {
        binding.expirationCalendar.isEnabled = false
        binding.nameField.isEnabled = false
        binding.quantityField.isEnabled = false
        binding.categorySpinner.isEnabled = false
        binding.disposedField.isEnabled = false
        imagesAdapter.disable()
    }

    fun Calendar.isBeforeByDays(other: Calendar): Boolean {
        return this.get(Calendar.YEAR) < other.get(Calendar.YEAR) ||
                (this.get(Calendar.YEAR) == other.get(Calendar.YEAR) && this.get(Calendar.DAY_OF_YEAR) < other.get(Calendar.DAY_OF_YEAR))
    }

    private fun millisToText(expirationDate: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = expirationDate
        return String.format("%02d/%02d/%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
    }

    private fun setupNewProductView() {
        errors.add("name")
        binding.nameField.error = "Name cannot be empty"
        binding.disposedField.visibility = View.GONE
        binding.quantityField.setText("1") // default value
        setupFields(true)
        stopLoader()
    }

    private fun setupFields(isNew: Boolean) {

        binding.expirationCalendar.setOnClickListener {
            showDatePicker(isNew, it)
        }

        binding.quantityField.addTextChangedListener {
                if (it.toString().isEmpty()) {
                    binding.quantityField.setText("0")
                    binding.quantityField.setSelection(0, binding.quantityField.text.length)
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

    private fun showDatePicker(isNew: Boolean, view: View) {
        val text = binding.expirationCalendar.text.toString()
        lateinit var initialCalendar: Calendar
        if (text.isEmpty()) {
            initialCalendar = Calendar.getInstance()
        } else {
            initialCalendar = textToCalendar(text)
        }

        val datePicker = DatePickerDialog(
            requireContext(),
            { calendarView, year, month, dayOfMonth ->
                val formattedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                view.findViewById<EditText>(R.id.expirationCalendar).setText(formattedDate)
            },
            initialCalendar.get(Calendar.YEAR),
            initialCalendar.get(Calendar.MONTH),
            initialCalendar.get(Calendar.DAY_OF_MONTH)
        ).also {
            if(isNew) {
                it.datePicker.minDate = Calendar.getInstance().timeInMillis
            }
        }
        datePicker.show()
    }

    private fun textToMillis(text: String): Long {
        val calendar = textToCalendar(text)
        return calendar.timeInMillis
    }

    private fun textToCalendar(text: String): Calendar {
        val date = text.split("/")
        val calendar = Calendar.getInstance()
        calendar.set(date[2].toInt(), date[1].toInt() - 1, date[0].toInt())
        return calendar
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_product -> {

                if (expired) {
                    showExpiredNotification()
                    return false
                }

                if (hasErrors()) {
                    showErrorNotification()
                    return false
                }
                // save/update and navigate back
                thread {
                    if (isNewProduct()) {
                        val product = ProductEntity(
                            name = binding.nameField.text.toString(),
                            expirationDate = textToMillis(binding.expirationCalendar.text.toString()),
                            category = binding.categorySpinner.selectedItemPosition,
                            quantity = binding.quantityField.text.toString().toInt(),
                            disposed = binding.disposedField.isChecked,
                            image = resources.getResourceEntryName(imagesAdapter.selectedIdRes)
                        )
                        db.productDao.addProduct(product)
                    } else {
                        val product = ProductEntity(
                            id = productId,
                            name = binding.nameField.text.toString(),
                            expirationDate = textToMillis(binding.expirationCalendar.text.toString()),
                            category = binding.categorySpinner.selectedItemPosition,
                            quantity = binding.quantityField.text.toString().toInt(),
                            disposed = binding.disposedField.isChecked,
                            image = resources.getResourceEntryName(imagesAdapter.selectedIdRes)
                        )
                        db.productDao.updateProduct(product)
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

    private fun showExpiredNotification() {
        Toast.makeText(requireContext(), this.resources?.getText(R.string.expired_product), Toast.LENGTH_SHORT).show()
    }

    fun showErrorNotification() {
        Toast.makeText(requireContext(), this.resources?.getText(R.string.form_has_errors), Toast.LENGTH_SHORT).show()
    }

    private fun isNewProduct() = productId == NO_PRODUCT

    fun hasErrors(): Boolean {
        return errors.isNotEmpty()
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}