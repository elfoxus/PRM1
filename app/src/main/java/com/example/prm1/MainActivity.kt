package com.example.prm1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.prm1.data.ProductDb
import com.example.prm1.data.entity.ProductEntity
import com.example.prm1.data.model.Category
import com.example.prm1.databinding.ActivityMainBinding
import com.example.prm1.fragments.UpsertProductFragment
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var db: ProductDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProductDb.open(this).also { db = it }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        loadExampleData()

    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }



    private fun loadExampleData() = thread {
        val dao = db.productDao
        if (dao.getAll().isEmpty()) {
            dao.insertAll(getExampleData())
        }
    }

    private fun getExampleData(): Array<ProductEntity> {
        return arrayOf(
            ProductEntity(
                image = resources.getResourceEntryName(R.drawable.mleko),
                name = "Mleko",
                expirationDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }.timeInMillis,
                category = Category.FOOD.getId(),
                quantity = 2,
                disposed = false
            ),
            ProductEntity(
                image = resources.getResourceEntryName(R.drawable.chleb),
                name = "Chleb z ziarnami",
                expirationDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) }.timeInMillis,
                category = Category.FOOD.getId(),
                quantity = 1,
                disposed = false
            ),
            ProductEntity(
                image = resources.getResourceEntryName(R.drawable.chleb),
                name = "Chleb tostowy",
                expirationDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -10) }.timeInMillis,
                category = Category.FOOD.getId(),
                quantity = 2,
                disposed = true
            ),
            ProductEntity(
                image = resources.getResourceEntryName(R.drawable.aspiryna),
                name = "Aspiryna",
                expirationDate = Calendar.getInstance().apply { add(Calendar.YEAR, 2) }.timeInMillis,
                category = Category.MEDICINES.getId(),
                quantity = 4,
                disposed = false
            ),
            ProductEntity(
                image = resources.getResourceEntryName(R.drawable.tusz_do_rzes),
                name = "Tusz do rzęs",
                expirationDate = Calendar.getInstance().apply { add(Calendar.MONTH, 5) }.timeInMillis,
                category = Category.COSMETICS.getId(),
                quantity = 1,
                disposed = false
            ),
            ProductEntity(
                image = resources.getResourceEntryName(R.drawable.pasta_do_zebow),
                name = "Pasta do zębów",
                expirationDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 5) }.timeInMillis,
                category = Category.COSMETICS.getId(),
                quantity = 1,
                disposed = false
            ),
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_main)
//
//        val currentFragment = getCurrentFragment()
//        if (currentFragment is UpsertProductFragment && currentFragment.hasErrors()) {
//            currentFragment.showErrorNotification()
//            return false
//        }

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
//        val currentFragment = getCurrentFragment()
//        if (currentFragment is UpsertProductFragment && currentFragment.hasErrors()) {
//            currentFragment.showErrorNotification()
//        } else {
            super.onBackPressed()
//        }
    }

    private fun getCurrentFragment(): Fragment? {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_main)
        val currentFragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
        return currentFragment
    }
}