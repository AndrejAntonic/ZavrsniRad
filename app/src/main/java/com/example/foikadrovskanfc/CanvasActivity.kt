package com.example.foikadrovskanfc

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.graphics.Canvas
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.foikadrovskanfc.entities.Personnel
import java.io.ByteArrayOutputStream

class CanvasActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var imageView: ImageView
    private val width = 400
    private val height = 300

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)

        backButton = findViewById(R.id.imgbtn_backArrow)
        backButton.setOnClickListener{
            finish()
        }

        imageView = findViewById(R.id.imgw_generatedImage)
        val logoDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.foilogo75x65)
        val receivedList = intent.getSerializableExtra("personnelList") as ArrayList<Personnel>
        val firstPersonnel = receivedList.firstOrNull()
        val title = firstPersonnel?.title ?: ""

        val originalImageBitmap = createImageBitmap(width, height, logoDrawable, title)
        val scaledImageBitmap = Bitmap.createScaledBitmap(originalImageBitmap, 100, 75, false)
        imageView.setImageBitmap(originalImageBitmap)

        val imageByteArray = convertBitmapToByteArray(originalImageBitmap)
        //logByteArray(imageByteArray)
    }

    fun logByteArray(byteArray: ByteArray) {
        val hexString = byteArray.joinToString("") { "%02x".format(it) }
        Log.d("Hexadecimal", "Hexadecimal representation: $hexString")

        val base64String = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
        Log.d("Base64", "Base64 representation: $base64String")
    }


    private fun createImageBitmap(width: Int, height: Int, logoDrawable: Drawable?, text: String): Bitmap{
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

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

        val textPaint = Paint().apply {
            textSize = 20f
            color = Color.BLACK
        }
        val textLeft = 10f
        val textTop = logoDrawable?.intrinsicHeight ?: (0 + 20f)
        canvas.drawText(text, textLeft, textTop.toFloat(), textPaint)

        return bitmap
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