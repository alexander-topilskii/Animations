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
    if (depth > maxDepth) return // Предотвращение слишком глубокой рекурсии

    val x = size.width / 2 + offset.x + (depth * 100f)
    val y = size.height / 2 + offset.y

    val currentPosition = Offset(x, y)
    nodePositions[node] = currentPosition

    // Рисуем узел в виде кружка
    drawCircle(
        color = if (node.isActive) Color.Red else Color.Blue,
        radius = 20f,
        center = currentPosition
    )

    // Рисуем линию к родительскому узлу
    parentPosition?.let {
        drawLine(
            color = Color.Gray,
            start = it,
            end = currentPosition,
            strokeWidth = 2f
        )
    }


    // Рекурсивно отрисовываем дочерние узлы
    node.childNodes.forEachIndexed { index, childNode ->
        // Смещение дочерних узлов по оси Y
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
