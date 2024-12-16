import java.nio.file.Files
import java.nio.file.Paths

typealias Warehouse = List<MutableList<WarehouseObject?>>

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day15/input.txt").toURI()

    val inputLines = Files.readAllLines(Paths.get(inputPath))

    val board = inputLines.takeWhile { it.isNotBlank() }.mapIndexed { rowIdx, line ->
        line.mapIndexed { colIdx, c ->
            c.getWarehouseObject(
                colIdx,
                rowIdx
            )
        }.toMutableList()
    }

    val movements = inputLines.dropWhile { it.isNotBlank() }.drop(1).joinToString("").map { it.getDirection() }

    val robot = board.flatten().single { it?.type == WarehouseObjectType.Robot }!!

    movements.forEach { direction -> robot.move(direction, board) }

    val gpsSum = board
        .flatten()
        .filter { it?.type == WarehouseObjectType.Box }
        .filterNotNull()
        .sumOf { 100 * it.y + it.x }

    println(gpsSum)
}

fun Char.getWarehouseObject(x: Int, y: Int): WarehouseObject? = when (this) {
    '#' -> WarehouseObject(WarehouseObjectType.Wall, x, y)
    'O' -> WarehouseObject(WarehouseObjectType.Box, x, y)
    '@' -> WarehouseObject(WarehouseObjectType.Robot, x, y)
    else -> null
}

data class WarehouseObject(val type: WarehouseObjectType, var x: Int, var y: Int) {
    fun move(direction: Direction, board: Warehouse): Boolean {
        val nextRowIdx = y + direction.getRowModifier()
        val nextColIdx = x + direction.getColModifier()

        if (nextRowIdx !in 0 until board.size) return false
        if (nextColIdx !in 0 until board.first().size) return false

        var nextObj = board[nextRowIdx][nextColIdx]

        if (nextObj?.type == WarehouseObjectType.Box) {
            if (nextObj.move(direction, board)) {
                nextObj = null
            }
        }

        if (nextObj == null) {
            board[y][x] = null

            this.y = nextRowIdx
            this.x = nextColIdx

            board[nextRowIdx][nextColIdx] = this

            return true
        }

        return false
    }
}

enum class WarehouseObjectType {
    Wall,
    Robot,
    Box
}