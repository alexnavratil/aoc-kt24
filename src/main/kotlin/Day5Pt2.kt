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

    val correctedMiddleSum = pages.filter { pageList -> !pageList.isPageOrderingValid(orderingRules) }
        .map { pageList -> pageList.correctOrdering(orderingRules) }
        .sumOf { pageList -> pageList[floor(pageList.size / 2f).toInt()] }

    println(correctedMiddleSum)
}

fun List<Int>.correctOrdering(orderingRules: List<Pair<Int, Int>>): List<Int> {
    val resultPageList = mutableListOf<Int>()
    val remainingPages = this.toMutableList()


    this.forEachIndexed { index, page ->
        val nextNumber = remainingPages.first { nextPage -> orderingRules.all { rule -> rule.isOrderingRuleValid(resultPageList + nextPage, remainingPages - nextPage) } }
        resultPageList.add(nextNumber)
        remainingPages.remove(nextNumber)
    }

    if (!resultPageList.isPageOrderingValid(orderingRules)) {
        throw IllegalStateException("Ordering of incorrect page list: $this resulting in $resultPageList failed!")
    }

    return resultPageList;
}

fun Pair<Int, Int>.isOrderingRuleValid(correctedPages: List<Int>, remainingPages: List<Int>): Boolean {
    return remainingPages.all { remainingPage -> this.isOrderingRuleValid(correctedPages + remainingPage) }
}
