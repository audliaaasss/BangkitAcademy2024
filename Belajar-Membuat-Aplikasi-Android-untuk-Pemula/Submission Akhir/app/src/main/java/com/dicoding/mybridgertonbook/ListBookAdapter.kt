package com.dicoding.mybridgertonbook
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListBookAdapter(private val listBook: ArrayList<Book>) : RecyclerView.Adapter<ListBookAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_book, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (title, description, cover) = listBook[position]
        holder.imgCover.setImageResource(cover)
        holder.tvTitle.text = title
        holder.tvDescription.text = description
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listBook[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listBook.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCover: ImageView = itemView.findViewById(R.id.img_book_cover)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_book_title)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_book_description)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Book)
    }

}