import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day7/input.txt").toURI()

    val operations = listOf(MathOperation.Add, MathOperation.Multiply)

    val equations = Files.readAllLines(Paths.get(inputPath))
        .map { line -> line.split(": ").let { strings -> Pair(strings[0].toLong(), strings[1].split(" ").map{ it.toLong() }) } }

    val possibleEquations = equations.filter { pair ->
        val combinations = generateOperationCombinations(pair.second.size - 1, operations)

        combinations.any { operations -> pair.second.reduceIndexed { index, acc, num -> operations.getOrNull(index - 1)?.apply(acc, num) ?: acc } == pair.first }
    }

    val sumPossibleEquations = possibleEquations.sumOf { pair -> pair.first }
    println(sumPossibleEquations)
}

fun generateOperationCombinations(placeholders: Int, operations: List<MathOperation>): List<List<MathOperation>> {
    val results = mutableListOf<List<MathOperation>>()

    fun appendOperations(current: List<MathOperation>, position: Int) {
        if (position == placeholders) {
            results.add(current)
            return
        }
        for (op in operations) {
            appendOperations(current + op, position + 1)
        }
    }

    appendOperations(emptyList(), 0)

    return results
}

fun MathOperation.apply(num1: Long, num2: Long): Long = when(this) {
    MathOperation.Add -> num1 + num2
    MathOperation.Multiply -> num1 * num2
    MathOperation.Concatenation -> "${num1}${num2}".toLong()
}

enum class MathOperation {
    Add,
    Multiply,
    Concatenation
}