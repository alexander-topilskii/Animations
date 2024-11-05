package com.ato.animations.simple_animation

import android.content.res.Configuration
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Full Preview", showSystemUi = true)
fun PreviewSquareHeightSample() {
    DisplaySquareHeightSample()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DisplaySquareHeightSample() {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("500") }

    var fastOutSlowInEasing by remember { mutableStateOf(true) }
    var linearOutSlowInEasing by remember { mutableStateOf(false) }
    var fastOutLinearInEasing by remember { mutableStateOf(false) }
    var stiffnessLow by remember { mutableStateOf(false) }

    val animationSpec = when {
        fastOutSlowInEasing -> tween(
            durationMillis = text.toIntOrNull() ?: 500,
            easing = FastOutSlowInEasing
        )

        linearOutSlowInEasing -> tween(
            durationMillis = text.toIntOrNull() ?: 500,
            easing = LinearOutSlowInEasing
        )

        fastOutLinearInEasing -> tween(
            durationMillis = text.toIntOrNull() ?: 500,
            easing = FastOutLinearInEasing
        )

        stiffnessLow -> spring<Dp>(
            dampingRatio = Spring.DampingRatioMediumBouncy,  // Controls how "bouncy" the spring is
            stiffness = Spring.StiffnessVeryLow  // Lower stiffness means more springiness
        )

        else -> tween(
            durationMillis = text.toIntOrNull() ?: 500,
            easing = LinearEasing
        )
    }

    val height: Dp by animateDpAsState(
        targetValue = if (expanded) 200.dp else 50.dp,
        animationSpec = animationSpec,
        label = "height animation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(height)
                    .width(150.dp)
                    .background(Color.Blue)
            ) { }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Speed: $text") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowColumn(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextCheckBox("fastOutSlowInEasing", fastOutSlowInEasing) {
                fastOutSlowInEasing = !fastOutSlowInEasing
            }
            TextCheckBox("linearOutSlowInEasing", linearOutSlowInEasing) {
                linearOutSlowInEasing = !linearOutSlowInEasing
            }
            TextCheckBox("fastOutLinearInEasing", fastOutLinearInEasing) {
                fastOutLinearInEasing = !fastOutLinearInEasing
            }
            TextCheckBox("stiffnessLow", stiffnessLow) {
                stiffnessLow = !stiffnessLow
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button({
            expanded = !expanded
        }) {
            Text("Change Invisible now: $height")
        }
    }
}