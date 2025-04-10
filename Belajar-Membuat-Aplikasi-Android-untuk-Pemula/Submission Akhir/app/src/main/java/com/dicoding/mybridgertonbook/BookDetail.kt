package com.dicoding.mybridgertonbook

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BookDetail : AppCompatActivity() {
    private lateinit var dataBook: Book

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        dataBook = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("EXTRA_BOOK", Book::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Book>("EXTRA_BOOK")
        } ?: return

        val tvTitle: TextView = findViewById(R.id.tv_book_title)
        val tvSynopsys: TextView = findViewById(R.id.tv_book_synopsys)
        val imgCover: ImageView = findViewById(R.id.img_book_cover)
        val tvPublishedDate: TextView = findViewById(R.id.tv_published_date)
        val tvMainChar: TextView = findViewById(R.id.tv_main_char)
        val tvPrevBook: TextView = findViewById(R.id.tv_prev_book)
        val tvNextBook: TextView = findViewById(R.id.tv_next_book)

        tvTitle.text = dataBook.title
        tvSynopsys.text = dataBook.synopsys
        imgCover.setImageResource(dataBook.cover)
        tvPublishedDate.text = dataBook.publishedDate
        tvMainChar.text = dataBook.mainCharacters
        tvPrevBook.text = dataBook.prevBook
        tvNextBook.text = dataBook.nextBook

        val shareButton: FloatingActionButton = findViewById(R.id.action_share)
        shareButton.setOnClickListener {
            shareBook()
        }
    }

    private fun shareBook() {
        val shareText = """
            Check out this Bridgerton book:
            "${dataBook.title}"
            
            Synopsys: ${dataBook.synopsys}
            
            Main Characters: ${dataBook.mainCharacters}
            Published: ${dataBook.publishedDate}
        """.trimIndent()

        val shareIntent = ShareCompat.IntentBuilder(this)
            .setType("text/plain")
            .setText(shareText)
            .intent

        startActivity(Intent.createChooser(shareIntent, "Share Book Details"))
    }

}