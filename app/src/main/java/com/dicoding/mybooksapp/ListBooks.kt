package com.dicoding.mybooksapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListBooks:  RecyclerView.Adapter<ListBooks.ListViewHolder>() {

    private lateinit var onItemClickListener: OnItemClickListener
    private val listBooks = ArrayList<books>()

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_books, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listBooks.size



    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, description, photo, item) = listBooks[position]
        holder.imgPhoto.setImageResource(photo)
        holder.tvName.text = name
        holder.tvDescription.text = description
        holder.tvItem.text = item

    }
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)
        val tvItem: TextView = itemView.findViewById(R.id.tv_item)
    }

    interface OnItemClickListener {
        fun onitemClicked(books: ListBooks)
    }

}