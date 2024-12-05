import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.abs

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day1/input.txt").toURI()

    val lines = Files.readAllLines(Paths.get(inputPath))

    var pairs = lines.map { line -> line.split("   ").let { Pair(it[0].toInt(), it[1].toInt()) } }
    var leftList = pairs.map { it.first }.sorted()
    var rightList = pairs.map { it.second }.sorted()

    var diffList = leftList.zip(rightList).map { pair -> abs(pair.first - pair.second) }

    val result = diffList.sum()
    println(result)
}