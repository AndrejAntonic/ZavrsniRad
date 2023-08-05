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

    fun applyFloydSteinbergDithering(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val oldPixel = newBitmap.getPixel(x, y)
                val grayscale = (Color.red(oldPixel) + Color.green(oldPixel) + Color.blue(oldPixel)) / 3

                // Set the grayscale color to the pixel
                newBitmap.setPixel(x, y, Color.rgb(grayscale, grayscale, grayscale))

                val newPixel = newBitmap.getPixel(x, y)
                val error = oldPixel - newPixel

                // Distribute the error to neighboring pixels
                if (x + 1 < width) {
                    applyErrorToPixel(newBitmap, x + 1, y, error, 7 / 16f)
                }
                if (x - 1 >= 0 && y + 1 < height) {
                    applyErrorToPixel(newBitmap, x - 1, y + 1, error, 3 / 16f)
                }
                if (y + 1 < height) {
                    applyErrorToPixel(newBitmap, x, y + 1, error, 5 / 16f)
                }
                if (x + 1 < width && y + 1 < height) {
                    applyErrorToPixel(newBitmap, x + 1, y + 1, error, 1 / 16f)
                }
            }
        }

        return newBitmap
    }

    private fun applyErrorToPixel(bitmap: Bitmap, x: Int, y: Int, error: Int, weight: Float) {
        val oldPixel = bitmap.getPixel(x, y)
        val newPixel = Color.rgb(
            clamp(Color.red(oldPixel) + (error * weight).toInt(), 0, 255),
            clamp(Color.green(oldPixel) + (error * weight).toInt(), 0, 255),
            clamp(Color.blue(oldPixel) + (error * weight).toInt(), 0, 255)
        )
        bitmap.setPixel(x, y, newPixel)
    }

    private fun clamp(value: Int, min: Int, max: Int): Int {
        return value.coerceIn(min, max)
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    fun byteArrayToBitmap(it: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(it, 0, it.size)
    }
}