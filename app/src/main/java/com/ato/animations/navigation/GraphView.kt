package com.ato.animations.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas

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
        val textY = currentPosition.y + 50f // Adjust as needed

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

private fun positionNodes(
    layoutInfo: NodeLayoutInfo,
    startX: Float,
    startY: Float,
    nodeLayouts: List<NodeLayoutInfo>,
    nodeRadius: Float,
    horizontalSpacing: Float,
    verticalSpacing: Float
) {
    // Position the current node at the center of its calculated height
    layoutInfo.position = Offset(startX, startY + layoutInfo.height / 2)

    // Position child nodes
    var childStartY = startY
    layoutInfo.node.childNodes.forEach { childNode ->
        val childLayout = nodeLayouts.find { it.node == childNode }
        if (childLayout != null) {
            positionNodes(
                layoutInfo = childLayout,
                startX = startX + horizontalSpacing,
                startY = childStartY,
                nodeLayouts = nodeLayouts,
                nodeRadius = nodeRadius,
                horizontalSpacing = horizontalSpacing,
                verticalSpacing = verticalSpacing
            )
            childStartY += childLayout.height + verticalSpacing
        }
    }
}

private fun calculateNodeLayout(
    node: Node,
    textPaint: android.graphics.Paint,
    nodeLayouts: MutableList<NodeLayoutInfo>,
    nodeRadius: Float,
    horizontalSpacing: Float,
    verticalSpacing: Float
): NodeLayoutInfo {
    // Measure the label size
    val labelWidth = textPaint.measureText(node.label)
    val labelHeight = textPaint.fontMetrics.run { bottom - top }

    // Create a layout info object for the node
    val layoutInfo = NodeLayoutInfo(node, labelWidth, labelHeight)

    // Add to the list of layouts
    nodeLayouts.add(layoutInfo)

    // Recursively calculate child layouts
    val childLayouts = node.childNodes.map { childNode ->
        calculateNodeLayout(
            node = childNode,
            textPaint = textPaint,
            nodeLayouts = nodeLayouts,
            nodeRadius = nodeRadius,
            horizontalSpacing = horizontalSpacing,
            verticalSpacing = verticalSpacing
        )
    }

    // Calculate total height required for the subtree
    val totalChildHeight = childLayouts.sumOf { it.height } + (childLayouts.size - 1) * verticalSpacing

    // Determine the required height for the current node
    layoutInfo.height = maxOf(
        labelHeight + nodeRadius * 2,
        if (childLayouts.isEmpty()) labelHeight + nodeRadius * 2 else totalChildHeight
    )

    return layoutInfo
}

// Data class to hold size and position information
data class NodeLayoutInfo(
    val node: Node,
    var width: Float = 0f,
    var height: Float = 0f,
    var position: Offset = Offset.Zero
)
