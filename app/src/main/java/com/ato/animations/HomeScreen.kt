package com.ato.animations

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ato.animations.navigation.Node

class HomeScreen : Node() {

    @Composable
    override fun Display(modifier: Modifier) {
        Box(
            modifier = modifier
        ) {
            Text("kek")
        }
    }
}

class SecondScreen() : Node() {

    @Composable
    override fun Display(modifier: Modifier) {
        Box(
            modifier = modifier
        ) {
            Text("kek: 2")
        }
    }
}