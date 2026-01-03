package com.labpwr.labap

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.labpwr.labap.ui.theme.LabapTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

class KakuroList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sizes = assets.list("generatedBoards")!!.toList().sortedBy { it.split("x")[0].toInt() }
        enableEdgeToEdge()
        setContent {
            LabapTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    var selectedSize by remember { mutableStateOf("0") }
                    LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxWidth()) {
                        items (sizes) { size->
                            Button(
                                onClick = {selectedSize = size},
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
                                Text(
                                    text = size,
                                )
                            }
                            if (selectedSize == size) {
                                val boards = assets.list("generatedBoards/$size")!!.toList()
                                Column {
                                    boards.forEach { board ->
                                        Button(
                                            onClick = {Intent(this@KakuroList, KakuroGame::class.java).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                putExtra("board", "generatedBoards/$size/$board")
                                                startActivity(this)
                                            }},
                                            modifier = Modifier.fillMaxWidth().padding(horizontal = 50.dp)) {
                                            Text(
                                                text = board,
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

