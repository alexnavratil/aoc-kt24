import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day8/input.txt").toURI()

    val board = Files.readAllLines(Paths.get(inputPath))
        .map { line -> line.toCharArray().toList() }

    val antennaMarkerExpr = Regex("""[a-zA-Z\d]""")

    val antennas = board.flatMapIndexed { rowIdx, row ->
        row.withIndex()
            .filter { (_, ch) -> antennaMarkerExpr.containsMatchIn(ch.toString()) }
            .map { Triple(it.value, rowIdx, it.index) }
    }

    val antinodes = antennas
        .groupBy { antenna -> antenna.first }
        .flatMap { entry ->
            entry.value.flatMap { antenna ->
                entry.value.filterNot { it == antenna }.map { otherAntenna -> antenna.getAntinode(otherAntenna) }
            }
        }
        .filter { board.isInsideBoard(it.first, it.second) }
        .toSet()

    println(antinodes.size)
}

fun Triple<Char, Int, Int>.getAntinode(other: Triple<Char, Int, Int>, multiplicator: Int = 1): Pair<Int, Int> {
    val rowDiff = this.second - other.second
    val colDiff = this.third - other.third

    return Pair(this.second + multiplicator * rowDiff, this.third + multiplicator * colDiff)
}