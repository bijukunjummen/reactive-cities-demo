package sample.tree

data class Node(
    val id: String,
    val childRefs: List<String>,
    var nodes: List<Node> = emptyList()
) {
    override fun toString(): String {
        return "Node-$id"
    }
}