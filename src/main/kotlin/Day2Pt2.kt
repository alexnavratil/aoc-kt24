import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day2/input.txt").toURI()

    val lines = Files.readAllLines(Paths.get(inputPath))

    var reports = lines.map { line -> line.split(" ").map { it.toInt() } }
    var safeReports = reports.filter { report -> report.toMutableList().isSafePart2() }

    println(safeReports.size)
}

fun MutableList<Int>.isSafePart2(): Boolean {
    var pairs = zipWithNext()
    val increasingPairs = pairs.filter { pair -> pair.first < pair.second }
    val decreasingPairs = pairs.filter { pair -> pair.first > pair.second }

    val isIncreasing = increasingPairs.count() > decreasingPairs.count()

    val wrongPairs = pairs.map { it.first - it.second }
        .map { diff -> if (isIncreasing) diff < -3 || diff > -1 else diff > 3 || diff < 1 }
    val wrongPairsCount = wrongPairs.count { it == true }

    // > 2 because of edge case: 68 65 69 72 74 77 80 83
    if (wrongPairsCount > 2) {
        return false
    }

    if (wrongPairsCount != 0) {
        val wrongPairIndex = wrongPairs.indexOfFirst { wrongPair -> wrongPair }

        val wrongPairsWithoutFirst =
            filterIndexed { index, pair -> index != wrongPairIndex }
                .zipWithNext()
                .map { it.first - it.second }
                .map { diff -> if (isIncreasing) diff < -3 || diff > -1 else diff > 3 || diff < 1 }

        val wrongPairsWithoutSecond =
            filterIndexed { index, pair -> index != wrongPairIndex + 1 }
                .zipWithNext()
                .map { it.first - it.second }
                .map { diff -> if (isIncreasing) diff < -3 || diff > -1 else diff > 3 || diff < 1 }

        return wrongPairsWithoutFirst.none { it } || wrongPairsWithoutSecond.none { it }
    }

    return true
}