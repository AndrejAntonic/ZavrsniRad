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
    private val grayShadesArray = byteArrayOf(
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
        0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
        0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F,
        0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27,
        0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D, 0x2E, 0x2F,
        0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
        0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D, 0x3E, 0x3F,
        0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47,
        0x48, 0x49, 0x4A, 0x4B, 0x4C, 0x4D, 0x4E, 0x4F,
        0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57,
        0x58, 0x59, 0x5A, 0x5B, 0x5C, 0x5D, 0x5E, 0x5F,
        0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67,
        0x68, 0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F,
        0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77,
        0x78, 0x79, 0x7A, 0x7B, 0x7C, 0x7D, 0x7E, 0x7F,
        0x80.toByte(), 0x81.toByte(), 0x82.toByte(), 0x83.toByte(), 0x84.toByte(), 0x85.toByte(), 0x86.toByte(), 0x87.toByte(),
        0x88.toByte(), 0x89.toByte(), 0x8A.toByte(), 0x8B.toByte(), 0x8C.toByte(), 0x8D.toByte(), 0x8E.toByte(), 0x8F.toByte(),
        0x90.toByte(), 0x91.toByte(), 0x92.toByte(), 0x93.toByte(), 0x94.toByte(), 0x95.toByte(), 0x96.toByte(), 0x97.toByte(),
        0x98.toByte(), 0x99.toByte(), 0x9A.toByte(), 0x9B.toByte(), 0x9C.toByte(), 0x9D.toByte(), 0x9E.toByte(), 0x9F.toByte(),
        0xA0.toByte(), 0xA1.toByte(), 0xA2.toByte(), 0xA3.toByte(), 0xA4.toByte(), 0xA5.toByte(), 0xA6.toByte(), 0xA7.toByte(),
        0xA8.toByte(), 0xA9.toByte(), 0xAA.toByte(), 0xAB.toByte(), 0xAC.toByte(), 0xAD.toByte(), 0xAE.toByte(), 0xAF.toByte(),
        0xB0.toByte(), 0xB1.toByte(), 0xB2.toByte(), 0xB3.toByte(), 0xB4.toByte(), 0xB5.toByte(), 0xB6.toByte(), 0xB7.toByte(),
        0xB8.toByte(), 0xB9.toByte(), 0xBA.toByte(), 0xBB.toByte(), 0xBC.toByte(), 0xBD.toByte(), 0xBE.toByte(), 0xBF.toByte(),
        0xC0.toByte(), 0xC1.toByte(), 0xC2.toByte(), 0xC3.toByte(), 0xC4.toByte(), 0xC5.toByte(), 0xC6.toByte(), 0xC7.toByte(),
        0xC8.toByte(), 0xC9.toByte(), 0xCA.toByte(), 0xCB.toByte(), 0xCC.toByte(), 0xCD.toByte(), 0xCE.toByte(), 0xCF.toByte(),
        0xD0.toByte(), 0xD1.toByte(), 0xD2.toByte(), 0xD3.toByte(), 0xD4.toByte(), 0xD5.toByte(), 0xD6.toByte(), 0xD7.toByte(),
        0xD8.toByte(), 0xD9.toByte(), 0xDA.toByte(), 0xDB.toByte(), 0xDC.toByte(), 0xDD.toByte(), 0xDE.toByte(), 0xDF.toByte(),
        0xE0.toByte(), 0xE1.toByte(), 0xE2.toByte(), 0xE3.toByte(), 0xE4.toByte(), 0xE5.toByte(), 0xE6.toByte(), 0xE7.toByte(),
        0xE8.toByte(), 0xE9.toByte(), 0xEA.toByte(), 0xEB.toByte(), 0xEC.toByte(), 0xED.toByte(), 0xEE.toByte(), 0xEF.toByte(),
        0xF0.toByte(), 0xF1.toByte(), 0xF2.toByte(), 0xF3.toByte(), 0xF4.toByte(), 0xF5.toByte(), 0xF6.toByte(), 0xF7.toByte(),
        0xF8.toByte(), 0xF9.toByte(), 0xFA.toByte(), 0xFB.toByte(), 0xFC.toByte(), 0xFD.toByte(), 0xFE.toByte(), 0xFF.toByte()
    )


    private lateinit var bitmap0: Bitmap
    private var width0: Int = 0
    private var height0: Int = 0
    private lateinit var pixels: IntArray
    private var image_buffer: ByteArray = ByteArray(15000)

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
                var tempPixels = IntArray(8)
                for(z in 0 until 8) {
                    tempPixels[z] = pixels[temp]
                    temp++
                }
                var totalLuminance = 0.0
                for(pixel in tempPixels) {
                    val red = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue = Color.blue(pixel)

                    val luminance = 0.299 * red + 0.587 * green + 0.114 * blue
                    totalLuminance += luminance
                }

                val averageLuminance = totalLuminance / tempPixels.size
                byteArray[index] = grayShadesArray[averageLuminance.toInt()]
                /*
                if(averageLuminance <= 8)
                    byteArray[index] = 0x00
                else if (averageLuminance > 8 && averageLuminance <= 16)
                    byteArray[index] = 0x08
                else if (averageLuminance > 16 && averageLuminance <= 24)
                    byteArray[index] = 0x10
                else if (averageLuminance > 24 && averageLuminance <= 32)
                    byteArray[index] = 0x18
                else if (averageLuminance > 32 && averageLuminance <= 40)
                    byteArray[index] = 0x20
                else if (averageLuminance > 40 && averageLuminance <= 48)
                    byteArray[index] = 0x28
                else if (averageLuminance > 48 && averageLuminance <= 56)
                    byteArray[index] = 0x30
                else if (averageLuminance > 56 && averageLuminance <= 64)
                    byteArray[index] = 0x38
                else if (averageLuminance > 64 && averageLuminance <= 72)
                    byteArray[index] = 0x40.toByte()
                else if (averageLuminance > 72 && averageLuminance <= 80)
                    byteArray[index] = 0x48.toByte()
                else if (averageLuminance > 80 && averageLuminance <= 88)
                    byteArray[index] = 0x50.toByte()
                else if (averageLuminance > 88 && averageLuminance <= 96)
                    byteArray[index] = 0x58.toByte()
                else if (averageLuminance > 96 && averageLuminance <= 104)
                    byteArray[index] = 0x60.toByte()
                else if (averageLuminance > 104 && averageLuminance <= 112)
                    byteArray[index] = 0x68.toByte()
                else if (averageLuminance > 112 && averageLuminance <= 120)
                    byteArray[index] = 0x70.toByte()
                else if (averageLuminance > 120 && averageLuminance <= 128)
                    byteArray[index] = 0x78
                else if (averageLuminance > 128 && averageLuminance <= 136)
                    byteArray[index] = 0x80.toByte()
                else if (averageLuminance > 136 && averageLuminance <= 144)
                    byteArray[index] = 0x88.toByte()
                else if (averageLuminance > 144 && averageLuminance <= 152)
                    byteArray[index] = 0x90.toByte()
                else if (averageLuminance > 152 && averageLuminance <= 160)
                    byteArray[index] = 0x98.toByte()
                else if (averageLuminance > 160 && averageLuminance <= 168)
                    byteArray[index] = 0xA0.toByte()
                else if (averageLuminance > 168 && averageLuminance <= 176)
                    byteArray[index] = 0xA8.toByte()
                else if (averageLuminance > 176 && averageLuminance <= 184)
                    byteArray[index] = 0xB0.toByte()
                else if (averageLuminance > 184 && averageLuminance <= 192)
                    byteArray[index] = 0xB8.toByte()
                else if (averageLuminance > 192 && averageLuminance <= 200)
                    byteArray[index] = 0xC0.toByte()
                else if (averageLuminance > 200 && averageLuminance <= 208)
                    byteArray[index] = 0xC8.toByte()
                else if (averageLuminance > 208 && averageLuminance <= 216)
                    byteArray[index] = 0xD0.toByte()
                else if (averageLuminance > 216 && averageLuminance <= 224)
                    byteArray[index] = 0xD8.toByte()
                else if (averageLuminance > 224 && averageLuminance <= 232)
                    byteArray[index] = 0xE0.toByte()
                else if (averageLuminance > 232 && averageLuminance <= 240)
                    byteArray[index] = 0xE8.toByte()
                else if (averageLuminance > 240 && averageLuminance <= 248)
                    byteArray[index] = 0xF0.toByte()
                else
                    byteArray[index] = 0xF8.toByte()*/
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