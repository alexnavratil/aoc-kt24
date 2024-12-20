import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

typealias MemoryBoard = List<MutableList<MemoryTile?>>

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day18/input.txt").toURI()

    var fallenBytesNum = 1024
    val fallingBytes = Files.readAllLines(Paths.get(inputPath))
        .take(fallenBytesNum)
        .map { line -> line.split(",")}
        .map { Pair(it.first().toInt(), it.last().toInt()) }

    val height = 71
    val width = 71

    val board = List(height) { y -> MutableList(width) { x -> if (fallingBytes.any { it.first == x && it.second == y }) null else MemoryTile(TileType.Free, x, y) } }

    board.flatten().forEach { it?.findNeighbors(board) }

    val start = board[0][0]!!
    val end = board.last().last()!!

    val shortestPath = dijkstraDay18(start, end)

    println(shortestPath.size - 1)
}

fun dijkstraDay18(start: MemoryTile, goal: MemoryTile): List<MemoryTile> {
    val distances = mutableMapOf<MemoryTile, Int>().withDefault { Int.MAX_VALUE }
    val previousTiles = mutableMapOf<MemoryTile, MemoryTile?>()
    val priorityQueue = PriorityQueue<Pair<MemoryTile, Int>>(compareBy { it.second })

    distances[start] = 0
    priorityQueue.add(Pair(start, 0))

    while (priorityQueue.isNotEmpty()) {
        val (currentTile, _) = priorityQueue.poll()

        if (currentTile == goal) break

        for (neighbor in currentTile.neighbors) {
            val newDist = distances.getValue(currentTile) + 1

            if (newDist < distances.getValue(neighbor)) {
                distances[neighbor] = newDist
                priorityQueue.add(Pair(neighbor, newDist))
                previousTiles[neighbor] = currentTile
            }
        }
    }

    // Reconstruct shortest path
    return buildPathDay18(previousTiles, goal)
}

fun buildPathDay18(previousTiles: Map<MemoryTile, MemoryTile?>, goal: MemoryTile): List<MemoryTile> {
    val path = mutableListOf<MemoryTile>()
    var currentTile: MemoryTile? = goal

    while (currentTile != null) {
        path.add(currentTile)
        currentTile = previousTiles[currentTile]
    }

    return path.reversed()
}

data class MemoryTile(val type: TileType, var x: Int, var y: Int) {
    var topTile: MemoryTile? = null
    var leftTile: MemoryTile? = null
    var bottomTile: MemoryTile? = null
    var rightTile: MemoryTile? = null

    val neighbors
        get() = listOfNotNull(topTile, leftTile, bottomTile, rightTile)

    fun findNeighbors(board: MemoryBoard) {
        topTile = board.getOrNull(y - 1)?.getOrNull(x)
        leftTile = board.getOrNull(y)?.getOrNull(x - 1)
        bottomTile = board.getOrNull(y + 1)?.getOrNull(x)
        rightTile = board.getOrNull(y)?.getOrNull(x + 1)
    }
}