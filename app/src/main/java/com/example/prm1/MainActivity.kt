package com.example.prm1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.prm1.databinding.ActivityMainBinding
import com.example.prm1.fragments.UpsertProductFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
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