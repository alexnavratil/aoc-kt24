import java.nio.file.Files
import java.nio.file.Paths

typealias Warehouse2 = List<MutableList<WarehouseObject2?>>

const val enableOutput = false

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day15/input.txt").toURI()

    val inputLines = Files.readAllLines(Paths.get(inputPath))

    val board = inputLines.takeWhile { it.isNotBlank() }.mapIndexed { rowIdx, line ->
        line.flatMapIndexed { colIdx, c ->
            c.getWarehouseObject2(
                colIdx * 2,
                rowIdx
            )
        }.toMutableList()
    }

    val movements = inputLines.dropWhile { it.isNotBlank() }.drop(1).joinToString("").map { it.getDirection() }

    val robot = board.flatten().single { it?.type == WarehouseObjectType.Robot }!!

    movements.forEach { direction ->
        robot.moveNext(direction, board)

        if (enableOutput) {
            println("Direction: $direction")
            board.print()

            println()
            println()
        }
    }

    val gpsSum = board
        .flatten()
        .filter { it?.type == WarehouseObjectType.Box }
        .filterNotNull()
        .toSet()
        .sumOf { 100 * it.y + it.x }

    println(gpsSum)
}

fun Warehouse2.print() {
    this.forEachIndexed { rowIdx, line ->
        line.forEachIndexed { colIdx, obj ->
            when (obj?.type) {
                WarehouseObjectType.Wall -> print('#')
                WarehouseObjectType.Robot -> print('@')
                WarehouseObjectType.Box -> if (obj.x == colIdx && obj.y == rowIdx) print("[]")
                null -> print('.')
            }
        }

        println()
    }
}

fun Char.getWarehouseObject2(x: Int, y: Int): List<WarehouseObject2?> = when (this) {
    '#' -> WarehouseObject2(WarehouseObjectType.Wall, x, y, 2).let { listOf(it, it) }
    'O' -> WarehouseObject2(WarehouseObjectType.Box, x, y, 2).let { listOf(it, it) }
    '@' -> listOf(WarehouseObject2(WarehouseObjectType.Robot, x, y, 1), null)
    else -> listOf(null, null)
}

fun WarehouseObject2?.moveNext(direction: Direction, board: Warehouse2): Boolean {
    if (this?.type == WarehouseObjectType.Wall) {
        return false
    }

    if (this == null) {
        return true
    }

    val rowRange = 0 until board.size
    val colRange = 0 until board.first().size

    if (this.type == WarehouseObjectType.Robot) {
        val nextRowIdx = y + direction.getRowModifier()
        val nextColIdx = x + direction.getColModifier()
        var nextObj = board[nextRowIdx][nextColIdx]

        if (nextRowIdx in rowRange && nextColIdx in colRange && nextObj.moveNext(direction, board)) {
            board[y][x] = null

            this.y = nextRowIdx
            this.x = nextColIdx

            board[nextRowIdx][nextColIdx] = this

            return true
        } else {
            return false
        }
    }

    if (this.type == WarehouseObjectType.Box) {
        if (direction == Direction.Left) {
            val nextRowIdx = y + direction.getRowModifier()
            val nextColIdx = x + direction.getColModifier()
            var nextObj = board[nextRowIdx][nextColIdx]

            if (nextRowIdx in rowRange && nextColIdx in colRange && nextObj.moveNext(direction, board)) {
                board[y][x] = null
                board[y][x + 1] = null

                this.y = nextRowIdx
                this.x = nextColIdx

                board[nextRowIdx][nextColIdx] = this
                board[nextRowIdx][nextColIdx + 1] = this

                return true
            } else {
                return false
            }
        } else if (direction == Direction.Right) {
            val nextRowIdx = y + direction.getRowModifier()
            val nextColIdx = x + direction.getColModifier() * widthX
            var nextObj = board[nextRowIdx][nextColIdx]

            if (nextRowIdx in rowRange && nextColIdx in colRange && nextObj.moveNext(direction, board)) {
                board[y][x] = null
                board[y][x + 1] = null

                this.y = nextRowIdx
                this.x = nextColIdx - 1

                board[nextRowIdx][nextColIdx - 1] = this
                board[nextRowIdx][nextColIdx] = this

                return true
            } else {
                return false
            }
        } else if (direction == Direction.Up || direction == Direction.Down) {
            val nextRowIdx = y + direction.getRowModifier()
            val leftNextColIdx = x
            val rightNextColIdx = x + 1
            var leftNextObj = board[nextRowIdx][leftNextColIdx]
            var rightNextObj = board[nextRowIdx][rightNextColIdx]

            val canMoveNextLeft = leftNextObj.canMoveNext(direction, board)
            val canMoveNextRight = rightNextObj.canMoveNext(direction, board)

            if (nextRowIdx in rowRange && leftNextColIdx in colRange && rightNextColIdx in colRange && canMoveNextLeft && canMoveNextRight) {
                val moveLeft = leftNextObj.moveNext(direction, board)
                val moveRight = if (leftNextObj != rightNextObj) rightNextObj.moveNext(direction, board) else moveLeft
                if (!(moveLeft && moveRight)) {
                    throw IllegalStateException("cannot happen, or at least it shouldn't")
                }

                board[y][x] = null
                board[y][x + 1] = null

                this.y = nextRowIdx
                this.x = leftNextColIdx

                board[nextRowIdx][leftNextColIdx] = this
                board[nextRowIdx][rightNextColIdx] = this

                return true
            }
        }
    }

    return false
}

fun WarehouseObject2?.canMoveNext(direction: Direction, board: Warehouse2): Boolean {
    if (this?.type == WarehouseObjectType.Wall) {
        return false
    }

    if (this == null) {
        return true
    }

    val rowRange = 0 until board.size
    val colRange = 0 until board.first().size

    if (this.type == WarehouseObjectType.Box) {
        if (direction == Direction.Left) {
            val nextRowIdx = y + direction.getRowModifier()
            val nextColIdx = x + direction.getColModifier()
            var nextObj = board[nextRowIdx][nextColIdx]

            return nextRowIdx in rowRange && nextColIdx in colRange && nextObj?.canMoveNext(direction, board) == true
        }

        if (direction == Direction.Right) {
            val nextRowIdx = y + direction.getRowModifier()
            val nextColIdx = x + direction.getColModifier() * widthX
            var nextObj = board[nextRowIdx][nextColIdx]

            return nextRowIdx in rowRange && nextColIdx in colRange && nextObj?.canMoveNext(direction, board) == true
        }

        if (direction == Direction.Up || direction == Direction.Down) {
            val nextRowIdx = y + direction.getRowModifier()
            val leftNextColIdx = x
            val rightNextColIdx = x + 1
            var leftNextObj = board[nextRowIdx][leftNextColIdx]
            var rightNextObj = board[nextRowIdx][rightNextColIdx]

            return nextRowIdx in rowRange && leftNextColIdx in colRange && rightNextColIdx in colRange && leftNextObj.canMoveNext(direction, board) && rightNextObj.canMoveNext(direction, board)
        }
    }

    return false
}

data class WarehouseObject2(val type: WarehouseObjectType, var x: Int, var y: Int, val widthX: Int)