  package com.dicoding.mybooksapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

  class MainActivity : AppCompatActivity() {
    private lateinit var rv_books: RecyclerView
    private val list = ArrayList<books>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv_books = findViewById(R.id.rv_books)
        rv_books.setHasFixedSize(true)
        
        list.addAll(getListBooks())
        showRecyclerList()
    }

      private fun getListBooks(): ArrayList<books> {
          val dataName = resources.getStringArray(R.array.data_name)
          val dataDescription = resources.getStringArray(R.array.data_description)
          val dataItem = resources.getStringArray(R.array.item_books)
          val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
          val listBooks = ArrayList<books>()
          for (i in dataName.indices) {
              val books = books(dataName[i], dataDescription[i], dataPhoto.getResourceId(i, -1), dataItem[i])
              listBooks.add(books)
          }
          return listBooks
      }

      private fun showRecyclerList() {
          rv_books.layoutManager = LinearLayoutManager(this)
          val listBooks = ListBooks(list)
          rv_books.adapter = listBooks

      }


  }