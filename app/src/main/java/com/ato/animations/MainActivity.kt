package com.ato.animations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ato.animations.navigation.Graph
import com.ato.animations.navigation.GraphView
import com.ato.animations.navigation.Node
import com.ato.animations.ui.theme.AnimationsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Graph.addNode(HomeScreen())
        enableEdgeToEdge()
        setContent {
            AnimationsTheme {
                Scaffold {
                    Column(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize()
                    ) {
                        Graph.getRootNode()?.let { GraphView(it) }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .background(Color.Green.copy(alpha = 0.1f))
                        ) {
                            Graph.Display(modifier = Modifier.align(Alignment.TopStart))
                        }

                        Button({
                            Graph.addNode(SecondScreen())
                        }) {
                            Text("1")
                        }
                        Button({

                        }) {
                            Text("2")
                        }
                        Button({

                        }) {
                            Text("3")
                        }
                    }
                }
            }
        }
    }
}
