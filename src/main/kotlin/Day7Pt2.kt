import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.floor

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day7/input.txt").toURI()

    val operations = listOf(MathOperation.Add, MathOperation.Multiply, MathOperation.Concatenation)

    val equations = Files.readAllLines(Paths.get(inputPath))
        .map { line -> line.split(": ").let { strings -> Pair(strings[0].toLong(), strings[1].split(" ").map{ it.toLong() }) } }

    val possibleEquations = equations.filter { pair ->
        val combinations = generateOperationCombinations(pair.second.size - 1, operations)

        combinations.any { operations -> pair.second.reduceIndexed { index, acc, num -> operations.getOrNull(index - 1)?.apply(acc, num) ?: acc } == pair.first }
    }

    val sumPossibleEquations = possibleEquations.sumOf { pair -> pair.first }
    println(sumPossibleEquations)
}