package sample.tree

import reactor.core.publisher.Mono

class NodeService {
    private val map: Map<String, Node> = mapOf(
        "1" to Node("1", listOf("1-1", "1-2")),
        "1-1" to Node("1-1", listOf("1-1-1", "1-1-2")),
        "1-2" to Node("1-2", listOf("1-2-1", "1-2-2")),
        "1-1-1" to Node("1-1-1", emptyList()),
        "1-1-2" to Node("1-1-2", emptyList()),
        "1-2-1" to Node("1-2-1", emptyList()),
        "1-2-2" to Node("1-2-2", emptyList())
    )

    fun getNode(key: String): Mono<Node> {
        return Mono.justOrEmpty(map[key])
    }
}