package com.example.persistx_exp_9

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "PersistX.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE notes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                body TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS notes")
        onCreate(db)
    }

    fun insertNote(title: String, body: String): Long {
        val values = ContentValues().apply {
            put("title", title)
            put("body", body)
        }

        return writableDatabase.insert("notes", null, values)
    }

    fun getNotes(): List<Triple<Int, String, String>> {
        val notes = mutableListOf<Triple<Int, String, String>>()

        readableDatabase.rawQuery(
            "SELECT id, title, body FROM notes ORDER BY id DESC",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                notes.add(
                    Triple(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                    )
                )
            }
        }

        return notes
    }

    fun deleteNote(id: Int): Int {
        return writableDatabase.delete(
            "notes",
            "id = ?",
            arrayOf(id.toString())
        )
    }
}
