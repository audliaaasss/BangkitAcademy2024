package com.dicoding.mybridgertonbook

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var rvBooks: RecyclerView
    private val list = ArrayList<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Bridgerton Books"

        rvBooks = findViewById(R.id.rv_books)
        rvBooks.setHasFixedSize(true)

        list.addAll(getListBooks())
        showRecyclerList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.about_page -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getListBooks(): ArrayList<Book> {
        val dataTitle = resources.getStringArray(R.array.data_title)
        val dataSynopsys = resources.getStringArray(R.array.data_synopsys)
        val dataCover = resources.obtainTypedArray(R.array.data_cover)
        val dataPublished = resources.getStringArray(R.array.content_published)
        val dataMainChar = resources.getStringArray(R.array.content_mainchar)
        val dataPrevBook = resources.getStringArray(R.array.content_prev_book)
        val dataNextBook = resources.getStringArray(R.array.content_next_book)

        val listBook = ArrayList<Book>()
        for (i in dataTitle.indices) {
            val book = Book(
                dataTitle[i],
                dataSynopsys[i],
                dataCover.getResourceId(i, -1),
                dataPublished.getOrElse(i) {""},
                dataMainChar.getOrElse(i) {""},
                dataPrevBook.getOrElse(i) {""},
                dataNextBook.getOrElse(i) {""}
            )
            listBook.add(book)
        }
        dataCover.recycle()
        return listBook
    }

    private fun showRecyclerList() {
        rvBooks.layoutManager = LinearLayoutManager(this)
        val listBookAdapter = ListBookAdapter(list)
        rvBooks.adapter = listBookAdapter

        listBookAdapter.setOnItemClickCallback(object : ListBookAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Book) {
                showSelectedBook(data)
            }
        })
    }

    private fun showSelectedBook(book: Book) {
        val intent = Intent(this@MainActivity, BookDetail::class.java)
        intent.putExtra("EXTRA_BOOK", book)
        startActivity(intent)
    }

}