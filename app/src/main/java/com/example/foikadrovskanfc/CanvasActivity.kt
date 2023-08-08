package com.example.foikadrovskanfc

import android.R.string
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import android.os.Bundle
import android.os.Parcelable
import android.os.SystemClock
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.foikadrovskanfc.entities.Personnel
import com.example.foikadrovskanfc.utils.GalleryUtils
import com.example.foikadrovskanfc.utils.PictureUtils
import waveshare.feng.nfctag.activity.a
import java.io.ByteArrayOutputStream
import java.io.IOException



class CanvasActivity : AppCompatActivity()/*, NfcAdapter.ReaderCallback*/ {
    private lateinit var backButton: ImageButton
    private lateinit var imageView: ImageView
    private lateinit var saveButton: Button
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var generatedImageBitmap: Bitmap
    private lateinit var dihteringImageBitmap: Bitmap
    private val pictureUtils = PictureUtils()
    private val galleryUtils = GalleryUtils()

    private lateinit var bitmap0: Bitmap
    private var width0: Int = 0
    private var height0: Int = 0
    private lateinit var pixels: IntArray
    private var image_buffer: ByteArray = ByteArray(100000)

    private val mInstance: a = a()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)
        val statusBarColor = ContextCompat.getColor(this, R.color.red)
        changeStatusBarColor(window, statusBarColor)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        generatedImageBitmap = pictureUtils.generateImageBitmap(intent.getSerializableExtra("personnelList") as ArrayList<Personnel>, this)
        dihteringImageBitmap = pictureUtils.applyFloydSteinbergDithering(generatedImageBitmap)

        bitmap0 = dihteringImageBitmap
        image_buffer = bitmapToByteArray(dihteringImageBitmap)

        val hexString = hexStringToBytes(IC_DIY).joinToString(" ") { "%02x".format(it) }
        Log.e("debug", hexString)


        backButton = findViewById(R.id.imgbtn_backArrow)
        backButton.setOnClickListener{
            finish()
        }

        saveButton = findViewById(R.id.btn_saveImage)
        saveButton.setOnClickListener {
            galleryUtils.saveBitmapToGallery(this, generatedImageBitmap, "FOI-kadrovska", this@CanvasActivity)
        }

        imageView = findViewById(R.id.imgw_generatedImage)
        imageView.setImageBitmap(dihteringImageBitmap)
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

    var IC_DIY: String = "F0DB020000" //10
    var data_DB: String = "F0DB000067" //10
    var start: String = "A0060020007A00FA" //16
    var RST: String = "A4010C" + "A502000A" + "A40108" + "A502000A" + "A4010C" + "A502000A" + "A40102" //28
    var soft_RST: String = "A10112" + "A40102" //14
    var delay_10ms: String = "A502000A" //8
    var wait_busy: String = "A40102" //6
    var set_control: String = "A10401270101" //12    //01 27 01
    var set_mode: String = "A1021101" //8
    var set_RAM: String = "A10344000F" + "A1054527010000" //24    //44 45
    var set_border: String = "A1023C05" //8
    var set_RAM_Shift: String = "A103210080" //10
    var set_temperature: String = "A1021880" //8
    var set_RAM_counter: String = "A1024E00" + "A1034F2701" //18     //4E 4F
    var write_BW: String = "A30124" //6
    var write_BWR: String = "A3022426" //8
    var update: String = "A20222F7" + "A20120" + "A40102" //20
    var sleep: String = "A2021001" + "A502000A" //16
    var epd_init: String = data_DB + start + RST + soft_RST + set_control + set_mode + set_RAM + set_border + set_RAM_Shift + set_temperature + set_RAM_counter + write_BW + update + sleep
    var CutScreen: String = "F0DA000003F00020" //10


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // ATTEMPT 3
        var failMsg = ""
        var success = false
        mInstance.a()

        val p: Parcelable = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG) ?: return

        val tag = p as Tag
        val nfcTag = NfcA.get(tag)

        try {
            // Initialize
            val connectionSuccessInt = this.mInstance.a(nfcTag)
            // Override WaveShare's SDK default of 700
            nfcTag.timeout = 60000
            if (connectionSuccessInt != 1) {
                // IO exception in nfcTag.connect()
                failMsg = "Failed to connect to tag"
            } else {
                var flashSuccessInt = -1
                flashSuccessInt = this.mInstance.a(3, dihteringImageBitmap)
                if (flashSuccessInt == 1) {
                    // Success!
                    success = true
                } else if (flashSuccessInt == 2) {
                    failMsg = "Incorrect image resolution"
                } else {
                    failMsg = "Failed to write over NFC, unknown reason"
                }
            }
        } catch (e: IOException) {
            failMsg = e.toString()
            Log.v("WaveshareHandler, IO Exception", failMsg)
        }







        /* ATTEMPT 2
        var detectedTag: Tag?
        var EPD_total_progress: Int
        var Size_Flag = 3
        var bmp_send = dihteringImageBitmap
        Log.e("debug", "onNewIntent")
        Log.e("debug", intent!!.action.toString())

        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent!!.action) {
            Log.e("debug", "Tag discovered")
            detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            val tech: Array<String> = detectedTag!!.getTechList() //Get the descriptor
            if (tech[0] == "android.nfc.tech.NfcA") { //if the descriptor is correct
                Log.e("debug", "nfca")
                val t: Thread = object : Thread() {
                    //Create an new thread
                    override fun run() {
                        var success = false
                        val tntag: NfcA //NFC tag
                        val a = a() //Create a new instance.
                        //a.a() //初始化发送函数
                        val thread = Thread {
                            EPD_total_progress = 0
                            while (EPD_total_progress !== -1) {
                                EPD_total_progress = a.a() //Read the process
                                if (EPD_total_progress === -1) {
                                    break
                                }
                                Log.e("debug", "$EPD_total_progress %")
                                //setStatusBody(getString(string.txing) + EPD_total_progress + "%")
                                if (EPD_total_progress === 100) {
                                    break
                                }
                                SystemClock.sleep(10)
                            }
                        }
                        thread.start()//start the thread
                        tntag = NfcA.get(detectedTag) //Get the tag instance.
                        tntag.timeout = 60000
                        a.a(tntag)
                        try {
                            Log.e("debug", "slanje")
                            val whether_succeed: Int =
                                a.a(Size_Flag, bmp_send) //Send picture
                            if (whether_succeed == 1) {
                                Log.e("debug", "uspjelo")
                                success = true
                                runOnUiThread {
                                    //checkNFCpopwindow.dismiss() //Tips
                                }
                                //Success_Sound_Effects()
                            } else {
                                Log.e("debug", "nije uspjelo")
                                //setStatusBody(getString(string.txfail))
                            }
                        } finally {
                            try {
                                if (success == false) {
                                    Log.e("debug", "success == false")
                                    //setStatusBody(getString(string.txfail))
                                    //runOnUiThread { checkNFCpopwindow.dismiss() }
                                }
                                tntag.close()
                            } catch (e: IOException) { //handle exception error
                                e.printStackTrace()
                            }
                        }
                    }
                }
                t.start() //Start thread
            }
        }*/




        /* ATTEMPT 1
        val p: Parcelable = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG) ?: return

        val tag = p as Tag
        val nfcA = NfcA.get(tag)
        if(nfcA != null) {
            try {


                Log.e("debug", "intent")

                nfcA.connect()

                var cmd: ByteArray
                var response: ByteArray

                nfcA.timeout = 60000


                cmd = hexStringToBytes(IC_DIY)
                response = nfcA.transceive(cmd)
                Log.e("IC_start_state:",  HexToString(response))

                cmd = hexStringToBytes(epd_init)
                response = nfcA.transceive(cmd)
                Log.e("epdinit_state:", HexToString(response))

                cmd = hexStringToBytes(CutScreen) // epd init
                response = nfcA.transceive(cmd)
                Log.e("CutScreen_state:", HexToString(response))

                //getPictureDataSSD()
                val datas = width0 * height0 / 8

                for (i in 0 until datas / 250) {
                    cmd = byteArrayOf(0xF0.toByte(), 0xD2.toByte(), 0x00, i.toByte(), 0xFA.toByte())

                    for (j in 0 until 250) {
                        cmd[j + 5] = image_buffer[j + 250 * i]
                    }

                    response = nfcA.transceive(cmd) // Send black and white data
                    Log.e("${i + 1} sendData_state:", HexToString(response)) // Feedback data display, 9000 is Ok

                    // Data mantissa sending
                    if (i == datas / 250 - 1 && datas % 250 != 0) {
                        cmd = byteArrayOf(0xF0.toByte(), 0xD2.toByte(), 0x00, (i + 1).toByte(), 0xFA.toByte())

                        for (j in 0 until 250) {
                            cmd[j + 5] = image_buffer[j + 250 * (datas / 250)]
                        }

                        response = nfcA.transceive(cmd) // Send black and white data
                    }

                    Log.e("${i + 1} sendData_state:", HexToString(response)) // Feedback data display, 9000 is Ok
                }

                val refreshCmd = byteArrayOf(0xF0.toByte(), 0xD4.toByte(), 0x05, 0x80.toByte(), 0x00)
                response = nfcA.transceive(refreshCmd) // Send e-paper refresh command
                Log.e("RefreshData1_state:", HexToString(response)) // Feedback data display, 9000 is Ok

                if (response[0] != 0x90.toByte()) {
                    response = nfcA.transceive(refreshCmd) // Send black and white refresh command
                    Log.e("RefreshData2_state:", HexToString(response))
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.e("debug", "Exception in onNewIntent: $e")
            }
            finally {
                nfcA.close()
            }
        }*/



        /*
        Log.e("debug", "---------- " + intent!!.action)

        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent!!.action) {
            val tag = intent!!.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            Log.e("debug", "----------$tag")

            if (NfcV.get(tag) != null) {
                Log.e("debug", "Use NfcV object for further operations")
            }
            else if (NfcA.get(tag) != null) {
                Log.e("debug", "Use NfcA object for further operations")
            }
        }
         */
    }

    fun convertBitmapToSSDFormat(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // SSD format stores 2 pixels in 1 byte
        val ssdWidth = width / 2
        val ssdHeight = height

        val ssdBitmap = Bitmap.createBitmap(ssdWidth, ssdHeight, Bitmap.Config.ARGB_8888)

        for (y in 0 until height) {
            for (x in 0 until width step 2) {
                val pixel1 = bitmap.getPixel(x, y)
                val pixel2 = bitmap.getPixel(x + 1, y)

                // Extract red, green, and blue components from each pixel
                val red1 = (Color.red(pixel1) and 0xF0) shr 4
                val red2 = Color.red(pixel2) and 0xF0

                // Combine the two pixels into one
                val combinedPixel = Color.rgb(red1 or red2, 0, 0)
                ssdBitmap.setPixel(x / 2, y, combinedPixel)
            }
        }

        return ssdBitmap
    }



    fun getPictureDataSSD(): ByteArray {
        var index = 0
        var temp: Byte = 0
        val bitmap: Bitmap = bitmap0 // Bitmap0 is the current picture or the picture opened by the app, with a resolution of 128x250
        width0 = bitmap.width
        height0 = bitmap.height
        pixels = IntArray(width0 * height0)

        bitmap.getPixels(pixels, 0, width0, 0, 0, width0, height0) // Gets the color value of each pixel in the picture
        for (i in width0 - 1 downTo 0) {
            temp = 0
            for (j in 0..height0 / 8 - 1) {
                for (k in 0 until 8) {
                    temp = (temp * 2).toByte()

                    val pixel = pixels[i + (j * 8) + k] // Vertical scanning, i is the abscissa and j * 8 + k is the ordinate
                    val r = (pixel and 0xff0000) shr 16
                    val g = (pixel and 0xff00) shr 8
                    val b = (pixel and 0xff)

                    if (r <= 100 && g <= 100 && b <= 100) // rgb Black
                        temp = (temp + 0).toByte() // Black
                    else
                        temp = (temp + 1).toByte() // White
                }
                image_buffer[index] = temp

                index++
            }
        }
        return image_buffer
    }

    fun hexStringToBytes(hexStr: String): ByteArray {
        val b = ByteArray(hexStr.length / 2)
        var j = 0
        for (i in 0 until b.size) {
            val c0: Char = hexStr[j++]
            val c1: Char = hexStr[j++]
            b[i] = (parse(c0) shl 4 or parse(c1)).toByte()
        //b[i] = (Character.digit(c0, 16) shl 4 or Character.digit(c1, 16)).toByte()
        }
        return b
    }

    fun parse(c: Char): Int {
        return when {
            c >= 'a' -> (c - 'a' + 10) and 0x0f
            c >= 'A' -> (c - 'A' + 10) and 0x0f
            else -> (c - '0') and 0x0f
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
/*
    override fun onTagDiscovered(tag: Tag?) {
        val nfcA = NfcA.get(tag)
        if(nfcA != null) {
            try {
                Log.e("debug", "tag")
                nfcA.connect()

                var cmd: ByteArray
                var response: ByteArray

                cmd = hexStringToBytes(IC_DIY)
                response = nfcA.transceive(cmd)
                Log.e("IC_start_state:",  HexToString(response))

                cmd = hexStringToBytes(epd_init)
                response = nfcA.transceive(cmd)
                Log.e("epdinit_state:", HexToString(response))

                cmd = hexStringToBytes(CutScreen) // epd init
                response = nfcA.transceive(cmd)
                Log.e("CutScreen_state:", HexToString(response))

                getPictureDataSSD()
                val datas = width0 * height0 / 8

                for (i in 0 until datas / 250) {
                    cmd = byteArrayOf(0xF0.toByte(), 0xD2.toByte(), 0x00, i.toByte(), 0xFA.toByte())

                    for (j in 0 until 250) {
                        cmd[j + 5] = image_buffer[j + 250 * i]
                    }

                    response = nfcA.transceive(cmd) // Send black and white data
                    Log.e("${i + 1} sendData_state:", HexToString(response)) // Feedback data display, 9000 is Ok

                    // Data mantissa sending
                    if (i == datas / 250 - 1 && datas % 250 != 0) {
                        cmd = byteArrayOf(0xF0.toByte(), 0xD2.toByte(), 0x00, (i + 1).toByte(), 0xFA.toByte())

                        for (j in 0 until 250) {
                            cmd[j + 5] = image_buffer[j + 250 * (datas / 250)]
                        }

                        response = nfcA.transceive(cmd) // Send black and white data
                    }

                    Log.e("${i + 1} sendData_state:", HexToString(response)) // Feedback data display, 9000 is Ok
                }

                val refreshCmd = byteArrayOf(0xF0.toByte(), 0xD4.toByte(), 0x05, 0x80.toByte(), 0x00)
                response = nfcA.transceive(refreshCmd) // Send e-paper refresh command
                Log.e("RefreshData1_state:", HexToString(response)) // Feedback data display, 9000 is Ok

                if (response[0] != 0x90.toByte()) {
                    response = nfcA.transceive(refreshCmd) // Send black and white refresh command
                    Log.e("RefreshData2_state:", HexToString(response))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("debug", "Exception in onNewIntent: $e")
            } finally {
                nfcA.close()
            }
        }
    }*/
}