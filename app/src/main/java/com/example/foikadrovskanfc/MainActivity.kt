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
    private lateinit var settingsFragment: SettingsFragment
    private lateinit var infoFragment: InfoFragment
    private lateinit var currentFragment: Fragment
    private val settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_LANG_CHANGED)
                recreate()
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //checkNfcCapability()
        applyUserSettings()

        val toolbar = findViewById<Toolbar>(R.id.tb_mainActivity)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        drawerLayout = findViewById(R.id.drawerLayout_main)
        val navigationView = findViewById<NavigationView>(R.id.navigationView_main)
        navigationView.setNavigationItemSelectedListener(this)

        fragmentManager = supportFragmentManager
        homeFragment = HomeFragment()
        settingsFragment = SettingsFragment()
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
            R.id.settings -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.info -> infoFragment
            R.id.clearDatabase -> {
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
            Log.d("API Error", "Error occurred while fetching personnel data")
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
    private fun applyUserSettings(): Boolean {
        PreferenceManager.getDefaultSharedPreferences(this)?.
        let { pref ->
            SettingsActivity.switchDarkMode(
                pref.getBoolean("preference_dark_mode", false)
            )
            val lang = pref.getString("preference_language", "EN")
            if (lang != null) {
                val locale = Locale(lang)
                if (
                    resources.configuration.locales[0].language != locale.language) {
                    resources.configuration.setLocale(locale)
                    Locale.setDefault(locale)
                    createConfigurationContext(
                        resources.configuration
                    )
                    recreate()
                    return false
                }
            }
        }
        return true
    }
    private fun changeStatusBarColor(window: Window, color: Int){
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }
    private fun checkNfcCapability() {
        val nfcUtils = NfcUtils(this)

        if(nfcUtils.hasNfcCapability())
            if(!nfcUtils.isNfcEnabled())
                Toast.makeText(this, "NFC is supported but not enabled", Toast.LENGTH_SHORT).show()
        else
            showNfcNotSupportedDialog()
    }
    private fun showNfcNotSupportedDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.nfc_not_supported_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnClose : Button = dialog.findViewById(R.id.btn_nfcNSupported)

        btnClose.setOnClickListener{
            finish()
        }

        dialog.show()
    }
/*
    private fun showNfcNotEnabledDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.nfc_not_enabled_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnClose : Button = dialog.findViewById(R.id.btn_nfcNEnabledClose)
        val btnEnable : Button = dialog.findViewById(R.id.btn_nfcNEnabledEnable)

        btnClose.setOnClickListener{
            finish()
        }

        btnEnable.setOnClickListener{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                startActivity(intent)
            }
            else{
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
            }
        }

        dialog.show()
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.refresh -> ApiService.getPersonnelData { personnelList ->
                    refreshPersonnelList(personnelList)
                }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
 */
}