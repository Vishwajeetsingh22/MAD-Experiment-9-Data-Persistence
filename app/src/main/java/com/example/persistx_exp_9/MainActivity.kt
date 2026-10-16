package com.example.persistx_exp_9

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit

class MainActivity : Activity() {

    private lateinit var db: NotesDatabaseHelper
    private lateinit var prefs: android.content.SharedPreferences

    private lateinit var etName: EditText
    private lateinit var etTitle: EditText
    private lateinit var etBody: EditText
    private lateinit var tvStatus: TextView
    private lateinit var notesContainer: LinearLayout

    private val purple = Color.rgb(124, 58, 237)
    private val dark = Color.rgb(33, 22, 61)
    private val muted = Color.rgb(129, 119, 148)
    private val light = Color.rgb(237, 233, 254)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = NotesDatabaseHelper(this)

        prefs = getSharedPreferences(
            "PersistXPrefs",
            MODE_PRIVATE
        )

        etName = findViewById(R.id.etName)
        etTitle = findViewById(R.id.etNoteTitle)
        etBody = findViewById(R.id.etNoteBody)
        tvStatus = findViewById(R.id.tvProfileStatus)
        notesContainer = findViewById(R.id.notesContainer)

        // Restore SharedPreferences data
        val savedName = prefs.getString("user_name", "") ?: ""
        etName.setText(savedName)

        tvStatus.text = if (savedName.isNotBlank()) {
            getString(R.string.welcome_back, savedName)
        } else {
            getString(R.string.profile_status_default)
        }

        // Save profile using SharedPreferences
        findViewById<Button>(R.id.btnSaveProfile)
            .setOnClickListener {
                val name = etName.text.toString().trim()

                if (name.isEmpty()) {
                    etName.error = "Enter your name"
                    return@setOnClickListener
                }

                prefs.edit {
                    putString("user_name", name)
                }

                tvStatus.text = getString(R.string.welcome_back, name)
                Toast.makeText(
                    this,
                    "Profile saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }

        // Logout action: clear session and return to LoginActivity
        findViewById<Button>(R.id.btnLogout)
            .setOnClickListener {
                prefs.edit {
                    putBoolean("is_logged_in", false)
                }

                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

        // Insert note into SQLite
        findViewById<Button>(R.id.btnAddNote)
            .setOnClickListener {
                val title = etTitle.text.toString().trim()
                val body = etBody.text.toString().trim()

                if (title.isEmpty() || body.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Enter both title and note",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val id = db.insertNote(title, body)

                if (id != -1L) {
                    etTitle.text.clear()
                    etBody.text.clear()
                    loadNotes()

                    Toast.makeText(
                        this,
                        "Note saved to SQLite",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Unable to save note",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        loadNotes()
    }

    private fun loadNotes() {
        notesContainer.removeAllViews()
        val notes = db.getNotes()

        if (notes.isEmpty()) {
            val empty = TextView(this).apply {
                text = getString(R.string.no_notes_yet)
                textSize = 14f
                setTextColor(muted)
                gravity = Gravity.CENTER
                setPadding(8, 24, 8, 24)
            }
            notesContainer.addView(empty)
            return
        }

        for ((id, title, body) in notes) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(14, 14, 14, 14)
                background = roundedBackground(light, 16)
            }

            val titleView = TextView(this).apply {
                text = title
                textSize = 16f
                setTextColor(dark)
                setTypeface(null, Typeface.BOLD)
            }

            val bodyView = TextView(this).apply {
                text = body
                textSize = 14f
                setTextColor(muted)
                setPadding(0, 6, 0, 8)
            }

            val deleteButton = Button(this).apply {
                text = getString(R.string.delete_note)
                textSize = 12f
                isAllCaps = false
                setTextColor(Color.WHITE)
                background = roundedBackground(purple, 12)
                setOnClickListener {
                    if (db.deleteNote(id) > 0) {
                        loadNotes()
                        Toast.makeText(
                            this@MainActivity,
                            R.string.note_deleted,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            card.addView(titleView)
            card.addView(bodyView)
            card.addView(
                deleteButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    46
                )
            )

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }

            notesContainer.addView(card, params)
        }
    }

    private fun roundedBackground(
        color: Int,
        radiusDp: Int
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radiusDp * resources.displayMetrics.density
        }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }
}
