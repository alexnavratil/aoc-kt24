import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.max

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day21/input.txt").toURI()

    val inputLines = Files.readAllLines(Paths.get(inputPath))

    val result = inputLines.sumOf {
        val nums = "A$it".toCharArray().map { it.toString() }
        val numPairs = nums.zipWithNext()

        val radiationDirectionalPath = numPairs.joinToString("A") { getNumericKeypadPath(it.first.first(), it.second.first()) } + "A"

        val coldDirectionalPath = "A$radiationDirectionalPath".toCharArray().map { it.toString() }.zipWithNext().joinToString("A") { getDirectionalKeypadPath(it.first.first(), it.second.first()) } + "A"

        val historiansDirectionalPath = "A$coldDirectionalPath".toCharArray().map { it.toString() }.zipWithNext().joinToString("A") { getDirectionalKeypadPath(it.first.first(), it.second.first()) } + "A"

        val inputScore = it.substring(0, it.length - 1).toInt()
        val shortestPathScore = historiansDirectionalPath.length

        println("$it: $historiansDirectionalPath (length: $shortestPathScore)")

        return@sumOf inputScore * shortestPathScore
    }

    println(result)
}

fun getNumericKeypadPath(from: Char, to: Char, goVerticalFirst: Boolean = false): String {
   val coordinates = mapOf(
       '7' to Pair(0, 0),
       '8' to Pair(0, 1),
       '9' to Pair(0, 2),
       '4' to Pair(1, 0),
       '5' to Pair(1, 1),
       '6' to Pair(1, 2),
       '1' to Pair(2, 0),
       '2' to Pair(2, 1),
       '3' to Pair(2, 2),
       '0' to Pair(3, 1),
       'A' to Pair(3, 2),
   )

    return when(Pair(from, to)) {
        Pair('7', '0'), Pair('7', 'A') -> "${getNumericKeypadPath(from, '8')}${getNumericKeypadPath('8', to)}" // skip optimal routes over blank field via 8
        Pair('4', '0'), Pair('4', 'A') -> "${getNumericKeypadPath(from, '5')}${getNumericKeypadPath('5', to)}" // skip optimal routes over blank field via 5
        Pair('1', '0'), Pair('1', 'A') -> "${getNumericKeypadPath(from, '2')}${getNumericKeypadPath('2', to)}" // skip optimal routes over blank field via 2
        Pair('0', '7'), Pair('0', '4'), Pair('0', '1') -> "${getNumericKeypadPath(from, '2')}${getNumericKeypadPath('2', to, true)}" // skip optimal routes over blank field via 2
        Pair('A', '7'), Pair('A', '4'), Pair('A', '1') -> "${getNumericKeypadPath(from, '3')}${getNumericKeypadPath('3', to, true)}" // skip optimal routes over blank field via 3
        Pair('2', 'A') -> "v>" // some edge cases leading to a shorter path (needed for the last code in part 1)
        else -> if (goVerticalFirst) {
            (0 until (max(0, coordinates[from]!!.first - coordinates[to]!!.first))).joinToString("") { "^" } + (0 until (max(0, coordinates[to]!!.first - coordinates[from]!!.first))).joinToString("") { "v" } + (0 until (max(0, coordinates[from]!!.second - coordinates[to]!!.second))).joinToString("") { "<" } + (0 until (max(0, coordinates[to]!!.second - coordinates[from]!!.second))).joinToString("") { ">" }
        } else {
            ((0 until (max(0, coordinates[from]!!.second - coordinates[to]!!.second))).joinToString("") { "<" } + (0 until (max(0, coordinates[to]!!.second - coordinates[from]!!.second))).joinToString("") { ">" } + (0 until (max(0, coordinates[from]!!.first - coordinates[to]!!.first))).joinToString("") { "^" } + (0 until (max(0, coordinates[to]!!.first - coordinates[from]!!.first))).joinToString("") { "v" }).sortDirections()
        }
    }
}

fun getDirectionalKeypadPath(from: Char, to: Char): String {
    val coordinates = mapOf(
        '^' to Pair(0, 1),
        'A' to Pair(0, 2),
        '<' to Pair(1, 0),
        'v' to Pair(1, 1),
        '>' to Pair(1, 2),
    )

    return when(Pair(from, to)) {
        Pair('<', '^') -> "${getDirectionalKeypadPath(from, 'v')}${getDirectionalKeypadPath('v', to)}" // skip optimal routes over blank field via v
        Pair('^', '<') -> "${getDirectionalKeypadPath(from, 'v')}${getDirectionalKeypadPath('v', to)}" // skip optimal routes over blank field via v
        Pair('<', 'A') -> "${getDirectionalKeypadPath(from, '>')}${getDirectionalKeypadPath('>', to)}" // skip optimal routes over blank field via >
        Pair('A', '<') -> "${getDirectionalKeypadPath(from, '>')}${getDirectionalKeypadPath('>', to)}" // skip optimal routes over blank field via >
        else -> ((0 until (max(0, coordinates[from]!!.second - coordinates[to]!!.second))).joinToString("") { "<" } + (0 until (max(0, coordinates[to]!!.second - coordinates[from]!!.second))).joinToString("") { ">" } + (0 until (max(0, coordinates[from]!!.first - coordinates[to]!!.first))).joinToString("") { "^" } + (0 until (max(0, coordinates[to]!!.first - coordinates[from]!!.first))).joinToString("") { "v" }).sortDirections()
    }
}

fun String.sortDirections(): String {
    val prioritySet = mapOf(
        '<' to 0,
        'v' to 1,
        '^' to 2,
        '>' to 3,
        'A' to 4
    )

    return this.toCharArray().sortedWith(object : Comparator<Char> {
        override fun compare(o1: Char?, o2: Char?): Int {
            return prioritySet[o1]!! - prioritySet[o2]!!
        }
    }).joinToString("")
}