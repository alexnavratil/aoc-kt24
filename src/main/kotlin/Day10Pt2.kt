import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day10/input.txt").toURI()

    val board = Files.readAllLines(Paths.get(inputPath))
        .map { line -> line.toCharArray().map { it.toString().toInt() } }

    val startingPositions = board
        .flatMapIndexed { rowIdx, heights ->
            heights.mapIndexed { colIdx, height ->
                Triple(
                    rowIdx,
                    colIdx,
                    height
                )
            }
        }
        .filter { it.third == 0 }

    val trailheadSum = startingPositions.sumOf { board.findTrailheads2(it.first, it.second).count() }

    println(trailheadSum)
}

fun List<List<Int>>.findTrailheads2(rowIdx: Int, colIdx: Int): List<Pair<Int, Int>> {
    val currentHeight = this[rowIdx][colIdx]

    if (currentHeight == 9) {
        return listOf(Pair(rowIdx, colIdx))
    }

    val heights = mutableListOf<Pair<Int, Int>>()

    val topRowIdx = rowIdx + Direction.Up.getRowModifier()
    val topColIdx = colIdx + Direction.Up.getColModifier()
    val topHeight = this.getOrNull(topRowIdx)?.getOrNull(topColIdx)

    if (topHeight == currentHeight + 1) {
        heights += findTrailheads2(topRowIdx, topColIdx)
    }

    val rightRowIdx = rowIdx + Direction.Right.getRowModifier()
    val rightColIdx = colIdx + Direction.Right.getColModifier()
    val rightHeight = this.getOrNull(rightRowIdx)?.getOrNull(rightColIdx)

    if (rightHeight == currentHeight + 1) {
        heights += findTrailheads2(rightRowIdx, rightColIdx)
    }

    val bottomRowIdx = rowIdx + Direction.Down.getRowModifier()
    val bottomColIdx = colIdx + Direction.Down.getColModifier()
    val bottomHeight = this.getOrNull(bottomRowIdx)?.getOrNull(bottomColIdx)

    if (bottomHeight == currentHeight + 1) {
        heights += findTrailheads2(bottomRowIdx, bottomColIdx)
    }

    val leftRowIdx = rowIdx + Direction.Left.getRowModifier()
    val leftColIdx = colIdx + Direction.Left.getColModifier()
    val leftHeight = this.getOrNull(leftRowIdx)?.getOrNull(leftColIdx)

    if (leftHeight == currentHeight + 1) {
        heights += findTrailheads2(leftRowIdx, leftColIdx)
    }

    return heights
}
