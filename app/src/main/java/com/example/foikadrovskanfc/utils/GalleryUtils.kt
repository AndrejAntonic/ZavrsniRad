package com.example.foikadrovskanfc.utils

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Date

class GalleryUtils {
    private val REQUEST_WRITE_STORAGE = 112

    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, displayName: String, activity: AppCompatActivity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val filename = "${displayName}_${Date().time}"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
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
            if (checkWriteStoragePermission(activity)) {
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val file = File(path, "${displayName}_${Date().time}.jpg")

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

    private fun checkWriteStoragePermission(activity: AppCompatActivity): Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_STORAGE)
                return false
            }
        }
        return true
    }
}