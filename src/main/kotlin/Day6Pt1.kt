import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day6/input.txt").toURI()

    val board = Files.readAllLines(Paths.get(inputPath))
        .map { line -> line.toCharArray().toList() }

    val markerExpr = Regex("""[\^v><]""")

    var rowIdx = board.indexOfFirst { line -> markerExpr.containsMatchIn(line.toString()) }
    var colIdx = board[rowIdx].indexOfFirst { ch -> markerExpr.containsMatchIn(ch.toString()) }

    var currentDirection = board[rowIdx][colIdx].getDirection()

    var visitedPositions = mutableSetOf<Pair<Int, Int>>(Pair(rowIdx, colIdx))

    while(board.isInsideBoard(rowIdx, colIdx)) {
        val nextRowIdx = currentDirection.getRowModifier() + rowIdx
        val nextColIdx = currentDirection.getColModifier() + colIdx

        if (!board.isInsideBoard(nextRowIdx, nextColIdx)) {
            break
        }

        val nextPositionChar = board[nextRowIdx][nextColIdx]

        if (nextPositionChar == '#') {
            currentDirection = currentDirection.moveRight()
        } else {
            rowIdx = nextRowIdx
            colIdx = nextColIdx
            visitedPositions += Pair(rowIdx, colIdx)
        }
    }

    println(visitedPositions.size)
}

fun Char.getDirection() = when(this) {
    '^' -> Direction.Up
    'v' -> Direction.Down
    '<' -> Direction.Left
    '>' -> Direction.Right
    else -> throw Exception("Invalid direction")
}

fun Direction.getRowModifier() = when(this) {
    Direction.Up -> -1
    Direction.Down -> 1
    else -> 0
}

fun Direction.getColModifier() = when(this) {
    Direction.Left -> -1
    Direction.Right -> 1
    else -> 0
}

fun Direction.moveRight(): Direction = when(this) {
    Direction.Up -> Direction.Right
    Direction.Right -> Direction.Down
    Direction.Down -> Direction.Left
    Direction.Left -> Direction.Up
}

fun List<List<Char>>.isInsideBoard(rowIdx: Int, colIdx: Int) = rowIdx in 0 until size && colIdx in 0 until first().size

enum class Direction {
    Up,
    Down,
    Left,
    Right
}

