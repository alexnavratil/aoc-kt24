import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day1/input.txt").toURI()

    val lines = Files.readAllLines(Paths.get(inputPath))

    var pairs = lines.map { line -> line.split("   ").let { Pair(it[0].toInt(), it[1].toInt()) } }
    var leftList = pairs.map { it.first }
    var rightList = pairs.map { it.second }.groupBy { i -> i }.mapValues { it.value.size }

    var diffList = leftList.map { i -> i * (rightList[i] ?: 0) }

    val result = diffList.sum()
    println(result)
}