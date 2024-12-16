import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day16/input.txt").toURI()

    val inputLines = Files.readAllLines(Paths.get(inputPath))

    val board = inputLines.mapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, char -> char.getTile(colIndex, rowIndex) }.toMutableList()
    }

    board.flatten().forEach { it?.findNeighbors(board) }

    val start = board.flatten().single { it?.type == TileType.Start }!!
    val end = board.flatten().single { it?.type == TileType.End }!!

    val bestPathsTiles = dijkstra2(start, end)

    println(bestPathsTiles.size)
}

fun dijkstra2(start: Tile, goal: Tile): Set<Tile> {
    val distances = mutableMapOf<Pair<Tile, Direction>, Int>().withDefault { Int.MAX_VALUE }
    val previousTiles = mutableMapOf<Pair<Tile, Direction>, List<Pair<Tile, Direction>>?>()
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
            if (newDist <= distances.getValue(neighborPair)) {
                distances[neighborPair] = newDist
                priorityQueue.add(Pair(neighborPair, newDist))
                previousTiles[neighborPair] = (previousTiles[neighborPair] ?: listOf())!! + listOf(currentPair)
            }
        }
    }

    // Reconstruct shortest path
    return buildPaths(previousTiles, goal)
}

fun buildPaths(previousTiles: Map<Pair<Tile, Direction>, List<Pair<Tile, Direction>>?>, goal: Tile): Set<Tile> {
    val endDirection = previousTiles.keys.find { it.first == goal }?.second ?: return emptySet()
    val path = mutableSetOf<Tile>()
    val currentPairQueue = linkedSetOf<Pair<Tile, Direction>>(Pair(goal, endDirection))

    while (currentPairQueue.isNotEmpty()) {
        val next = currentPairQueue.removeFirst()
        path.add(next.first)
        currentPairQueue.addAll(previousTiles[next] ?: listOf())
    }

    return path
}
