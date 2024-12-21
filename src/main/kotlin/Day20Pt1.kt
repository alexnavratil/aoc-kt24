import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

typealias RaceMap = List<MutableList<RaceTile?>>

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day20/input.txt").toURI()

    val inputLines = Files.readAllLines(Paths.get(inputPath))

    val board = inputLines.mapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, char -> char.getRaceTile(colIndex, rowIndex) }.toMutableList()
    }

    board.flatten().forEach { it?.findNeighbors(board) }

    val start = board.flatten().single { it?.type == RaceTileType.Start }!!
    val end = board.flatten().single { it?.type == RaceTileType.End }!!

    val path = dijkstraDay20(start, end)

    val cheatingPaths = mutableMapOf<Int, MutableList<List<RaceTile>>>()

    path.forEachIndexed { tileIndex, tile ->
        val nextNeighbors = tile.wallNeighbors.flatMap { it.neighbors }.filter { it.distanceToGoal != null && it.distanceToGoal!! < tile.distanceToGoal!! && path.contains(it) }

        nextNeighbors.forEach { neighbor ->
            val savings = tile.distanceToGoal!! - neighbor.distanceToGoal!! - 2

            val cheatingPath = path.filter { it.distanceToGoal!! < tile.distanceToGoal!! && it.distanceToGoal!! > neighbor.distanceToGoal!! }

            if (cheatingPaths[savings] == null) {
                cheatingPaths[savings] = mutableListOf(cheatingPath)
            } else {
                cheatingPaths[savings]!!.add(cheatingPath)
            }
        }
    }

    val result = cheatingPaths.filter { it.key >= 100 }.values.sumOf { it.size }
    println(result)
}

fun dijkstraDay20(start: RaceTile, goal: RaceTile): List<RaceTile> {
    val distances = mutableMapOf<RaceTile, Int>().withDefault { Int.MAX_VALUE }
    val previousTiles = mutableMapOf<RaceTile, RaceTile?>()
    val priorityQueue = PriorityQueue<Pair<RaceTile, Int>>(compareBy { it.second })

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
    return buildPathDay20(previousTiles, goal)
}

fun buildPathDay20(previousTiles: Map<RaceTile, RaceTile?>, goal: RaceTile): List<RaceTile> {
    val path = mutableListOf<RaceTile>()
    var currentTile: RaceTile? = goal

    var distanceToGoal = 0

    while (currentTile != null) {
        currentTile.distanceToGoal = distanceToGoal
        path.add(currentTile)
        currentTile = previousTiles[currentTile]
        distanceToGoal++
    }

    return path.reversed()
}

fun Char.getRaceTile(x: Int, y: Int): RaceTile? = when (this) {
    '#' -> RaceTile(RaceTileType.Wall, x, y)
    '.' -> RaceTile(RaceTileType.Free, x, y)
    'S' -> RaceTile(RaceTileType.Start, x, y)
    'E' -> RaceTile(RaceTileType.End, x, y)
    else -> null
}

data class RaceTile(val type: RaceTileType, var x: Int, var y: Int) {
    var topTile: RaceTile? = null
    var leftTile: RaceTile? = null
    var bottomTile: RaceTile? = null
    var rightTile: RaceTile? = null

    var distanceToGoal: Int? = null

    val neighbors
        get() = listOfNotNull(topTile, leftTile, bottomTile, rightTile).filter { it.type != RaceTileType.Wall }

    val wallNeighbors
        get() = listOfNotNull(topTile, leftTile, bottomTile, rightTile).filter { it.type == RaceTileType.Wall }

    fun findNeighbors(board: RaceMap) {
        topTile = board.getOrNull(y - 1)?.getOrNull(x)
        leftTile = board.getOrNull(y)?.getOrNull(x - 1)
        bottomTile = board.getOrNull(y + 1)?.getOrNull(x)
        rightTile = board.getOrNull(y)?.getOrNull(x + 1)
    }
}

enum class RaceTileType {
    Start,
    End,
    Free,
    Wall
}