import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.floor

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day5/input.txt").toURI()

    val lines = Files.readAllLines(Paths.get(inputPath))

    val orderingRules = lines.takeWhile { line -> line != "" }
        .map { line -> line.split("|").let { pageNrs -> Pair(pageNrs.first().toInt(), pageNrs.last().toInt()) } }

    val pages = lines.dropWhile { lines -> lines != "" }
        .drop(1)
        .map { line -> line.split(",").map { pageNr -> pageNr.toInt() } }

    val validMiddlePageNumberSum = pages.filter { pageList -> pageList.isPageOrderingValid(orderingRules) }
        .sumOf { pageList -> pageList[floor(pageList.size / 2f).toInt()] }

    println(validMiddlePageNumberSum)
}

fun List<Int>.isPageOrderingValid(orderingRules: List<Pair<Int, Int>>): Boolean {
    return orderingRules.all { rule -> rule.isOrderingRuleValid(this) }
}

fun Pair<Int, Int>.isOrderingRuleValid(pages: List<Int>): Boolean {
    if (!(pages.contains(first) && pages.contains(second))) {
        return true
    }

    return pages.indexOf(first) <= pages.indexOf(second)
}