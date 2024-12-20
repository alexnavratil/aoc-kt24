import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day18/input.txt").toURI()

    var fallenBytesNum = 1025
    val fallingBytes = Files.readAllLines(Paths.get(inputPath))
        .map { line -> line.split(",")}
        .map { Pair(it.first().toInt(), it.last().toInt()) }

    val height = 71
    val width = 71

    var shortestPath = listOf<MemoryTile>()
    do {
        val fallenBytes = fallingBytes.take(fallenBytesNum)
        val board = List(height) { y -> MutableList(width) { x -> if (fallenBytes.any { it.first == x && it.second == y }) null else MemoryTile(TileType.Free, x, y) } }

        board.flatten().forEach { it?.findNeighbors(board) }

        val start = board[0][0]!!
        val end = board.last().last()!!

        shortestPath = dijkstraDay18(start, end)

        fallenBytesNum++
    } while (shortestPath.size - 1 > 0)

    println(fallingBytes[fallenBytesNum - 2].let { "${it.first},${it.second}" })
}