package com.example.foikadrovskanfc

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.foikadrovskanfc.utils.NfcUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nfcUtils = NfcUtils(this)
        val hasNfcCapability = nfcUtils.hasNfcCapability()
        val isNfcEnabled = nfcUtils.isNfcEnabled()

        if(hasNfcCapability) {
            if(isNfcEnabled)
                Toast.makeText(this, "NFC is supported and enabled", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "NFC is supported but not enabled", Toast.LENGTH_SHORT).show()
        } else
            showNfcNotSupportedDialog()
    }

    private fun showNfcNotSupportedDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.nfc_not_supported_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnClose : Button = dialog.findViewById(R.id.nfcNSupported_btn)

        btnClose.setOnClickListener{
            finish()
        }

        dialog.show()
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