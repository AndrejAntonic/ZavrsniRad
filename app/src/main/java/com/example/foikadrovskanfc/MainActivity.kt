package com.example.foikadrovskanfc

import android.os.Bundle
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
            Toast.makeText(this, "NFC is not supported on this device", Toast.LENGTH_SHORT).show()
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