package com.example.room.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.room.database.Note
import com.example.room.database.NoteDao
import com.example.room.database.NoteRoomDatabase
import com.example.room.databinding.ActivityDetailBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService
    private var updateId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.NoteDao()!!
        var command = intent.getStringExtra("COMMAND")


        with(binding){

            if(command=="UPDATE"){
                binding.updatebtn.isVisible = true
                binding.savebtn.isVisible = false
                updateId = intent.getIntExtra("ID", 0)
                var item_title = intent.getStringExtra("TITTLE")
                var item_note = intent.getStringExtra("NOTE")

                binding.titleEdit.setText(item_title.toString())
                binding.noteEdit.setText(item_note.toString())
            }else{
                binding.updatebtn.isVisible = false
                binding.savebtn.isVisible = true
            }


            savebtn.setOnClickListener(View.OnClickListener {
                if (validateInput()){
                    insert(
                        Note(
                            title = titleEdit.text.toString(),
                            note = noteEdit.text.toString()
                        )
                    )
                    setEmptyField()
                    val IntentToHome = Intent(this@DetailActivity, MainActivity::class.java)
                    Toast.makeText(this@DetailActivity, "Data Berhasil Ditambahkan Bos!", Toast.LENGTH_SHORT).show()
                    startActivity(IntentToHome)
                }else{
                    Toast.makeText(this@DetailActivity, "Hey, Kolom Tidak Boleh Kosong !!", Toast.LENGTH_SHORT).show()
                }
            })

            updatebtn.setOnClickListener {
                if(validateInput()){
                    update(
                        Note(
                            id = updateId,
                            title = titleEdit.text.toString(),
                            note = noteEdit.text.toString()
                        )
                    )
                    updateId = 0
                    setEmptyField()
                    val IntentToMain = Intent(this@DetailActivity, MainActivity::class.java)
                    Toast.makeText(this@DetailActivity, "Data Berhasil DiUpdate Bos!", Toast.LENGTH_SHORT).show()
                    startActivity(IntentToMain)
                }else{
                    Toast.makeText(this@DetailActivity, "Hey, Kolom Tidak Boleh Kosong !!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun insert(note: Note) {
        executorService.execute { mNotesDao.insert(note) }
    }

    private fun delete(note: Note) {
        executorService.execute { mNotesDao.delete(note) }
    }

    private fun update(note: Note) {
        executorService.execute { mNotesDao.update(note) }
    }

    override fun onResume() {
        super.onResume()
//        getAllNotes()
    }

    private fun setEmptyField() {
        with(binding) {
            titleEdit.setText("")
            noteEdit.setText("")
        }
    }

    private fun validateInput(): Boolean {
        with(binding) {
            if(titleEdit.text.toString()!="" && noteEdit.text.toString()!="" ){
                return true
            }else{
                return false
            }
        }

    }
}