package com.example.foikadrovskanfc.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.foikadrovskanfc.R
import com.example.foikadrovskanfc.entities.Personnel
import java.io.ByteArrayOutputStream

class PictureUtils() {
    private val imageWidth = 400
    private val imageHeight = 300

    fun generateImageBitmapLogo(context: Context): Bitmap {
        val foiLogo = ContextCompat.getDrawable(context, R.drawable.foilogo75x65)
        val bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        foiLogo?.let {
            val logoWidth = imageWidth / 5
            val logoHeight = imageHeight / 5

            val logoBitmap = Bitmap.createBitmap(logoWidth, logoHeight, Bitmap.Config.ARGB_8888)
            val logoCanvas = Canvas(logoBitmap)

            it.setBounds(0, 0, logoWidth, logoHeight)
            it.draw(logoCanvas)

            val logoLeft = 5
            val logoTop = 5
            canvas.drawBitmap(logoBitmap, logoLeft.toFloat(), logoTop.toFloat(), null)
        }

        return bitmap
    }

    fun generateImageBitmapText(personnelList: ArrayList<Personnel>): Bitmap {
        val bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        when(personnelList.size) {
            1 -> canvas = addOnePersonnel(personnelList, canvas)
            2 -> canvas = addTwoPersonnel(personnelList, canvas)
            3 -> canvas = addThreePersonnel(personnelList, canvas)
            4 -> canvas = addFourPersonnel(personnelList, canvas)
        }

        return bitmap
    }

    fun generateImageBitmap(personnelList: ArrayList<Personnel>, context: Context): Bitmap {
        val foiLogo = ContextCompat.getDrawable(context, R.drawable.foilogo75x65)
        val bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        foiLogo?.let {
            val logoWidth = imageWidth / 5
            val logoHeight = imageHeight / 5

            val logoBitmap = Bitmap.createBitmap(logoWidth, logoHeight, Bitmap.Config.ARGB_8888)
            val logoCanvas = Canvas(logoBitmap)

            it.setBounds(0, 0, logoWidth, logoHeight)
            it.draw(logoCanvas)

            val logoLeft = 5
            val logoTop = 5
            canvas.drawBitmap(logoBitmap, logoLeft.toFloat(), logoTop.toFloat(), null)
        }

        when(personnelList.size) {
            1 -> canvas = addOnePersonnel(personnelList, canvas)
            2 -> canvas = addTwoPersonnel(personnelList, canvas)
            3 -> canvas = addThreePersonnel(personnelList, canvas)
            4 -> canvas = addFourPersonnel(personnelList, canvas)
        }

        return bitmap
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

    private fun addThreePersonnel(receivedList: java.util.ArrayList<Personnel>, canvas: Canvas): Canvas {
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

}