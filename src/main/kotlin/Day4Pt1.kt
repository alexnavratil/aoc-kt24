import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day4/input.txt").toURI()

    val content = Files.readString(Paths.get(inputPath))
    val lines = content.split("\r\n")

    val horizontalExpr = Regex("""XMAS""")
    val horizontalMatches = horizontalExpr.findAll(content).count()

    val backwardsHorizontalExpr = Regex("""SAMX""")
    val backwardsHorizontalMatches = backwardsHorizontalExpr.findAll(content).count()

    val contentTransposed = lines[0].mapIndexed { index, ch -> lines.map { line -> line[index] }.joinToString("") }.joinToString("\n")

    val verticalExpr = Regex("""XMAS""")
    val verticalMatches = verticalExpr.findAll(contentTransposed).count()

    val backwardsVerticalExpr = Regex("""SAMX""")
    val backwardsVerticalMatches = backwardsVerticalExpr.findAll(contentTransposed).count()

    val diagonalMatches = lines.countDiagonal()

    println(horizontalMatches + backwardsHorizontalMatches + verticalMatches + backwardsVerticalMatches + diagonalMatches)
}

fun List<String>.countDiagonal(): Int {
    return mapIndexed { lineIdx, line ->
        line.mapIndexed { colIdx, ch ->
            if (ch == 'X') {
                this.findXmas(lineIdx, colIdx, true, true).toInt() +
                        findXmas(lineIdx, colIdx, false, false).toInt() +
                        findXmas(lineIdx, colIdx, true, false).toInt() +
                        findXmas(lineIdx, colIdx, false, true).toInt()
            } else {
                0
            }
        }.sum()
    }.sum()
}

fun List<String>.findXmas(startLineIdx: Int, startColIdx: Int, goLeft: Boolean, goUp: Boolean): Boolean {
    val colIdxFactor = if (goLeft) 1 else -1
    val lineIdxFactor = if (goUp) -1 else 1

    val secondChar = this.getOrNull(startLineIdx + lineIdxFactor)?.getOrNull(startColIdx + colIdxFactor)
    val thirdChar = this.getOrNull(startLineIdx + 2 * lineIdxFactor)?.getOrNull(startColIdx + 2 * colIdxFactor)
    val fourthChar = this.getOrNull(startLineIdx + 3 * lineIdxFactor)?.getOrNull(startColIdx + 3 * colIdxFactor)

    return secondChar == 'M' && thirdChar == 'A' && fourthChar == 'S'
}

fun Boolean.toInt() = if (this) 1 else 0