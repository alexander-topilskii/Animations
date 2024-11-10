package com.ato.animations.simple_animation

import android.content.res.Configuration
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Full Preview", showSystemUi = true)
fun PreviewDisplayCustomLayoutSample() {
    DisplayCustomLayoutSample()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayCustomLayoutSample(
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

    val linearAnimatedHeight: Dp by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = tween(),
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
                MainBox(
                    height = animatedHeight,
                    progress = calculateProgress(
                        currentValue = linearAnimatedHeight,
                        min = minHeight,
                        mid = mediumHeight,
                        max = maxHeight
                    )
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


@Composable
private fun MainBox(
    height: Dp,
    progress: Float = 0f
) {
    CustomLayout(
        progress = progress,
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .background(Color.Blue)
    ) {
        Text("kek1", modifier = Modifier.alpha(1f - progress))
        Text("kek2")
    }
}

@Composable
private fun CustomLayout(
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        check(measurables.size == 2)

        // Measure the child with unconstrained constraints
        val icon1 = measurables[0].measure(Constraints())
        val icon2 = measurables[1].measure(Constraints())

        // Determine the size of the layout based on the icon size
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        layout(width = layoutWidth, height = layoutHeight) {
            // Place the icon at the desired position
            icon1.placeRelative(
                x = layoutWidth / 2 - icon1.width / 2, // Padding from the left edge
                y = layoutHeight / 2 - icon1.height / 2   // Padding from the top edge
            )

            icon2.placeRelative(
                x = lerp(
                    start = 0,
                    stop = layoutWidth - icon2.width,
                    fraction = progress
                ),
                y = lerp(
                    start = 0,
                    stop = layoutHeight - icon2.height,
                    fraction = progress
                )
            )
        }
    }
}


fun calculateProgress(currentValue: Dp, min: Dp, mid: Dp, max: Dp): Float {
    return when {
        currentValue <= min -> 0f
        currentValue >= max -> 1f
        currentValue <= mid -> {
            val fraction = (currentValue.value - min.value) / (mid.value - min.value)
            fraction * 0.5f
        }

        else -> {
            val fraction = (currentValue.value - mid.value) / (max.value - mid.value)
            0.5f + fraction * 0.5f
        }
    }
}