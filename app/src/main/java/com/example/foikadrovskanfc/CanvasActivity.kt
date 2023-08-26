package com.example.foikadrovskanfc

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.toUpperCase
import androidx.core.content.ContextCompat
import com.example.foikadrovskanfc.entities.Personnel
import com.example.foikadrovskanfc.utils.GalleryUtils
import com.example.foikadrovskanfc.utils.PictureUtils
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread


class CanvasActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var imageView: ImageView
    private lateinit var saveButton: Button
    private lateinit var percentageText: TextView
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var generatedImageBitmap: Bitmap
    private lateinit var logoBitmap: Bitmap
    private lateinit var textBitmap: Bitmap
    private val pictureUtils = PictureUtils()
    private val galleryUtils = GalleryUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)
        val statusBarColor = ContextCompat.getColor(this, R.color.red)
        changeStatusBarColor(window, statusBarColor)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        generatedImageBitmap = pictureUtils.generateImageBitmap(intent.getSerializableExtra("personnelList") as ArrayList<Personnel>, this)
        percentageText = findViewById(R.id.tw_percentage)

        logoBitmap = pictureUtils.generateImageBitmapLogo(this)
        textBitmap = pictureUtils.generateImageBitmapText(intent.getSerializableExtra("personnelList") as ArrayList<Personnel>)


        backButton = findViewById(R.id.imgbtn_backArrow)
        backButton.setOnClickListener{
            finish()
        }

        saveButton = findViewById(R.id.btn_saveImage)
        saveButton.setOnClickListener {
            galleryUtils.saveBitmapToGallery(this, generatedImageBitmap, "FOI-kadrovska", this@CanvasActivity)
            Toast.makeText(this, "Slika spremljena", Toast.LENGTH_SHORT).show()
        }

        imageView = findViewById(R.id.imgw_generatedImage)
        imageView.setImageBitmap(generatedImageBitmap)
    }

    override fun onResume() {
        super.onResume()

        if(nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.nfcNSupported), Toast.LENGTH_SHORT).show()
            return
        }
        else if(!nfcAdapter.isEnabled) {
            checkNFC()
            return
        }

        if(nfcAdapter != null) {
            try {
                val TECHLISTS = arrayOf(arrayOf(IsoDep::class.java.name), arrayOf(NfcV::class.java.name), arrayOf(NfcF::class.java.name), arrayOf(NfcA::class.java.name))
                val FILTERS = arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED, "*/*"))

                val intent = Intent(this, CanvasActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
                nfcAdapter.enableForegroundDispatch(this, pendingIntent, FILTERS, TECHLISTS)
                //nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null)
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.e("debug", "Exception in onResume")
            }
        }
    }

    override fun onPause() {
        super.onPause()

        if(nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val p: Parcelable = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG) ?: return
        val tag = p as Tag
        val nfcA = NfcA.get(tag)
        if(nfcA != null) {
            thread {
                try {
                    nfcA.connect()

                    var cmd: ByteArray
                    var response: ByteArray
                    nfcA.timeout = 1200

                    cmd = byteArrayOf(0xCD.toByte(), 0x80.toByte())
                    response = nfcA.transceive(cmd)
                    Log.e("Inicijaliziranje pinova", HexToString(response))
                    Thread.sleep(200)

                    cmd = byteArrayOf(0xCD.toByte(), 0x81.toByte())
                    response = nfcA.transceive(cmd)
                    Log.e("Soft reset", HexToString(response))
                    Thread.sleep(200)

                    cmd = byteArrayOf(0xCD.toByte(), 0x82.toByte())
                    response = nfcA.transceive(cmd)
                    Log.e("Eink step 2", HexToString(response))
                    Thread.sleep(200)

                    cmd = byteArrayOf(0xCD.toByte(), 0x83.toByte())
                    response = nfcA.transceive(cmd)
                    Log.e("Crno/bijeli mode", HexToString(response))
                    Thread.sleep(200)

                    cmd = ByteArray(33)
                    var text = pictureUtils.convertPictureToByteArray(textBitmap)
                    var length = text.size
                    var totalPercent = 0
                    var index = 0
                    for(i in 0 until length step 30) {
                        cmd[0] = 0xCD.toByte()
                        cmd[1] = 0x85.toByte()
                        cmd[2] = 0x1E
                        for(j in 0 until 30)
                            cmd[j + 3] = text[index++]
                        response = nfcA.transceive(cmd)
                        totalPercent += 30
                        runOnUiThread {
                            percentageText.text =
                                ((totalPercent.toFloat() / 30000) * 100).toInt().toString() + "%"
                        }
                    }
                    Thread.sleep(200)

                    cmd = byteArrayOf(0xCD.toByte(), 0x84.toByte())
                    response = nfcA.transceive(cmd)
                    Log.e("Crveni mode", HexToString(response))
                    Thread.sleep(200)

                    cmd = ByteArray(33)
                    val logo = pictureUtils.convertPictureToByteArray(logoBitmap)
                    length = logo.size
                    index = 0
                    for(i in 0 until length step 30) {
                        cmd[0] = 0xCD.toByte()
                        cmd[1] = 0x85.toByte()
                        cmd[2] = 0x1E
                        for(j in 0 until 30)
                            cmd[j + 3] = logo[index++]
                        response = nfcA.transceive(cmd)
                        totalPercent += 30
                        runOnUiThread {
                            percentageText.text =
                                ((totalPercent.toFloat() / 30000) * 100).toInt().toString() + "%"
                        }
                    }
                    Thread.sleep(200)
                    cmd = byteArrayOf(0xCD.toByte(), 0x86.toByte())
                    response = nfcA.transceive(cmd)
                    Log.e("Refresh", HexToString(response))
                    Thread.sleep(200)

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("debug", "Exception in onNewIntent: $e")
                } finally {
                    nfcA.close()
                }
            }
        }
    }

    private fun NumToString(c: Int): String {
        var data = ""
        if (c >= 10) {
            when (c) {
                10 -> data = "A"
                11 -> data = "B"
                12 -> data = "C"
                13 -> data = "D"
                14 -> data = "E"
                15 -> data = "F"
            }
        } else {
            when (c) {
                0 -> data = "0"
                1 -> data = "1"
                2 -> data = "2"
                3 -> data = "3"
                4 -> data = "4"
                5 -> data = "5"
                6 -> data = "6"
                7 -> data = "7"
                8 -> data = "8"
                9 -> data = "9"
            }
        }
        return data
    }

    private fun HexToString(data: ByteArray): String {
        var ReData = ""
        for (i in 0 until data.size) {
            ReData += NumToString(data[i] / 16) + NumToString(data[i] % 16)
        }
        return ReData
    }

    private fun checkNFC() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.nfcAlertTitle))
        builder.setMessage(getString(R.string.nfcAlertMessage))

        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
            openNfcSettings()
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun openNfcSettings() {
        try {
            val intent = Intent(android.provider.Settings.ACTION_NFC_SETTINGS)
            startActivity(intent)
        } catch (e: Exception) {

        }
    }

    private fun changeStatusBarColor(window: Window, color: Int){
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }
}