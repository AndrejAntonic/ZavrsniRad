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


class CanvasActivity : AppCompatActivity()/*, NfcAdapter.ReaderCallback*/ {
    private lateinit var backButton: ImageButton
    private lateinit var imageView: ImageView
    private lateinit var saveButton: Button
    private lateinit var percentageText: TextView
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var generatedImageBitmap: Bitmap
    private lateinit var dihteringImageBitmap: Bitmap
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
        dihteringImageBitmap = pictureUtils.applyFloydSteinbergDithering(generatedImageBitmap)
        percentageText = findViewById(R.id.tw_percentage)

        logoBitmap = pictureUtils.generateImageBitmapLogo(this)
        textBitmap = pictureUtils.generateImageBitmapText(intent.getSerializableExtra("personnelList") as ArrayList<Personnel>)

        val asd = convertPictureToByteArray(logoBitmap)

        backButton = findViewById(R.id.imgbtn_backArrow)
        backButton.setOnClickListener{
            finish()
        }

        saveButton = findViewById(R.id.btn_saveImage)
        saveButton.setOnClickListener {
            galleryUtils.saveBitmapToGallery(this, logoBitmap, "FOI-kadrovska", this@CanvasActivity)
            Toast.makeText(this, "Slika spremljena", Toast.LENGTH_SHORT).show()
        }

        imageView = findViewById(R.id.imgw_generatedImage)
        imageView.setImageBitmap(generatedImageBitmap)
    }

    fun convertToBlackAndWhite(inputBitmap: Bitmap): Bitmap {
        val width = inputBitmap.width
        val height = inputBitmap.height

        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixelColor = inputBitmap.getPixel(x, y)
                val grayscaleValue = (Color.red(pixelColor) + Color.green(pixelColor) + Color.blue(pixelColor)) / 3
                val bwPixelColor = if (grayscaleValue < 128) Color.BLACK else Color.WHITE
                outputBitmap.setPixel(x, y, bwPixelColor)
            }
        }

        return outputBitmap
    }


    private fun paintForColor(color: Int): Paint {
        val paint = Paint()
        paint.color = color
        return paint
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
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

    //For a different e-paper with a different driver -> needs to be changed -> don't know how
    var data_DB = "F0DB000069";//10    Î²ÊýÊÇºóÃæËùÓÐÊý¾ÝºÍµÄÒ»°ë¡£
    var start = "A00603300190012C";//16   4.2´çµ¥É«400x300
    var RST = "A4010C" + "A502000A" + "A40108" + "A502000A" + "A4010C" + "A502000A" + "A40108" + "A502000A" + "A4010C" + "A502000A" + "A40108" + "A502000A" + "A4010C" + "A502000A" + "A40103"; // 48 +56   ¸´Î»Èý´Î
    var proba = "A4010C" + "A502000A" + "A40108" + "A502000A" + "A4010C" + "A502000A" + "A40102"
    var set_wf = "A102000F";//8  //0x00 0F
    var set_power = "A10104" + "A40103";//12  //0x04  busy
    var set_resolution = "A105610190012C";//14   //0x61 01 90 01 2C
    var set_border = "A1025097";//8  //0x50 97
    var write_BW = "A3021013"; //6    //0x10
    var write_BWR = "A3021013"; //8   //0x13
    var update = "A20112" + "A502000A" + "A40103";//20  //0x12 delay  busy
    var sleep = "A20102" + "A40103" + "A20207A5"; //20 //0x02 busy 07 A5
    val epd_init: Array<String> = arrayOf(
        data_DB + start + RST + set_wf + set_resolution + set_border + set_power + write_BWR + update + sleep,
        "F0DA000003F00330" // Additional data as in your provided code
    )


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val p: Parcelable = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG) ?: return
        val tag = p as Tag
        val nfcA = NfcA.get(tag)
        if(nfcA != null) {
            thread {
                try {
                    Log.e("debug", "intent")

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

                    var text = convertPictureToByteArray(textBitmap)
                    var length = text.size
                    var totalPercent = 0
                    for(i in 0 until length step 5) {
                        cmd = byteArrayOf(0xCD.toByte(), 0x85.toByte(), 0x05, text[i], text[i + 1], text[i + 2], text[i + 3], text[i + 4])
                        response = nfcA.transceive(cmd)
                        //Log.e("Slanje crno", i.toString() + " " + HexToString(response))
                        totalPercent += 5
                        runOnUiThread {
                            percentageText.text =
                                ((totalPercent.toFloat() / 30000) * 100).toInt().toString() + "%"
                        }
                        //Log.e("percent", ((totalPercent.toFloat() / 30000) * 100).toInt().toString() + "%")
                    }
                    Thread.sleep(200)
                    cmd = byteArrayOf(0xCD.toByte(), 0x84.toByte())
                    response = nfcA.transceive(cmd)
                    Log.e("Crveni mode", HexToString(response))
                    Thread.sleep(200)

                    val logo = convertPictureToByteArray(logoBitmap)
                    length = logo.size
                    for(i in 0 until length step 5) {
                        cmd = byteArrayOf(0xCD.toByte(), 0x85.toByte(), 0x05, logo[i], logo[i + 1], logo[i + 2], logo[i + 3], logo[i + 4])
                        response = nfcA.transceive(cmd)
                        //Log.e("Slanje crveno", i.toString() + " " + HexToString(response))
                        totalPercent += 5
                        runOnUiThread {
                            percentageText.text =
                                ((totalPercent.toFloat() / 30000) * 100).toInt().toString() + "%"
                        }
                        //Log.e("percent", ((totalPercent.toFloat() / 30000) * 100).toInt().toString() + "%")
                    }
                    Thread.sleep(200)
                    cmd = byteArrayOf(0xCD.toByte(), 0x86.toByte())
                    response = nfcA.transceive(cmd)
                    Log.e("Refresh", HexToString(response))
                    Thread.sleep(200)
/*
                    if (response[0] != 0x90.toByte()) {
                        response = nfcA.transceive(cmd)
                        Log.e("Refresh2:", HexToString(response))
                    }*/
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

    fun convertPictureToByteArray(bitmap: Bitmap): ByteArray {
        val byteArray = ByteArray(15000)
        var index = 0
        var temp = 0

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for(x in (bitmap.height - 1) downTo 0) {
            for(y in 0 until bitmap.width / 8) {
                var tempBinary = ""
                for(z in 0 until 8) {
                    val red = Color.red(pixels[temp])
                    val green = Color.green(pixels[temp])
                    val blue = Color.blue(pixels[temp])

                    val luminance = 0.299 * red + 0.587 * green + 0.114 * blue
                    if(luminance < 128)
                        tempBinary += "0"
                    else
                        tempBinary += "1"
                    temp++
                }
                val decimalValue = Integer.parseInt(tempBinary, 2)
                val hexValue = decimalValue.toString(16).toUpperCase()
                byteArray[index] = hexValue.toInt(16).toByte()
                index++
            }
        }

        return byteArray
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