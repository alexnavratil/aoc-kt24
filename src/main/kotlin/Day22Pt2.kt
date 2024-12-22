import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day22/input.txt").toURI()

    val inputLines = Files.readAllLines(Paths.get(inputPath)).map { it.toLong() }
    val prices = inputLines.associate {
        it to it.calculateNextSecretNumbers(MaxSecretNumbersPerDay)
    }

    val realPrices = prices.mapValues { it.value.map { price -> price.mod(10L) } }

    val realPricesWithChanges = realPrices.mapValues { (_, value) -> (listOf(value.first()) + value).windowed(2).map { Pair(it.last(), it.last() - it.first()) } }

    var priceChangesPerSecretNumber = mapOf<Long, Map<Quadruple<Long, Long, Long, Long>, Long>>()
    priceChangesPerSecretNumber = realPricesWithChanges.toMap().mapValues { (_, values) ->
        values.windowed(4).groupBy { Quadruple(it[0].second, it[1].second, it[2].second, it[3].second) }.mapValues { (_, values) -> values.first().last().first }
    }

    val allChangeWindows = priceChangesPerSecretNumber.flatMap { it.value.keys }.toSet()
    val bestChangeWindow = allChangeWindows.maxBy { changeWindow -> priceChangesPerSecretNumber.map { it.value.getOrDefault(changeWindow, 0L) }.sum() }

    val result = priceChangesPerSecretNumber.map { it.value.getOrDefault(bestChangeWindow, 0L) }.sum()
    println(result)
}

fun Long.calculateNextSecretNumbers(times: Long): List<Long> {
    var currentNumber = this

    return listOf(this) + (0 until times).map {
        currentNumber = currentNumber.calculateNextSecretNumber()
        currentNumber
    }
}

data class Quadruple<T1, T2, T3, T4>(val first: T1, val second: T2, val third: T3, val fourth: T4)