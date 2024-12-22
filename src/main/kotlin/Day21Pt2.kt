import java.nio.file.Files
import java.nio.file.Paths

const val KeyboardConundrumMaxDepthPart2 = 26

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day21/input.txt").toURI()

    val inputLines = Files.readAllLines(Paths.get(inputPath))

    val result = inputLines.sumOf {
        val numPart = it.substring(0, it.length - 1)
        val historiansDirectionalPath = calculateShortestPath(numPart, KeyboardConundrumMaxDepthPart2)
        val inputScore = numPart.toInt()
        val shortestPathScore = historiansDirectionalPath

        println("$it: $historiansDirectionalPath")

        return@sumOf inputScore * shortestPathScore
    }

    println(result)
}

fun calculateShortestPath(line: String, depth: Int, cache: MutableMap<Pair<String, Int>, Long> = mutableMapOf()): Long {
    if (depth == 0) {
        return line.length + 1L // last directional keypad reached -> return length + 1 for button A
    }

    if (cache.containsKey(Pair(line, depth))) {
        return cache[Pair(line, depth)]!!
    }

    val nums = "A${line}A".toCharArray().map { it.toString() }
    val numPairs = nums.zipWithNext()

    if (depth == KeyboardConundrumMaxDepthPart2) {
        val directionalPathForNumericKeypad = numPairs.joinToString("A") { getNumericKeypadPath(it.first.first(), it.second.first()) } + "A"

        cache[Pair(line, depth)] = directionalPathForNumericKeypad.split("A").dropLast(1).sumOf { calculateShortestPath(it, depth - 1, cache) }
    } else {
        val directionalPathForDirectionalKeypad = numPairs.joinToString("A") { getDirectionalKeypadPath(it.first.first(), it.second.first()) } + "A"

        cache[Pair(line, depth)] = directionalPathForDirectionalKeypad.split("A").dropLast(1).sumOf { calculateShortestPath(it, depth - 1, cache) }
    }

    return cache[Pair(line, depth)]!!
}