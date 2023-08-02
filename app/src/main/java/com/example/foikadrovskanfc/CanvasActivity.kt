package com.example.foikadrovskanfc

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.graphics.Canvas
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foikadrovskanfc.entities.Personnel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Date

class CanvasActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var imageView: ImageView
    private lateinit var sendButton: Button
    private lateinit var saveButton: Button
    private var nfcAdapter: NfcAdapter? = null
    private val REQUEST_WRITE_STORAGE = 112
    private val width = 400
    private val height = 300

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)
        val window = window
        val statusBarColor = ContextCompat.getColor(this, R.color.red)
        changeStatusBarColor(window, statusBarColor)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        backButton = findViewById(R.id.imgbtn_backArrow)
        backButton.setOnClickListener{
            finish()
        }

        imageView = findViewById(R.id.imgw_generatedImage)
        val logoDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.foilogo75x65)
        val receivedList = intent.getSerializableExtra("personnelList") as ArrayList<Personnel>

        val originalImageBitmap = createImageBitmap(width, height, logoDrawable, receivedList)
        val scaledImageBitmap = Bitmap.createScaledBitmap(originalImageBitmap, 100, 75, false)
        imageView.setImageBitmap(originalImageBitmap)

        val imageByteArray = convertBitmapToByteArray(originalImageBitmap)

        saveButton = findViewById(R.id.btn_saveImage)
        saveButton.setOnClickListener {
            saveBitmapToGallery(this@CanvasActivity, originalImageBitmap, "MyImage")
        }

        sendButton = findViewById(R.id.btn_sendImage)
        sendButton.setOnClickListener {
            nfcAdapter?.let {
                if(!it.isEnabled) {
                    openNfcSettings()
                }
                else
                    sendNfcMessage(imageByteArray)
            }
        }
        //logByteArray(imageByteArray)
    }

    private fun createNdefMessage(image: ByteArray): NdefMessage {
        val mimeRecord = NdefRecord.createMime("image/jpeg", image)
        return NdefMessage(mimeRecord)
    }

    private fun sendNfcMessage(image: ByteArray) {
        val ndefMessage = createNdefMessage(image)
        nfcAdapter?.setNdefPushMessage(ndefMessage, this)
    }

    private fun openNfcSettings() {
        try {
            val intent = Intent(android.provider.Settings.ACTION_NFC_SETTINGS)
            startActivity(intent)
        } catch (e: Exception) {

        }
    }

    private fun checkWriteStoragePermission(activity: AppCompatActivity): Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("ccccc", "ccccccc")
            if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_STORAGE)
                Log.d("dddddd", "ddddddddd")
                return false
            }
        }
        return true
    }

    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, displayName: String) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val filename = "${displayName}_${Date().time}.png"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val resolver = context.contentResolver
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            try {
                imageUri?.let {
                    val outputStream: OutputStream? = resolver.openOutputStream(it)
                    outputStream?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else {
            // For Android 9 and earlier (Legacy Storage)
            if (checkWriteStoragePermission(this@CanvasActivity)) {
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val file = File(path, "${displayName}_${Date().time}.png")

                try {
                    val outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /*
    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, displayName: String) {
        if(checkWriteStoragePermission(this@CanvasActivity)) {
            Log.d("aaaaa", "aaaaaaaaaaa")
            val filename = "${displayName}_${Date().time}.png"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            try {
                imageUri?.let {
                    val outputStream: OutputStream? = resolver.openOutputStream(it)
                    outputStream?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri!!, contentValues, null, null)
                }
            }
        }
    }
     */

    private fun changeStatusBarColor(window: Window, color: Int){
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }

    fun logByteArray(byteArray: ByteArray) {
        val hexString = byteArray.joinToString("") { "%02x".format(it) }
        Log.d("Hexadecimal", "Hexadecimal representation: $hexString")

        val base64String = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
        Log.d("Base64", "Base64 representation: $base64String")
    }


    private fun createImageBitmap(width: Int, height: Int, logoDrawable: Drawable?, receivedList: ArrayList<Personnel>): Bitmap{
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        logoDrawable?.let {
            val logoWidth = width / 5
            val logoHeight = height / 5

            val logoBitmap = Bitmap.createBitmap(logoWidth, logoHeight, Bitmap.Config.ARGB_8888)
            val logoCanvas = Canvas(logoBitmap)

            it.setBounds(0, 0, logoWidth, logoHeight)
            it.draw(logoCanvas)

            val logoLeft = 5
            val logoTop = 5
            canvas.drawBitmap(logoBitmap, logoLeft.toFloat(), logoTop.toFloat(), null)
        }

        when(receivedList.size) {
            1 -> canvas = addOnePersonnel(receivedList, canvas)
            2 -> canvas = addTwoPersonnel(receivedList, canvas)
            3 -> canvas = addThreePersonnel(receivedList, canvas)
            4 -> canvas = addFourPersonnel(receivedList, canvas)
        }

        return bitmap
    }

    private fun addFourPersonnel(receivedList: ArrayList<Personnel>, canvas: Canvas): Canvas {
        val firstPersonnel = receivedList[0]
        var titleOne = firstPersonnel.title
        var nameOne = firstPersonnel.firstName + " " + firstPersonnel.lastName
        val secondPersonnel = receivedList[1]
        var titleTwo = secondPersonnel.title
        var nameTwo = secondPersonnel.firstName + " " + secondPersonnel.lastName
        val thirdPersonnel = receivedList[2]
        var titleThree = thirdPersonnel.title
        var nameThree = thirdPersonnel.firstName + " " + thirdPersonnel.lastName
        val fourthPersonnel = receivedList[3]
        var titleFour = fourthPersonnel.title
        var nameFour = fourthPersonnel.firstName + " " + fourthPersonnel.lastName

        if (nameTwo.length < nameOne.length) {
            val tempName = nameOne
            val tempTitle = titleOne
            nameOne = nameTwo
            titleOne = titleTwo
            nameTwo = tempName
            titleTwo = tempTitle
        }
        if (nameFour.length < nameThree.length) {
            val tempName = nameThree
            val tempTitle = titleFour
            nameThree = nameFour
            titleThree = titleFour
            nameFour = tempName
            titleFour = tempTitle
        }
        val textTitle = Paint().apply {
            textSize = 15f
            color = Color.BLACK
        }
        val textName = Paint().apply {
            textSize = 20f
            color = Color.BLACK
        }
        var firstHalfTextWidth = textName.measureText(nameTwo)
        var firstHalfx = canvas.width / 4f - firstHalfTextWidth / 2f
        canvas.drawText(titleOne, firstHalfx, 115f, textTitle)
        canvas.drawText(nameOne, firstHalfx, 140f, textName)
        canvas.drawText(titleTwo, firstHalfx, 180f, textTitle)
        canvas.drawText(nameTwo, firstHalfx, 215f, textName)

        var secondHalfTextWidth = textName.measureText(nameFour)
        var secondHalfx = canvas.width * 3f / 4f - secondHalfTextWidth / 2f
        canvas.drawText(titleThree, secondHalfx, 115f, textTitle)
        canvas.drawText(nameThree, secondHalfx, 140f, textName)
        canvas.drawText(titleFour, secondHalfx, 180f, textTitle)
        canvas.drawText(nameFour, secondHalfx, 215f, textName)


        return canvas
    }

    private fun addThreePersonnel(receivedList: java.util.ArrayList<Personnel>, canvas: Canvas): Canvas {
        //TODO: Popraviti sortiranje po velicini
        val firstPersonnel = receivedList[0]
        var titleOne = firstPersonnel.title
        var nameOne = firstPersonnel.firstName + " " + firstPersonnel.lastName
        val secondPersonnel = receivedList[1]
        var titleTwo = secondPersonnel.title
        var nameTwo = secondPersonnel.firstName + " " + secondPersonnel.lastName
        val thirdPersonnel = receivedList[2]
        var titleThree = thirdPersonnel.title
        var nameThree = thirdPersonnel.firstName + " " + thirdPersonnel.lastName

        if (nameTwo.length < nameOne.length) {
            val tempName = nameOne
            val tempTitle = titleOne
            nameOne = nameTwo
            titleOne = titleTwo
            nameTwo = tempName
            titleTwo = tempTitle
        }
        if (nameThree.length < nameOne.length) {
            val tempName = nameOne
            val tempTitle = titleOne
            nameOne = nameThree
            titleOne = titleThree
            nameThree = tempName
            titleThree = tempTitle
        }
        if (nameThree.length < nameTwo.length) {
            val tempName = nameTwo
            val tempTitle = titleTwo
            nameTwo = nameThree
            titleTwo = titleThree
            nameThree = tempName
            titleThree = tempTitle
        }

        val textTitle = Paint().apply {
            textSize = 15f
            color = Color.BLACK
        }
        val textName = Paint().apply {
            textSize = 25f
            color = Color.BLACK
        }
        var textWidth = textName.measureText(nameOne)
        var x = canvas.width / 2f - textWidth / 2f
        canvas.drawText(titleOne, x, 105f, textTitle)
        canvas.drawText(nameOne, x, 130f, textName)

        textWidth = textName.measureText(nameTwo)
        x = canvas.width / 2f - textWidth / 2f
        canvas.drawText(titleTwo, x, 165f, textTitle)
        canvas.drawText(nameTwo, x, 190f, textName)

        textWidth = textName.measureText(nameThree)
        x = canvas.width / 2f - textWidth / 2f
        canvas.drawText(titleThree, x, 225f, textTitle)
        canvas.drawText(nameThree, x, 250f, textName)

        return canvas
    }

    private fun addTwoPersonnel(receivedList: ArrayList<Personnel>, canvas: Canvas): Canvas {
        val firstPersonnel = receivedList[0]
        var titleOne = firstPersonnel.title
        var nameOne = firstPersonnel.firstName + " " + firstPersonnel.lastName
        val secondPersonnel = receivedList[1]
        var titleTwo = secondPersonnel.title
        var nameTwo = secondPersonnel.firstName + " " + secondPersonnel.lastName

        if (nameTwo.length < nameOne.length) {
            val tempName = nameOne
            val tempTitle = titleOne
            nameOne = nameTwo
            titleOne = titleTwo
            nameTwo = tempName
            titleTwo = tempTitle
        }

        val textTitle = Paint().apply {
            textSize = 15f
            color = Color.BLACK
        }
        val textName = Paint().apply {
            textSize = 25f
            color = Color.BLACK
        }
        var textWidth = textName.measureText(nameOne)
        var x = canvas.width / 2f - textWidth / 2f
        canvas.drawText(titleOne, x, 110f, textTitle)
        canvas.drawText(nameOne, x, 140f, textName)

        textWidth = textName.measureText(nameTwo)
        x = canvas.width / 2f - textWidth / 2f
        canvas.drawText(titleTwo, x, 190f, textTitle)
        canvas.drawText(nameTwo, x, 220f, textName)

        return canvas
    }

    private fun addOnePersonnel(receivedList: ArrayList<Personnel>, canvas: Canvas): Canvas {
        val firstPersonnel = receivedList[0]
        val title = firstPersonnel.title
        val name = firstPersonnel.firstName + " " + firstPersonnel.lastName

        val textTitle = Paint().apply {
            textSize = 20f
            color = Color.BLACK
        }
        val textName = Paint().apply {
            textSize = 35f
            color = Color.BLACK
        }
        val textWidth = textName.measureText(name)
        val x = canvas.width / 2f - textWidth / 2f

        canvas.drawText(title, x, 140f, textTitle)
        canvas.drawText(name, x, 180f, textName)

        return canvas
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}


/*
imageView = findViewById(R.id.imgw_generatedImage)
        val logoDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.foilogo75x65)
        val text = "Proba"

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        logoDrawable?.let{
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            it.draw(canvas)
        }

        val textPaint = Paint().apply{
            textSize = 24f
            color = Color.BLACK
        }

        canvas.drawText(text, 10f, 40f, textPaint)

        imageView.setImageBitmap(bitmap)
 */