import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day11/input.txt").toURI()

    val stones = Files.readString(Paths.get(inputPath)).split(" ").map { it.toLong() }.toMutableList()

    var numIterations = 75

    var stoneCounts = stones.groupBy { it }.mapValues { it.value.size.toLong() }.toMutableMap()

    for (i in 0 until numIterations) {
        println("$i: ${stoneCounts.size}")

        val nextStoneCounts = mutableMapOf<Long, Long>()

        stoneCounts.forEach { stone, count ->
            if (stone == 0L) {
                nextStoneCounts[1L] = nextStoneCounts.getOrDefault(1L, 0L) + count
            } else {
                val numDigits = floor(log10(stone.toDouble()) + 1).toLong()

                if (numDigits % 2 == 0L) {
                    val k = 10.0.pow(numDigits / 2.0).toLong()

                    // split number into two half (the first X/2 digits and the last X/2 digits)
                    val first = stone / k
                    val second = stone.mod(k)

                    nextStoneCounts[first] = nextStoneCounts.getOrDefault(first, 0) + count
                    nextStoneCounts[second] = nextStoneCounts.getOrDefault(second, 0) + count
                } else {
                    val nextStone = stone * 2024
                    nextStoneCounts[nextStone] = nextStoneCounts.getOrDefault(nextStone, 0) + count
                }
            }
        }

        stoneCounts = nextStoneCounts
    }

    println(stoneCounts.map { it.value.toLong() }.sum())
}
