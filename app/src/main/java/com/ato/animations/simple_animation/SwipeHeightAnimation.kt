package com.ato.animations.simple_animation

import android.content.res.Configuration
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
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
fun DisplaySwipeHeightSample(
    minHeight: Dp = 48.dp,
    mediumHeight: Dp = 100.dp,
    maxHeight: Dp = 200.dp,
    animationSpec: AnimationSpec<Dp> = spring(
        dampingRatio = 0.5f,
        stiffness = 500f
    )
) {
    var targetHeight by remember { mutableStateOf(mediumHeight) }
    val listState = rememberLazyListState()

    val nestedScrollConnection = remember {
        NestedScrollAnimation(
            listState = listState,
            minHeight = minHeight,
            mediumHeight = mediumHeight,
            maxHeight = maxHeight,
            onTargetHeightChanged = { targetHeight = it }
        )
    }

    val animatedHeight: Dp by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = animationSpec,
        label = "height animation",
    )

    nestedScrollConnection.animatedHeight = animatedHeight

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


class NestedScrollAnimation(
    private val listState: LazyListState,
    private val onTargetHeightChanged: (Dp) -> Unit,
    private val minHeight: Dp = 48.dp,
    private val mediumHeight: Dp = 100.dp,
    private val maxHeight: Dp = 200.dp,
) : NestedScrollConnection {

    var animatedHeight: Dp = mediumHeight

    private var targetHeight = mediumHeight
        set(value) {
            onTargetHeightChanged(value)
            field = value
        }
    private var currentStep = CurrentStep.MEDIUM
    private var goingToMin = false


    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val dragAmount = available.y

        if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) {
            val nextTarget = targetHeight + dragAmount.dp

            when {
                // Если куда мы направляемся больше медиума но мы сейчас движемся из позиции Min
                nextTarget > mediumHeight && currentStep == CurrentStep.MIN -> {
                    targetHeight = mediumHeight
                    goingToMin = false
                    return available.copy(y = dragAmount)
                }

                currentStep == CurrentStep.MAX -> {
                    targetHeight = minHeight
                    goingToMin = false
                    return available.copy(y = dragAmount)
                }

                nextTarget < minHeight && animatedHeight != targetHeight -> {
                    targetHeight = minHeight
                    goingToMin = true
                    return available.copy(y = 0f)
                }

                nextTarget < minHeight && animatedHeight == targetHeight -> {
                    targetHeight = minHeight
                    goingToMin = true
                    return available.copy(y = 0f)
                }

                nextTarget > maxHeight && animatedHeight != targetHeight -> {
                    targetHeight = maxHeight
                    goingToMin = false
                    return available.copy(y = dragAmount)
                }

                nextTarget > maxHeight && animatedHeight == targetHeight -> {
                    targetHeight = maxHeight
                    goingToMin = false
                    return available.copy(y = 0f)
                }

                else -> {
                    targetHeight = nextTarget
                    goingToMin = false
                    return available.copy(y = dragAmount)
                }
            }
        } else {
            return Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        if (animatedHeight != targetHeight && !goingToMin) {
            // останавливаем Fling если человек бесконечно скроллит
            return available
        }
        return super.onPreFling(available)
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        val distanceToMax = abs(targetHeight.value - maxHeight.value)
        val distanceToMedium = abs(targetHeight.value - mediumHeight.value)
        val distanceToMin = abs(targetHeight.value - minHeight.value)

        val epsilon = 1e-6f
        when {
            abs(
                distanceToMin - minOf(distanceToMin, distanceToMedium, distanceToMax)
            ) < epsilon -> {
                targetHeight = minHeight
                currentStep = CurrentStep.MIN
            }

            abs(
                distanceToMedium - minOf(distanceToMin, distanceToMedium, distanceToMax)
            ) < epsilon -> {
                targetHeight = mediumHeight
                currentStep = CurrentStep.MEDIUM
            }

            else -> {
                targetHeight = maxHeight
                currentStep = CurrentStep.MAX
            }
        }

        return super.onPostFling(consumed, available)
    }
}


enum class CurrentStep {
    MIN, MEDIUM, MAX
}
