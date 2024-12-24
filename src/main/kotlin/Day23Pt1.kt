import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.floor
import kotlin.mod

const val ChiefHistorianNamePrefix = "t"

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

    val triangles: MutableSet<HashSet<String>> = mutableSetOf()

    graph
        .forEach { (firstNode, firstNodes) ->
            firstNodes.forEach { secondNode ->
                val secondNodes = graph[secondNode]!!
                secondNodes
                    .filter { it != firstNode }
                    .forEach { thirdNode ->
                        val thirdNodes = graph[thirdNode]!!

                        if (thirdNodes.contains(firstNode)) {
                            triangles += HashSet<String>(setOf(firstNode, secondNode, thirdNode))
                        }
                    }
            }
        }

    val intersectionsWithPotentialChiefHistorian =
        triangles.filter { it.any { node -> node.startsWith(ChiefHistorianNamePrefix) } }

    println(intersectionsWithPotentialChiefHistorian.size)
}
