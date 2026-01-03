package com.labpwr.labap

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.labpwr.labap.ui.theme.LabapTheme


class GameSaveDataDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Highscores (
                game_name TEXT PRIMARY KEY,
                score INTEGER
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS KakuroInputs (
                board_id TEXT,
                x_pos INTEGER,
                y_pos INTEGER,
                value INTEGER,
                PRIMARY KEY (board_id, x_pos, y_pos)
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS KakuroCompletedBoards (
                board_id TEXT PRIMARY KEY,
                width INTEGER,
                height INTEGER
            )
        """)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("""
            DROP TABLE IF EXISTS Highscores
        """)
        db.execSQL("""
            DROP TABLE IF EXISTS KakuroInputs
        """)
        db.execSQL("""
            DROP TABLE IF EXISTS KakuroCompletedBoards
        """)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 4
        const val DATABASE_NAME = "GameSaveData.db"
    }
}

class MainActivity : ComponentActivity() {
    lateinit var db : SQLiteDatabase
    val scores = mutableStateMapOf<String, Int>()

    override fun onResume() {
        super.onResume()
        val cursor = db.query("Highscores", arrayOf("game_name", "score") , null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val gameName = getString(0)
                val score = getInt(1)
                scores.put(gameName, score)
            }
        }
        cursor.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = GameSaveDataDbHelper(this).readableDatabase;

        val cursor = db.query("Highscores", arrayOf("game_name", "score") , null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val gameName = getString(0)
                val score = getInt(1)
                scores.put(gameName, score)
            }
        }
        cursor.close()

        enableEdgeToEdge()
        setContent {
            LabapTheme {
                Column( modifier = Modifier.fillMaxWidth(1f) ) {
                    Spacer(modifier = Modifier.height(80.dp))
                    Button(
                        modifier = Modifier.padding(1.dp),
                        onClick = {
                            Intent(this@MainActivity, NativeLoader::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                putExtra("GameId", 1)
                                startActivity(this)
                            }
                        }
                    ) {
                        Text(text = "Bird Game (High score: ${if (scores.contains("birdGame")) scores["birdGame"] else 0})")
                    }
                    Button(
                        modifier = Modifier.padding(1.dp),
                        onClick = {
                            Intent(this@MainActivity, NativeLoader::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                putExtra("GameId", 2)
                                startActivity(this)
                            }
                        }
                    ) {
                        Text(text = "Accelerator Game (High score: ${if (scores.contains("obstacleGame")) scores["obstacleGame"] else 0})")
                    }
                    Button(
                        modifier = Modifier.padding(1.dp),
                        onClick = {
                            Intent(this@MainActivity, KakuroList::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(this)
                            }
                        }
                    ) {
                        Text(text = "Kakuro")
                    }

                }
            }
        }
    }

    override fun onDestroy() {
        db.close();
        super.onDestroy()
    }
}
