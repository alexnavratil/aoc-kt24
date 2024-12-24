import java.nio.file.Files
import java.nio.file.Paths

const val MinChunkSize = 3

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day23/input.txt").toURI()

    val inputLines = Files.readAllLines(Paths.get(inputPath)).map { it.split("-").let { Pair(it[0], it[1]) } }
    val graph = mutableMapOf<String, MutableList<String>>()

    inputLines.forEach {
        graph[it.first] =
            (graph.getOrDefault(it.first, mutableListOf()) + mutableListOf<String>(it.second)).toMutableList()
        graph[it.second] =
            (graph.getOrDefault(it.second, mutableListOf()) + mutableListOf<String>(it.first)).toMutableList()
    }

    val maxChunkSize = graph.maxBy { it.value.size }.value.size + 1
    (MinChunkSize..maxChunkSize).reversed().forEach { chunkSize ->
        graph
            .forEach { (firstNode, firstNodes) ->
                val baseNodes = firstNodes + listOf(firstNode)

                if (baseNodes.size >= chunkSize) {
                    val chunkedBaseNodes = baseNodes.combinations(chunkSize)

                    chunkedBaseNodes.forEach { chunk ->
                        val intersectedNodes = chunk.map { node -> (graph[node]!! + setOf(node)).toSet() }
                        var totalIntersection = intersectedNodes.first()

                        val success = intersectedNodes.drop(1).all { nodes ->
                            totalIntersection = totalIntersection.intersect(nodes)

                            return@all totalIntersection == chunk
                        }

                        if (success) {
                            println(chunk.sorted().joinToString(","))
                            return
                        }
                    }
                }

            }
    }
}

fun <T> List<T>.combinations(combinationSize: Int): Set<Set<T>> {
    fun combinationsInternal(list: List<T>, combinationSize: Int, startIndex: Int, currentCombination: Set<T>, result: MutableSet<Set<T>>) {
        if (currentCombination.size == combinationSize) {
            result.add(currentCombination)
            return
        }
        for (i in startIndex until list.size) {
            combinationsInternal(list, combinationSize, i + 1, currentCombination + list[i], result)
        }
    }

    val result = mutableSetOf<Set<T>>()
    combinationsInternal(this, combinationSize, 0, emptySet(), result)
    return result
}
