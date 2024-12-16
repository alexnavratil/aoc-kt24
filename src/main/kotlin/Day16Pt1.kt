import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

typealias ReindeerMap = List<MutableList<Tile?>>

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day16/input.txt").toURI()

    val inputLines = Files.readAllLines(Paths.get(inputPath))

    val board = inputLines.mapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, char -> char.getTile(colIndex, rowIndex) }.toMutableList()
    }

    board.flatten().forEach { it?.findNeighbors(board) }

    val start = board.flatten().single { it?.type == TileType.Start }!!
    val end = board.flatten().single { it?.type == TileType.End }!!

    val shortestPath = dijkstra(start, end)
    val score = shortestPath.getScore()

    println(score)
}

fun List<Tile>.getScore(): Int {
    val steps = this.zipWithNext()

    var currentDirection = Direction.Right
    var score = 0

    steps.forEach {
        score += it.first.distanceToNeighbor(currentDirection, it.second)
        currentDirection = it.first.directionOf(it.second)
    }

    return score
}

fun dijkstra(start: Tile, goal: Tile): List<Tile> {
    val distances = mutableMapOf<Pair<Tile, Direction>, Int>().withDefault { Int.MAX_VALUE }
    val previousTiles = mutableMapOf<Pair<Tile, Direction>, Pair<Tile, Direction>?>()
    val priorityQueue = PriorityQueue<Pair<Pair<Tile, Direction>, Int>>(compareBy { it.second })

    // Assuming the starting direction is necessary. Choose one based on implementation context.
    val initialDirection = Direction.Right
    distances[Pair(start, initialDirection)] = 0
    priorityQueue.add(Pair(Pair(start, initialDirection), 0))

    while (priorityQueue.isNotEmpty()) {
        val (currentPair, _) = priorityQueue.poll()
        val (currentTile, currentDirection) = currentPair

        if (currentTile == goal) break

        for (neighbor in currentTile.neighbors) {
            val rotationCost = currentTile.distanceToNeighbor(currentDirection, neighbor)
            val newDist = distances.getValue(currentPair) + rotationCost

            val neighborDirection = currentTile.directionOf(neighbor)

            val neighborPair = Pair(neighbor, neighborDirection)
            if (newDist < distances.getValue(neighborPair)) {
                distances[neighborPair] = newDist
                priorityQueue.add(Pair(neighborPair, newDist))
                previousTiles[neighborPair] = currentPair
            }
        }
    }

    // Reconstruct shortest path
    return buildPath(previousTiles, goal)
}

fun buildPath(previousTiles: Map<Pair<Tile, Direction>, Pair<Tile, Direction>?>, goal: Tile): List<Tile> {
    val endDirection = previousTiles.keys.find { it.first == goal }?.second ?: return emptyList()
    val path = mutableListOf<Tile>()
    var currentPair: Pair<Tile, Direction>? = Pair(goal, endDirection)

    while (currentPair != null) {
        path.add(currentPair.first)
        currentPair = previousTiles[currentPair]
    }

    return path.reversed()
}

fun Char.getTile(x: Int, y: Int): Tile? = when (this) {
    '#' -> null
    '.' -> Tile(TileType.Free, x, y)
    'S' -> Tile(TileType.Start, x, y)
    'E' -> Tile(TileType.End, x, y)
    else -> null
}

data class Tile(val type: TileType, var x: Int, var y: Int) {
    var topTile: Tile? = null
    var leftTile: Tile? = null
    var bottomTile: Tile? = null
    var rightTile: Tile? = null

    val neighbors
        get() = listOfNotNull(topTile, leftTile, bottomTile, rightTile)

    fun findNeighbors(board: ReindeerMap) {
        topTile = board[y - 1][x]
        leftTile = board[y][x - 1]
        bottomTile = board[y + 1][x]
        rightTile = board[y][x + 1]
    }

    fun distanceToNeighbor(currentDirection: Direction, tile: Tile): Int {
        if (!neighbors.contains(tile)) {
            throw IllegalStateException("not a neighbor")
        }

        val directionToNeighbor = directionOf(tile)

        val rotationCount = when(currentDirection) {
            Direction.Up -> when(directionToNeighbor) {
                Direction.Up -> 0
                Direction.Down -> 2
                Direction.Left -> 1
                Direction.Right -> 1
            }
            Direction.Down -> when(directionToNeighbor) {
                Direction.Up -> 2
                Direction.Down -> 0
                Direction.Left -> 1
                Direction.Right -> 1
            }
            Direction.Left -> when(directionToNeighbor) {
                Direction.Up -> 1
                Direction.Down -> 1
                Direction.Left -> 0
                Direction.Right -> 2
            }
            Direction.Right -> when(directionToNeighbor) {
                Direction.Up -> 1
                Direction.Down -> 1
                Direction.Left -> 2
                Direction.Right -> 0
            }
        }

        if (rotationCount == 0) {
            return 1
        }

        return 1000 * rotationCount + 1
    }

    fun directionOf(tile: Tile): Direction {
        return when (tile) {
            topTile -> Direction.Up
            rightTile -> Direction.Right
            bottomTile -> Direction.Down
            leftTile -> Direction.Left
            else -> throw IllegalStateException("not a neighbor")
        }
    }
}

enum class TileType {
    Start,
    End,
    Free
}