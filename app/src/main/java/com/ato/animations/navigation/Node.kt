package com.ato.animations.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.util.UUID

// Abstract Node class
abstract class Node {
    private val id: String = UUID.randomUUID().toString()
    var label: String = id
    var isActive: Boolean = true

    // List of child nodes
    val childNodes = mutableListOf<Node>()

    // Reference to the parent node
    var parentNode: Node? = null

    // Abstract composable function to display the node with a modifier
    @Composable
    abstract fun Display(modifier: Modifier)

    // Add a child node
    fun addChild(node: Node) {
        this.isActive = false
        this.parentNode?.isActive = false
        node.isActive = true
        node.parentNode = this  // Set the parent node
        childNodes.add(node)
    }

    // Method to recursively print the nodes
    fun printNodes(indent: String = "-") {
        // Print the current node's information
        println("INTAG: ${indent}Node: $this, isActive: $isActive")

        // Recursively print each child node with increased indentation
        for (child in childNodes) {
            child.printNodes("$indent")
        }
    }

    // Remove a child node
    fun removeChild(node: Node) {
        childNodes.remove(node)
    }

    // Iterative method to find the last active node
    fun findLastActiveNode(): Node? {
        var lastActiveNode: Node? = null
        val stack = mutableListOf<Node>()
        val visited = mutableSetOf<Node>()

        stack.add(this)

        while (stack.isNotEmpty()) {
            val currentNode = stack.removeAt(stack.size - 1)

            // Skip if already visited to prevent infinite loops
            if (currentNode in visited) continue
            visited.add(currentNode)

            // Update the last active node
            if (currentNode.isActive) {
                lastActiveNode = currentNode
            }

            // Add child nodes to the stack
            stack.addAll(currentNode.childNodes)
        }

        return lastActiveNode
    }
}

// Graph object to manage nodes
object Graph {
    private var root: Node? = null
    private var activeNode by mutableStateOf<Node?>(null)

    // Add a node to the latest active node
    fun addNode(node: Node) {
        if (root == null) {
            node.isActive = true
            root = node
        } else {
            root?.findLastActiveNode()?.addChild(node)
        }

        root?.printNodes() ?: println("root is null")
    }

    fun getRootNode(): Node? {
        return root
    }

    // Remove a node
    fun removeNode(node: Node) {

    }

    // Navigate back to the parent node
    fun navigateBack() {

    }

    // Display all active nodes starting from the active root node
    @Composable
    fun Display(modifier: Modifier = Modifier) {
        activeNode?.Display(modifier)
    }
}

// Extension function to recursively remove a node
fun Node.removeChildRecursive(node: Node) {
    if (childNodes.contains(node)) {
        removeChild(node)
    } else {
        childNodes.forEach { child ->
            child.removeChildRecursive(node)
        }
    }
}
