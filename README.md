# MAD Experiment 9: Data Persistence Using SharedPreferences and SQLite

## Aim
To demonstrate data persistence in an Android application using SharedPreferences and SQLite.

## Description
**PersistX** is an Android application developed using Kotlin and XML with a premium purple interface. It demonstrates saving profile/session information and storing structured records in local SQLite databases.

## Technologies Used
- Android Studio
- Kotlin
- XML layouts
- SharedPreferences
- SQLite

## Features
- Premium purple-themed UI
- Login and profile/session persistence
- Save and restore user information
- Local database storage
- Inspect database tables using Android Studio Database Inspector

## Project Structure
```text
PersistX_Exp-9/
├── app/
│   └── src/main/
│       ├── java/com/example/persistx_exp_9/
│       │   └── MainActivity.kt
│       ├── res/layout/
│       │   └── activity_main.xml
│       └── AndroidManifest.xml
├── screenshots/
│   ├── 1_Login_Screen.png
│   ├── 2_profile&logout_button.png
│   ├── 3_User_database.png
│   └── 4_MainActivity.kt.png
└── README.md
```
## Source Code

### 1. SharedPreferences Example
```kotlin
val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)

// Save data
prefs.edit().putString("name", "Vishwajeet").apply()

// Retrieve data
val name = prefs.getString("name", "")
```

### 2. DatabaseHelper.kt
```kotlin
package com.example.persistx

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "PersistX.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE notes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, body TEXT NOT NULL)"
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase, oldVersion: Int, newVersion: Int
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
            "SELECT id, title, body FROM notes ORDER BY id DESC", null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                notes.add(
                    Triple(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
                )
            }
        }
        return notes
    }

    fun deleteNote(id: Int): Int {
        return writableDatabase.delete("notes", "id = ?", arrayOf(id.toString()))
    }
}
```

### 3. MainActivity.kt — Core Persistence Logic
```kotlin
package com.example.persistx

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var db: DatabaseHelper
    private lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)
        prefs = getSharedPreferences("PersistXPrefs", MODE_PRIVATE)

        val etName = findViewById<EditText>(R.id.etName)
        val tvStatus = findViewById<TextView>(R.id.tvProfileStatus)
        etName.setText(prefs.getString("user_name", "") ?: "")

        findViewById<Button>(R.id.btnSaveProfile).setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                etName.error = "Enter your name"
            } else {
                prefs.edit().putString("user_name", name).apply()
                tvStatus.text = "Welcome back, $name!"
                Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
            }
        }

        // For notes: call db.insertNote(title, body), db.getNotes(),
        // and db.deleteNote(id) from the note UI event handlers.
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }
}
```

> **Note:** The `MainActivity.kt` snippet demonstrates SharedPreferences and database setup. Add note UI event handlers using the methods in `DatabaseHelper.kt`, or use the complete activity implementation in your project.
## Screenshots

## Screenshots

<table>
<tr>
<td align="center"><b>1. Login Screen</b><br><img src="https://raw.githubusercontent.com/Vishwajeetsingh22/MAD-Experiment-9-Data-Persistence/main/screenshots/1_Login_Screen.png" width="100%" alt="Login Screen"></td>
<td align="center"><b>2. Profile and Logout</b><br><img src="https://raw.githubusercontent.com/Vishwajeetsingh22/MAD-Experiment-9-Data-Persistence/main/screenshots/2_profile_logout_button.png" width="100%" alt="Profile and Logout"></td>
</tr>
<tr>
<td align="center"><b>3. SQLite Database Inspector</b><br><img src="https://raw.githubusercontent.com/Vishwajeetsingh22/MAD-Experiment-9-Data-Persistence/main/screenshots/3_User_database.png" width="100%" alt="SQLite Database"></td>
<td align="center"><b>4. MainActivity.kt</b><br><img src="https://raw.githubusercontent.com/Vishwajeetsingh22/MAD-Experiment-9-Data-Persistence/main/screenshots/4_MainActivity.kt.png" width="100%" alt="MainActivity Kotlin Code"></td>
</tr>
</table>
## Data Persistence
- **SharedPreferences** stores small key-value information, such as saved profile details.
- **SQLite** stores structured records in local database tables.
- Data remains on the device between app launches unless app data is cleared or the app is uninstalled.

## How to Run
1. Open the project in Android Studio.
2. Wait for Gradle sync to finish.
3. Run the app on an Android emulator or connected Android device.
4. Use the app to save profile information and create records.
5. To inspect SQLite, open **View → Tool Windows → App Inspection → Database Inspector** while the app is running.

## Author
**Vishwajeet Singh**  
**USN:** 25MCAR0219  
**JAIN DEEMED TO BE UNIVERSITY**

## Result
Data persistence using SharedPreferences and SQLite was demonstrated in the PersistX Android application.
