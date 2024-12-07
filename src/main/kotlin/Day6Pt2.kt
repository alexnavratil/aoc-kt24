import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.floor

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day6/input.txt").toURI()

    val board = Files.readAllLines(Paths.get(inputPath))
        .map { line -> line.toCharArray().toList() }

    val markerExpr = Regex("""[\^v><]""")
    val startRowIdx = board.indexOfFirst { line -> markerExpr.containsMatchIn(line.toString()) }
    val startColIdx = board[startRowIdx].indexOfFirst { ch -> markerExpr.containsMatchIn(ch.toString()) }
    val startDirection = board[startRowIdx][startColIdx].getDirection()

    val visitedPositions = board.simulateBoard(startRowIdx, startColIdx, startDirection)

    val loopingPositions = visitedPositions
        .map { triple -> Pair(triple.first, triple.second) }
        .filterIndexed { index, obstaclePos ->
            val potentiallyLoopingBoard = board.placeObstacle(obstaclePos.first, obstaclePos.second)
            val resultingPath = potentiallyLoopingBoard.simulateBoard(startRowIdx, startColIdx, startDirection)
            resultingPath.hasDuplicatePosition()
        }.toSet()

    println(loopingPositions.size)
}

fun List<List<Char>>.simulateBoard(
    startRowIdx: Int,
    startColIdx: Int,
    startDirection: Direction
): List<Triple<Int, Int, Direction>> {
    var rowIdx = startRowIdx
    var colIdx = startColIdx

    var currentDirection = startDirection

    var visitedPositions =
        mutableSetOf<Triple<Int, Int, Direction>>(Triple(rowIdx, colIdx, currentDirection)).toHashSet()

    while (isInsideBoard(rowIdx, colIdx)) {
        val nextRowIdx = currentDirection.getRowModifier() + rowIdx
        val nextColIdx = currentDirection.getColModifier() + colIdx

        if (!isInsideBoard(nextRowIdx, nextColIdx)) {
            return visitedPositions.toList()
        }

        if (visitedPositions.contains(Triple(nextRowIdx, nextColIdx, currentDirection))) {
            return visitedPositions.toList() + Triple(nextRowIdx, nextColIdx, currentDirection)
        }

        val nextPositionChar = this[nextRowIdx][nextColIdx]

        if (nextPositionChar == '#') {
            currentDirection = currentDirection.moveRight()
        } else {
            rowIdx = nextRowIdx
            colIdx = nextColIdx
            visitedPositions += Triple(rowIdx, colIdx, currentDirection)
        }
    }

    return visitedPositions.toList()
}

fun List<Triple<Int, Int, Direction>>.hasDuplicatePosition(): Boolean =
    this.groupingBy { it }.eachCount().any { it.value > 1 }

fun List<List<Char>>.placeObstacle(rowIdx: Int, colIdx: Int): List<List<Char>> =
    this.toList().map { chars -> chars.toMutableList() }.also { board -> board[rowIdx][colIdx] = '#' }

fun List<List<Char>>.getLineOrCol(rowIdx: Int, colIdx: Int, direction: Direction): List<Char> {
    return when (direction) {
        Direction.Left -> this[rowIdx].filterIndexed { index, ch -> index < colIdx }
        Direction.Right -> this[rowIdx].filterIndexed { index, ch -> index > colIdx }
        Direction.Up -> this.filterIndexed { index, chars -> index < rowIdx }.map { line -> line[colIdx] }
        Direction.Down -> this.filterIndexed { index, chars -> index > rowIdx }.map { line -> line[colIdx] }
    }
}