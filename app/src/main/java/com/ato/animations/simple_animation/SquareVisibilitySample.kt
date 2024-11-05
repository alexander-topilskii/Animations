package com.ato.animations.simple_animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun PreviewSquareSample() {
    DisplaySample()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DisplaySample() {
    var visible by remember { mutableStateOf(true) }
    var fadeInEnabled by remember { mutableStateOf(true) }
    var expandInEnabled by remember { mutableStateOf(true) }
    var slideInEnabled by remember { mutableStateOf(true) }
    var scaleInEnabled by remember { mutableStateOf(true) }

    var fadeOutEnabled by remember { mutableStateOf(true) }
    var shrinkOutEnabled by remember { mutableStateOf(true) }
    var slideOutEnabled by remember { mutableStateOf(true) }
    var scaleOutEnabled by remember { mutableStateOf(true) }

    val enterTransition: EnterTransition =
        getEnterTransition(
            fadeInEnabled = fadeInEnabled,
            expandInEnabled = expandInEnabled,
            slideInEnabled = slideInEnabled,
            scaleInEnabled = scaleInEnabled
        )

    val exitTransition: ExitTransition =
        getExitTransition(
            fadeOutEnabled = fadeOutEnabled,
            shrinkOutEnabled = shrinkOutEnabled,
            slideOutEnabled = slideOutEnabled,
            scaleOutEnabled = scaleOutEnabled
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
            androidx.compose.animation.AnimatedVisibility(
                visible = visible,
                enter = enterTransition,
                exit = exitTransition,
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .width(150.dp)
                        .background(Color.Blue)
                ) { }
            }
        }

        FlowRow(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextCheckBox("fadeIn", fadeInEnabled) { fadeInEnabled = !fadeInEnabled }
            TextCheckBox("expandIn", expandInEnabled) { expandInEnabled = !expandInEnabled }
            TextCheckBox("slideInEnabled", slideInEnabled) { slideInEnabled = !slideInEnabled }
            TextCheckBox("scaleInEnabled", scaleInEnabled) { scaleInEnabled = !scaleInEnabled }
        }
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextCheckBox("fadeOutEnabled", fadeOutEnabled) { fadeOutEnabled = !fadeOutEnabled }
            TextCheckBox("shrinkOutEnabled", shrinkOutEnabled) { shrinkOutEnabled = !shrinkOutEnabled }
            TextCheckBox("slideOutEnabled", slideOutEnabled) { slideOutEnabled = !slideOutEnabled }
            TextCheckBox("scaleInEnabled", scaleOutEnabled) { scaleOutEnabled = !scaleOutEnabled }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button({
            visible = !visible
        }) {
            Text("Change Invisible now: $visible")
        }
    }

    Text("Sampe")
}

@Composable
private fun getExitTransition(
    fadeOutEnabled: Boolean,
    shrinkOutEnabled: Boolean,
    slideOutEnabled: Boolean,
    scaleOutEnabled: Boolean
) = try {
    // Initialize an empty list of transitions
    var transitions: ExitTransition = ExitTransition.None

    // Dynamically add transitions based on enabled flags
    if (fadeOutEnabled) {
        transitions += fadeOut()
    }
    if (shrinkOutEnabled) {
        transitions += shrinkOut()
    }
    if (slideOutEnabled) {
        transitions += slideOutHorizontally { it } // Can also use slideOutVertically
    }
    if (scaleOutEnabled) {
        transitions += scaleOut()
    }

    // Return combined transitions or None if no transition is enabled
    transitions.takeIf { it != ExitTransition.None } ?: ExitTransition.None
} catch (e: Exception) {
    // Handle any errors and return a safe fallback
    ExitTransition.None
}

@Composable
private fun getEnterTransition(
    fadeInEnabled: Boolean,
    expandInEnabled: Boolean,
    slideInEnabled: Boolean,
    scaleInEnabled: Boolean
): EnterTransition {
    val enterTransition: EnterTransition = try {
        // Initialize an empty list of transitions
        var transitions: EnterTransition = EnterTransition.None

        // Dynamically add transitions based on enabled flags
        if (fadeInEnabled) {
            transitions += fadeIn()
        }
        if (expandInEnabled) {
            transitions += expandIn()
        }
        if (slideInEnabled) {
            transitions += slideInHorizontally { it } // Can also use slideInVertically
        }
        if (scaleInEnabled) {
            transitions += scaleIn()
        }

        // Return combined transitions or None if no transition is enabled
        transitions.takeIf { it != EnterTransition.None } ?: EnterTransition.None
    } catch (e: Exception) {
        // Handle any errors and return a safe fallback
        EnterTransition.None
    }
    return enterTransition
}

@Composable
fun TextCheckBox(text: String, isChecked: Boolean, onChanged: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onChanged.invoke(!isChecked)
            }
            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 2.dp)
    ) {
        Text(text)
        Spacer(modifier = Modifier.width(8.dp))
        Checkbox(
            checked = isChecked,
            onCheckedChange = null
        )
    }
}