package com.example.room.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.room.R
import com.example.room.database.Note
import com.example.room.database.NoteDao
import com.example.room.database.NoteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class TabAdapter (var listNote: List<Note>?): RecyclerView.Adapter<TabAdapter.MyViewHolder>(){
    lateinit var executorService: ExecutorService
    lateinit var mNotesDao: NoteDao

    class MyViewHolder (view: View):RecyclerView.ViewHolder(view){
        val tittle = view.findViewById<TextView>(R.id.title_detail)
        val note = view.findViewById<TextView>(R.id.note_detail)
        val btnUpdate = view.findViewById<Button>(R.id.update_btn)
        val btnDelete = view.findViewById<Button>(R.id.delete_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if(listNote!=null){
            return listNote!!.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(holder.itemView.context)
        mNotesDao = db!!.NoteDao()!!

        holder.tittle.text = "${listNote?.get(position)?.title}"
        holder.note.text = "${listNote?.get(position)?.note}"

        holder.btnUpdate.setOnClickListener {
            val intentToDetail = Intent(holder.itemView.context, DetailActivity::class.java)
            intentToDetail.putExtra("ID", listNote?.get(position)?.id)
            intentToDetail.putExtra("TITTLE", listNote?.get(position)?.title)
            intentToDetail.putExtra("NOTE", listNote?.get(position)?.note)
            intentToDetail.putExtra("COMMAND", "UPDATE")
            holder.itemView.context.startActivity(intentToDetail)
        }

        holder.btnDelete.setOnClickListener {
            val noteId = listNote?.get(position)?.id
            noteId?.let { deleteNoteById(it) }
            Toast.makeText(holder.itemView.context, "Berhasil Menghapus Data", Toast.LENGTH_SHORT)
                .show()
            true
        }

    }
    private fun deleteNoteById(noteId: Int) {
        executorService.execute {
            mNotesDao.deleteById(noteId)
        }
    }
}