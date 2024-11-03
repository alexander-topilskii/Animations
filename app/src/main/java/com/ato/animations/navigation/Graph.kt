package com.ato.animations.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


// Graph object to manage nodes
object Graph {
    // List of root nodes
    private val nodes = mutableListOf<Node>()

    // Active root node
    var activeNode by mutableStateOf<Node?>(null)

    // Add a root node
    fun addNode(node: Node, isActive: Boolean = true) {
        nodes.add(node)
        if (isActive) {
            markNodeAsActive(node)
        }
    }

    // Remove a root node
    fun removeNode(node: Node) {
        nodes.remove(node)
        if (activeNode == node) {
            activeNode = null
        }
    }

    // Mark a root node as active
    fun markNodeAsActive(node: Node) {
        if (nodes.contains(node)) {
            activeNode = node
        }
    }

    // Display all active nodes starting from the active root node
    @Composable
    fun Display(modifier: Modifier = Modifier) {
        activeNode?.let {
            it.Display(modifier)
        }
    }
}