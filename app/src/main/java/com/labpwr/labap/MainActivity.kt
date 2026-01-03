package com.labpwr.labap

import android.content.Intent
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.labpwr.labap.ui.theme.LabapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                        Text(text = "Game1")
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
                        Text(text = "Game2")
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
                        Text(text = "Game3")
                    }

                }
            }
        }
    }
}
