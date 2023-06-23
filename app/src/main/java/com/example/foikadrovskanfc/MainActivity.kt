package com.example.foikadrovskanfc

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foikadrovskanfc.adapters.PersonnelAdapter
import com.example.foikadrovskanfc.helpers.MockDataLoader
import com.example.foikadrovskanfc.utils.NfcUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabPersonnel: FloatingActionButton
    private val adapter = PersonnelAdapter(MockDataLoader.getDemoData())
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        //TODO: Poboljsati kod za navigaciju i dodati kod za odabiranje opcije u navigaciji.

        checkNfcCapability()

        recyclerView = findViewById(R.id.rv_personnelRecords)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fabPersonnel = findViewById(R.id.fab_generateImage)
        fabPersonnel.setOnClickListener{
            val checkedTags = adapter.getSelectedItems()
            if(checkedTags.isNotEmpty()) {
                val intent = Intent(this, CanvasActivity::class.java)
                intent.putExtra("personnelList", ArrayList(checkedTags))
                startActivity(intent)
            }
            else
                Toast.makeText(this, "Please select an employee first!", Toast.LENGTH_SHORT).show()
            /*
            val checkedTags = adapter.getSelectedItems()
            var message = ""
            for (tag in checkedTags)
                message += tag.id
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
             */
        }
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
        TODO("Not yet implemented")
    }
}


/*
Generirano sa projektom

Ovisnosti
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.foikadrovskanfc.ui.theme.FOIKadrovskaNFCTheme

U oncreate
setContent {
            FOIKadrovskaNFCTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FOIKadrovskaNFCTheme {
        Greeting("Android")
    }
}
 */