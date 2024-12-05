import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.abs

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day2/input.txt").toURI()

    val lines = Files.readAllLines(Paths.get(inputPath))

    var reports = lines.map { line -> line.split(" ").map { it.toInt() } }
    var safeReports = reports.filter { report -> report.isSafe() }

    println(safeReports.size)
}

fun List<Int>.isSafe(): Boolean {
    val isIncreasing = first() < last()
    val isAnyPairWrong = zipWithNext().any { pair -> if (isIncreasing) pair.first > pair.second else pair.first < pair.second}

    if (isAnyPairWrong) {
        return false
    }

    val diffs = zipWithNext().map { pair -> abs(pair.first - pair.second) }

    val minDiff = diffs.min()
    val maxDiff = diffs.max()

    return minDiff >= 1 && maxDiff <= 3
}