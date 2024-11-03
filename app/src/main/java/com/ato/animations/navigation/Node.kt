package com.ato.animations.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

// Abstract Node class
abstract class Node {
    // List of child nodes
    val childNodes = mutableListOf<Node>()

    // Active child node
    var activeNode by mutableStateOf<Node?>(null)

    // Abstract composable function to draw the node
    @Composable
    abstract fun Display(modifier: Modifier)

    // Add a child node
    fun addChild(node: Node) {
        childNodes.add(node)
    }

    // Remove a child node
    fun removeChild(node: Node) {
        childNodes.remove(node)
        if (activeNode == node) {
            activeNode = null
        }
    }

    // Mark a child node as active
    fun markNodeAsActive(node: Node) {
        if (childNodes.contains(node)) {
            activeNode = node
        }
    }

    // Display all active nodes recursively
    @Composable
    fun DisplayActiveNodes(modifier: Modifier = Modifier) {
        activeNode?.let {
            it.Display(modifier)
            it.DisplayActiveNodes()
        }
    }
}

