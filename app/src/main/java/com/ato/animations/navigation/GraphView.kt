package com.ato.animations.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun GraphView(startNode: Node) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.1f))
            .fillMaxWidth()
            .height(300.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGraph(
                node = startNode,
                offset = Offset(offsetX, offsetY),
                nodePositions = mutableMapOf(),
                depth = 0,
                maxDepth = 10,
                parentPosition = null
            )
        }
    }
}

private fun DrawScope.drawGraph(
    node: Node,
    offset: Offset,
    nodePositions: MutableMap<Node, Offset>,
    depth: Int,
    maxDepth: Int,
    parentPosition: Offset?
) {
    if (depth > maxDepth) return // Prevent too deep recursion

    // Calculate the position of the current node
    val x = size.width / 2 + offset.x + (depth * 150f)
    val y = size.height / 2 + offset.y

    val currentPosition = Offset(x, y)
    nodePositions[node] = currentPosition

    // Draw the node as a circle
    drawCircle(
        color = if (node.isActive) Color.Red else Color.Blue,
        radius = 20f,
        center = currentPosition
    )

    // Draw the label under the node
    drawContext.canvas.nativeCanvas.apply {
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 40f
            textAlign = android.graphics.Paint.Align.CENTER
        }

        // Calculate the position for the text (slightly below the node)
        val textX = currentPosition.x
        val textY = currentPosition.y + Random.nextInt(50, 150) // Adjust as needed

        drawText(node.label, textX, textY, textPaint)
    }

    // Draw a line to the parent node
    parentPosition?.let {
        drawLine(
            color = Color.Gray,
            start = it,
            end = currentPosition,
            strokeWidth = 2f
        )
    }

    // Recursively draw child nodes
    node.childNodes.forEachIndexed { index, childNode ->
        // Offset child nodes along the Y-axis
        val childOffset = offset.copy(
            x = offset.x,
            y = offset.y + (index - node.childNodes.size / 2) * 100f
        )
        drawGraph(
            node = childNode,
            offset = childOffset,
            nodePositions = nodePositions,
            depth = depth + 1,
            maxDepth = maxDepth,
            parentPosition = currentPosition
        )
    }
}


// Data class to hold size and position information
data class NodeLayoutInfo(
    val node: Node,
    var width: Float = 0f,
    var height: Float = 0f,
    var position: Offset = Offset.Zero
)
