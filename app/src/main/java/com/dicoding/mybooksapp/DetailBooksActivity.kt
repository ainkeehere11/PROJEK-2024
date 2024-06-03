package com.dicoding.mybooksapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class DetailBooksActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ detail_books)

        val bookss = intent.getParcelableExtra<books>(MainActivity.INTENT_PARCELABLE)

        val photo = findViewById<ImageView>(R.id.img_item_photo)
        val name = findViewById<TextView>(R.id.tv_item_name)
        val description = findViewById<TextView>(R.id.tv_item_description)

        photo.setImageResource(bookss?.photo!!)
        name.text = bookss.name
        description.text = bookss.description
    }
}