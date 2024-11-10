package com.ato.animations.simple_animation

import android.content.res.Configuration
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Full Preview", showSystemUi = true)
fun PreviewSwipeHeightSample() {
    DisplaySwipeHeightSample()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplaySwipeHeightSample() {
    val minHeight = 48.dp
    val mediumHeight = 100.dp
    val maxHeight = 200.dp

    var boxHeight by remember { mutableStateOf(mediumHeight) }
    var stiffValue by remember { mutableStateOf("50") }
    var dumpValue by remember { mutableStateOf("0.9") }
    var currentStep by remember { mutableStateOf(CurrentStep.MEDIUM) }

    val listState = rememberLazyListState()

    val animationSpec = spring<Dp>(
        dampingRatio = dumpValue.toFloatOrNull() ?: 0.9f,
        stiffness = stiffValue.toFloatOrNull() ?: 500f
    )
    val tween = tween<Dp>(
        durationMillis = 300,
        easing = FastOutLinearInEasing
    )

    val animatedHeight: Dp by animateDpAsState(
        targetValue = boxHeight,
        animationSpec = tween,
        label = "height animation",
    )

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val dragAmount = available.y

                if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) {
                    val nextTarget = boxHeight + dragAmount.dp

                    when {
                        // Если куда мы направляемся больше медиума но мы сейчас движемся из позиции Min
                        nextTarget > mediumHeight && currentStep == CurrentStep.MIN -> {
                            boxHeight = mediumHeight

                            return available.copy(y = dragAmount)
                        }

                        currentStep == CurrentStep.MAX -> {
                            boxHeight = minHeight
                            return available.copy(y = dragAmount)
                        }

                        nextTarget < minHeight -> {
                            boxHeight = minHeight
                            return available.copy(y = 0f)
                        }

                        nextTarget > maxHeight -> {
                            boxHeight = maxHeight
                            return available.copy(y = 0f)
                        }


                        else -> {
                            boxHeight = nextTarget
                            return available.copy(y = dragAmount)
                        }
                    }
                } else {
                    return Offset.Zero
                }
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (animatedHeight != boxHeight) {
                    // останавливаем Fling если человек бесконечно скроллит
                    return available
                }
                return super.onPreFling(available)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val distanceToMax = abs(boxHeight.value - maxHeight.value)
                val distanceToMedium = abs(boxHeight.value - mediumHeight.value)
                val distanceToMin = abs(boxHeight.value - minHeight.value)

                val epsilon = 1e-6f
                when {
                    abs(
                        distanceToMin - minOf(distanceToMin, distanceToMedium, distanceToMax)
                    ) < epsilon -> {
                        boxHeight = minHeight
                        currentStep = CurrentStep.MIN
                    }

                    abs(
                        distanceToMedium - minOf(distanceToMin, distanceToMedium, distanceToMax)
                    ) < epsilon -> {
                        boxHeight = mediumHeight
                        currentStep = CurrentStep.MEDIUM
                    }

                    else -> {
                        boxHeight = maxHeight
                        currentStep = CurrentStep.MAX
                    }
                }

                return super.onPostFling(consumed, available)
            }
        }
    }

    Box(
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
        ) {
            stickyHeader {
                MainBox(animatedHeight)
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                OutlinedTextField(
                    modifier = Modifier.padding(16.dp),
                    value = stiffValue,
                    onValueChange = { stiffValue = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    label = { Text("Stiff (0-10_000): $stiffValue") }
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                OutlinedTextField(
                    modifier = Modifier.padding(16.dp),
                    value = dumpValue,
                    onValueChange = { dumpValue = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    label = { Text("Dump (0-1f): $dumpValue") }
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            items((0..100).map { "$it" }) { item ->
                Text(
                    text = item,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

enum class CurrentStep {
    MIN, MEDIUM, MAX
}

@Composable
private fun MainBox(
    animatedHeight: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .height(animatedHeight)
                .fillMaxWidth()
                .background(Color.Blue)
        )
        Text("$animatedHeight")
    }
}
