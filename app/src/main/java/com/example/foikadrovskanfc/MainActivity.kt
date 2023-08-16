package com.example.foikadrovskanfc

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.example.foikadrovskanfc.api.ApiService
import com.example.foikadrovskanfc.database.PersonnelDatabase
import com.example.foikadrovskanfc.fragments.HomeFragment
import com.example.foikadrovskanfc.fragments.InfoFragment
import com.example.foikadrovskanfc.fragments.SettingsFragment
import com.example.foikadrovskanfc.utils.NfcUtils
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var fragmentManager: FragmentManager
    private lateinit var homeFragment: HomeFragment
    private lateinit var infoFragment: InfoFragment
    private lateinit var currentFragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.tb_mainActivity)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        drawerLayout = findViewById(R.id.drawerLayout_main)
        val navigationView = findViewById<NavigationView>(R.id.navigationView_main)
        navigationView.setNavigationItemSelectedListener(this)

        fragmentManager = supportFragmentManager
        homeFragment = HomeFragment()
        infoFragment = InfoFragment()

        if (savedInstanceState == null) {
            currentFragment = homeFragment
            showFragment(homeFragment)
            navigationView.setCheckedItem(R.id.home)
        } else {
            currentFragment = fragmentManager.getFragment(savedInstanceState, "currentFragment")
                ?: homeFragment
        }

        val drawerToggle = object : androidx.appcompat.app.ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        ) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                updateMenuItemsVisibility(currentFragment)
            }
        }

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val window = window
        val statusBarColor = ContextCompat.getColor(this, R.color.red)
        changeStatusBarColor(window, statusBarColor)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragmentManager.putFragment(outState, "currentFragment", currentFragment)
    }

    private fun updateMenuItemsVisibility(fragment: Fragment) {
        val navigationView = findViewById<NavigationView>(R.id.navigationView_main)
        val menu = navigationView.menu

        val refreshItem = menu.findItem(R.id.refresh)
        val clearDatabaseItem = menu.findItem(R.id.clearDatabase)

        when (fragment) {
            is HomeFragment -> {
                refreshItem.isVisible = true
                clearDatabaseItem.isVisible = true
            }
            else -> {
                refreshItem.isVisible = false
                clearDatabaseItem.isVisible = false
            }
        }
    }
    private fun showFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        currentFragment = when (item.itemId) {
            R.id.home -> homeFragment
            R.id.info -> infoFragment
            R.id.clearDatabase -> {
                homeFragment.checkedItems.clear()
                showClearDatabasePopup()
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.refresh -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                GlobalScope.launch(Dispatchers.Main) {
                    performRefresh()
                }
                return true
            }
            else -> throw IllegalArgumentException("Invalid menu item selected")
        }

        showFragment(currentFragment)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private suspend fun performRefresh() {
        val response = ApiService.getPersonnelData()
        if (response != null) {
            homeFragment.loadPersonnelList()
        } else
            Log.e("API Error", "Error occurred while fetching personnel data")
    }

    private fun showClearDatabasePopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.delete))
        builder.setMessage(getString(R.string.deleteQuestion))
        builder.setPositiveButton(getString(R.string.clear)) { _, _ ->
            PersonnelDatabase.buildInstance(this)
            PersonnelDatabase.getInstance().getPersonnelDAO().deleteAllPersonnel()
            homeFragment.loadPersonnelList()
        }
        builder.setNegativeButton(getString(R.string.close)) { _, _ ->

        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            onBackPressedDispatcher.onBackPressed()
    }

    private fun changeStatusBarColor(window: Window, color: Int){
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }
}