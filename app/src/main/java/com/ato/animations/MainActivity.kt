package com.ato.animations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.unit.dp
import com.ato.animations.navigation.Graph
import com.ato.animations.navigation.Node
import com.ato.animations.ui.theme.AnimationsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Graph.addNode(HomeScreen)
        enableEdgeToEdge()
        setContent {
            AnimationsTheme {
                Scaffold {
                    Box(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        ) {
                            Graph.Display(modifier = Modifier.align(Alignment.TopStart))
                        }

                        Column(
                            modifier = Modifier.align(Alignment.BottomStart)
                        ) {

                            Button({
                                Graph.addNode(SecondScreen)
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
}
