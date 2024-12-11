import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day11/input.txt").toURI()

    var stones = Files.readString(Paths.get(inputPath)).split(" ").map { it.toLong() }

    var numIterations = 25

    for (i in 0 until numIterations) {
        stones = stones.flatMap { stone ->
            if (stone == 0L) {
                return@flatMap listOf(1L)
            }

            if (stone.toString().length % 2 == 0) {
                return@flatMap listOf(stone.toString().substring(0, stone.toString().length / 2).toLong(), stone.toString().substring(stone.toString().length / 2).toLong())
            }

            return@flatMap listOf(stone * 2024L)
        }
    }

    println(stones.size)
}