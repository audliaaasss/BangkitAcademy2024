package com.dicoding.mynotesapp

import android.content.Intent
import android.icu.text.Transliterator.Position
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.dicoding.mynotesapp.adapter.NoteAdapter
import com.dicoding.mynotesapp.databinding.ActivityMainBinding
import com.dicoding.mynotesapp.databinding.ActivityNoteAddUpdateBinding
import com.dicoding.mynotesapp.db.NoteHelper
import com.dicoding.mynotesapp.entity.Note
import com.dicoding.mynotesapp.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteAdapter

    val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data != null) {
            when (result.resultCode) {
                NoteAddUpdateActivity.RESULT_ADD -> {
                    val note = if (Build.VERSION.SDK_INT >= 33) {
                        result.data?.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE, Note::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        result.data?.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE)
                    } as Note

                    adapter.addItem(note)
                    binding.rvNotes.smoothScrollToPosition(adapter.itemCount - 1)
                    showSnackbarMessage("Satu item berhasil ditambahkan")
                }
                NoteAddUpdateActivity.RESULT_UPDATE -> {
                    val note = if (Build.VERSION.SDK_INT >= 33) {
                        result.data?.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE, Note::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        result.data?.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE)
                    } as Note

                    val position = result.data?.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0) ?: 0
                    adapter.updateItem(position, note)
                    binding.rvNotes.smoothScrollToPosition(position)
                    showSnackbarMessage("Satu item berhasil diubah")
                }
                NoteAddUpdateActivity.RESULT_DELETE -> {
                    val position = result?. data?.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0) as Int
                    adapter.removeItem(position)
                    showSnackbarMessage("Satu item berhasil dihapus")
                }
            }
        }
    }

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Notes"

        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.setHasFixedSize(true)

        adapter = NoteAdapter(object : NoteAdapter.OnItemClickCallback {
            override fun onItemClicked(selectedNote: Note?, position: Int?) {
                val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
                intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, selectedNote)
                intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position)
                resultLauncher.launch(intent)
            }
        })
        binding.rvNotes.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
            resultLauncher.launch(intent)
        }

        if (savedInstanceState == null) {
            loadNotesAsync()
        } else {
            val list = if (Build.VERSION.SDK_INT >= 33) {
                savedInstanceState.getParcelableArrayList(EXTRA_STATE, Note::class.java)
            } else {
                @Suppress("DEPRECATION")
                savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            }

            if (list != null) {
                adapter.listNotes = list
            }
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.rvNotes, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun loadNotesAsync() {
        lifecycleScope.launch {
            binding.progressbar.visibility = View.VISIBLE
            val noteHelper = NoteHelper.getInstance(applicationContext)
            noteHelper.open()
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = noteHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
            binding.progressbar.visibility = View.INVISIBLE
            val notes = deferredNotes.await()
            if (notes.size > 0) {
                adapter.listNotes = notes
            } else {
                adapter.listNotes = ArrayList()
                showSnackbarMessage("Tidak ada data saat ini")
            }
            noteHelper.close()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listNotes)
    }
}