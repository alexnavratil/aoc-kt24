import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.math.abs

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
        val nextNeighbors = path.filter { it.distanceToGoal!! < tile.distanceToGoal!! && tile.manhattanDistanceTo(it) <= 20 && tile.manhattanDistanceTo(it) < (tile.distanceToGoal!! - it.distanceToGoal!!) }.filter { !tile.neighbors.contains(it) }

        nextNeighbors.forEach { neighbor ->
            val savings = tile.distanceToGoal!! - neighbor.distanceToGoal!! - tile.manhattanDistanceTo(neighbor)

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

fun RaceTile.manhattanDistanceTo(other: RaceTile): Int {
    return abs(this.x - other.x) + abs(this.y - other.y)
}
