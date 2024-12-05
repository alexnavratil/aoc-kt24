import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day4/input.txt").toURI()

    val content = Files.readString(Paths.get(inputPath))
    val lines = content.split("\r\n")

    val diagonalMatches = lines.countDiagonalMas()

    println(diagonalMatches)
}

fun List<String>.countDiagonalMas(): Int {
    return mapIndexed { lineIdx, line ->
        line.mapIndexed { colIdx, ch ->
            if (ch == 'A') {
                val count = this.findDiagonalMas(lineIdx, colIdx, true, true).toInt() +
                        findDiagonalMas(lineIdx, colIdx, false, false).toInt() +
                        findDiagonalMas(lineIdx, colIdx, true, false).toInt() +
                        findDiagonalMas(lineIdx, colIdx, false, true).toInt()
                if (count == 2) {
                    1
                } else {
                    0
                }
            } else {
                0
            }
        }.sum()
    }.sum()
}

fun List<String>.findDiagonalMas(startLineIdx: Int, startColIdx: Int, goLeft: Boolean, goUp: Boolean): Boolean {
    val colIdxFactor = if (goLeft) 1 else -1
    val lineIdxFactor = if (goUp) -1 else 1

    val firstChar = this.getOrNull(startLineIdx + lineIdxFactor * -1)?.getOrNull(startColIdx + colIdxFactor * -1)
    val secondChar = this.getOrNull(startLineIdx)?.getOrNull(startColIdx)
    val thirdChar = this.getOrNull(startLineIdx + lineIdxFactor)?.getOrNull(startColIdx + colIdxFactor)

    return firstChar == 'M' && secondChar == 'A' && thirdChar == 'S'
}