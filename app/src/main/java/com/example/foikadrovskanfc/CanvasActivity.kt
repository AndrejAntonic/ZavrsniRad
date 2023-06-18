package com.example.foikadrovskanfc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class CanvasActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)

        backButton = findViewById(R.id.imgbtn_backArrow)
        backButton.setOnClickListener{
            finish()
        }
    }
}