package com.example.foikadrovskanfc

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foikadrovskanfc.fragments.HomeFragment
import com.example.foikadrovskanfc.fragments.InfoFragment
import com.example.foikadrovskanfc.fragments.SettingsFragment
import com.example.foikadrovskanfc.utils.NfcUtils
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkNfcCapability()

        drawerLayout = findViewById(R.id.drawerLayout_main)
        val toolbar = findViewById<Toolbar>(R.id.tb_mainActivity)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val navigationView = findViewById<NavigationView>(R.id.navigationView_main)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val window = window
        val statusBarColor = ContextCompat.getColor(this, R.color.red)
        changeStatusBarColor(window, statusBarColor)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.menuHome)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.settings -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()
            R.id.info -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, InfoFragment()).commit()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
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
    private fun checkNfcCapability() {
        val nfcUtils = NfcUtils(this)
        val hasNfcCapability = nfcUtils.hasNfcCapability()
        val isNfcEnabled = nfcUtils.isNfcEnabled()

        if(hasNfcCapability) {
            if(isNfcEnabled)
                Toast.makeText(this, "NFC is supported and enabled", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "NFC is supported but not enabled", Toast.LENGTH_SHORT).show()
            //showNfcNotEnabledDialog()
        } else
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