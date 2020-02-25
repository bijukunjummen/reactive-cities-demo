package sample.tree

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class NodeExpandTest {

    val nodeService = NodeService()

    @Test
    fun expandATree() {
        val rootMono: Mono<Node> = nodeService.getNode("1")
        val expanded: Flux<Node> = rootMono.expand { node ->
            Flux.fromIterable(node.childRefs)
                .flatMap { nodeRef -> nodeService.getNode(nodeRef) }
        }
        expanded.subscribe { node -> println(node) }
    }
}