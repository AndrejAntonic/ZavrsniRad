package com.example.foikadrovskanfc

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.graphics.Canvas
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.nfc.tech.NfcV
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foikadrovskanfc.entities.Personnel
import com.example.foikadrovskanfc.utils.GalleryUtils
import com.example.foikadrovskanfc.utils.NfcUtils
import com.example.foikadrovskanfc.utils.PictureUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Date

class CanvasActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var imageView: ImageView
    private lateinit var saveButton: Button
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var generatedImageBitmap: Bitmap
    private lateinit var dihteringImageBitmap: Bitmap
    private val pictureUtils = PictureUtils()
    private val galleryUtils = GalleryUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)
        val statusBarColor = ContextCompat.getColor(this, R.color.red)
        changeStatusBarColor(window, statusBarColor)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        checkNFC()
        generatedImageBitmap = pictureUtils.generateImageBitmap(intent.getSerializableExtra("personnelList") as ArrayList<Personnel>, this)
        dihteringImageBitmap = pictureUtils.applyFloydSteinbergDithering(generatedImageBitmap)

        backButton = findViewById(R.id.imgbtn_backArrow)
        backButton.setOnClickListener{
            finish()
        }

        saveButton = findViewById(R.id.btn_saveImage)
        saveButton.setOnClickListener {
            galleryUtils.saveBitmapToGallery(this, generatedImageBitmap, "FOI-kadrovska", this@CanvasActivity)
        }

        imageView = findViewById(R.id.imgw_generatedImage)
        imageView.setImageBitmap(generatedImageBitmap)
    }

    override fun onResume() {
        super.onResume()

        val intent = Intent(this, CanvasActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()

        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

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
            else Log.e("debug", "Unsupported tag technology")
        }
    }

    private fun checkNFC() {
        if(!nfcAdapter.isEnabled) {
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