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

        val originalImageBitmap = createImageBitmap(width, height, logoDrawable, receivedList)
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


    private fun createImageBitmap(width: Int, height: Int, logoDrawable: Drawable?, receivedList: ArrayList<Personnel>): Bitmap{
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)

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