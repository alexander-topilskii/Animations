package com.ato.animations.simple_animation

import android.content.res.Configuration
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
import androidx.compose.ui.unit.dp

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
    var boxHeight by remember { mutableStateOf(100.dp) }
    var stiffValue by remember { mutableStateOf("50") }
    var dumpValue by remember { mutableStateOf("0.5") }

    val listState = rememberLazyListState()

    val animationSpec = spring<Dp>(
        dampingRatio = dumpValue.toFloatOrNull() ?: 0.5f,
        stiffness = stiffValue.toFloatOrNull() ?: 50f
    )
    val animatedHeight: Dp by animateDpAsState(
        targetValue = boxHeight,
        animationSpec = animationSpec,
        label = "height animation"
    )

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val dragAmount = available.y

                if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) {

                    boxHeight += if (dragAmount < 0) -10.dp else 10.dp
                    if (boxHeight < 50.dp) {
                        boxHeight = 50.dp

                        return available.copy(y = 0f)
                    }
                    if (boxHeight > 500.dp) {
                        boxHeight = 500.dp

                        return available.copy(y = 0f)
                    }
                } else {
                    return Offset.Zero
                }



                return available
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
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                OutlinedTextField(
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
    }
}
