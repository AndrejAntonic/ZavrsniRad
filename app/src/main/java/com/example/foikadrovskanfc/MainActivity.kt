package com.example.foikadrovskanfc

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.text.toLowerCase
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foikadrovskanfc.adapters.PersonnelAdapter
import com.example.foikadrovskanfc.api.ApiInterface
import com.example.foikadrovskanfc.api.ApiService
import com.example.foikadrovskanfc.database.PersonnelDatabase
import com.example.foikadrovskanfc.entities.Personnel
import com.example.foikadrovskanfc.entities.UserResponse
import com.example.foikadrovskanfc.helpers.MockDataLoader
import com.example.foikadrovskanfc.utils.NfcUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.security.AccessController.getContext
import java.util.Locale

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabPersonnel: FloatingActionButton
    private lateinit var twEmpty: TextView
    private lateinit var filterOn: ImageButton
    private lateinit var filterOff: ImageButton
    private lateinit var searchView: SearchView
    private lateinit var adapter: PersonnelAdapter
    private lateinit var drawerLayout: DrawerLayout
    private val checkedItems: MutableList<Personnel> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PersonnelDatabase.buildInstance(applicationContext)
        PersonnelDatabase.getInstance().getPersonnelDAO().deleteAllPersonnel()

        val window = window
        val statusBarColor = ContextCompat.getColor(this, R.color.red)
        changeStatusBarColor(window, statusBarColor)

        drawerLayout = findViewById(R.id.drawerLayout_main)
        var toolbar = findViewById<Toolbar>(R.id.tb_mainActivity)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        val navigationView = findViewById<NavigationView>(R.id.navigationView_main)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        checkNfcCapability()

        twEmpty = findViewById(R.id.tw_empty)
        recyclerView = findViewById(R.id.rv_personnelRecords)
        loadPersonnelList()
        recyclerView.layoutManager = LinearLayoutManager(this)

        fabPersonnel = findViewById(R.id.fab_generateImage)
        fabPersonnel.setOnClickListener{
            checkedItems.addAll(adapter.getSelectedItems())
            checkedItems.removeAll(adapter.getRemovedItems())
            if(checkedItems.isNotEmpty()) {
                val intent = Intent(this, CanvasActivity::class.java)
                intent.putExtra("personnelList", ArrayList(checkedItems))
                startActivity(intent)
            }
            else
                Toast.makeText(this, "Please select an employee first!", Toast.LENGTH_SHORT).show()
        }

        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val copyList = PersonnelDatabase.getInstance().getPersonnelDAO().getAllPersonnel().toMutableList()
                val filteredList = mutableListOf<Personnel>()
                if(newText.isEmpty()) {
                    filteredList.addAll(copyList)
                }
                else {
                    val query = newText.lowercase()
                    for(personnel in copyList) {
                        val fullName = personnel.firstName + " " + personnel.lastName
                        if (personnel.firstName.lowercase().contains(query) ||
                            personnel.lastName.lowercase().contains(query) ||
                            personnel.title.lowercase().contains(query) ||
                            fullName.lowercase().contains(query))
                            filteredList.add(personnel)
                    }
                }

                refreshPersonnelList(filteredList)
                return true
            }
        })
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.refresh -> ApiService.getPersonnelData { personnelList ->
                    refreshPersonnelList(personnelList)
                }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun refreshPersonnelList(personnelList: MutableList<Personnel>) {
        checkedItems.addAll(adapter.getSelectedItems())
        checkedItems.removeAll(adapter.getRemovedItems())
        Log.d("Checked items", checkedItems.toString())
        Log.d("Checked tags", adapter.getSelectedItems().toString())
        if(personnelList.isNotEmpty())
            twEmpty.text = ""
        adapter = PersonnelAdapter(personnelList, checkedItems)
        recyclerView.adapter = adapter
    }

    private fun loadPersonnelList() {
        val personnelList = PersonnelDatabase.getInstance().getPersonnelDAO().getAllPersonnel().toMutableList()
        if (personnelList.isNotEmpty())
            twEmpty.text = ""
        adapter = PersonnelAdapter(personnelList.toMutableList())
        recyclerView.adapter = adapter
    }
}