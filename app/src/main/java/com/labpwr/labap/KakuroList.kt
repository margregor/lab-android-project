package com.labpwr.labap

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.labpwr.labap.ui.theme.LabapTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

class KakuroList : ComponentActivity() {
    lateinit var db : SQLiteDatabase
    val completionStatus = mutableStateMapOf<String, Int>()

    override fun onDestroy() {
        db.close();
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        completionStatus.clear()
        Log.w("KAKURO", "onresume");
        with(db.query("KakuroCompletedBoards", arrayOf("board_id", "width", "height"), null, null , null, null, null)) {
            while (moveToNext()) {
                completionStatus.put(getString(0), 1)
                if (completionStatus.contains("${getInt(1)}x${getInt(2)}"))
                    completionStatus["${getInt(1)}x${getInt(2)}"] = completionStatus["${getInt(1)}x${getInt(2)}"]!! + 1
                else
                    completionStatus["${getInt(1)}x${getInt(2)}"] = 1
            }
            close()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = GameSaveDataDbHelper(this).writableDatabase;

        val sizes = assets.list("generatedBoards")!!.toList().sortedBy { it.split("x")[0].toInt() }
        enableEdgeToEdge()
        setContent {
            LabapTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    var selectedSize by remember { mutableStateOf("0") }
                    LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxWidth()) {
                        items (sizes) { size->
                            val completed = completionStatus[size] == assets.list("generatedBoards/$size")!!.size
                            Button(
                                onClick = {selectedSize = size},
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
                                Text(
                                    text = "$size${
                                        if (completed) "✅" else ""
                                    }",
                                )
                            }
                            if (selectedSize == size) {
                                val boards = assets.list("generatedBoards/$size")!!.toList()
                                Column {
                                    boards.forEach { board ->
                                        val completed by mutableStateOf(completionStatus.contains(board.split(".").first()))
                                        Button(
                                            onClick = {Intent(this@KakuroList, KakuroGame::class.java).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                putExtra("board", "generatedBoards/$size/$board")
                                                startActivity(this)
                                            }},
                                            modifier = Modifier.fillMaxWidth().padding(horizontal = 50.dp)) {
                                            Text(
                                                text = "$board${if (completed) "✅" else ""}",
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

