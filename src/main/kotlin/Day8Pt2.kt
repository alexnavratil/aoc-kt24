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
                entry.value.filterNot { it == antenna }.flatMap { otherAntenna -> antenna.getHarmonicResonantHarmonics(otherAntenna, board) }
            }
        }
        .toSet()

    println(antinodes.size)
}


fun Triple<Char, Int, Int>.getHarmonicResonantHarmonics(other: Triple<Char, Int, Int>, board: List<List<Char>>): List<Pair<Int, Int>> {
    val antinodes = mutableListOf<Pair<Int, Int>>(Pair(second, third)) // This means that some of the new antinodes will occur at the position of each antenna (unless that antenna is the only one of its frequency).
    var multiplicator = 1

    while (true) {
        val antinode = this.getAntinode(other, multiplicator)

        if (!board.isInsideBoard(antinode.first, antinode.second)) {
            return antinodes
        }

        antinodes.add(antinode)
        multiplicator++
    }
}