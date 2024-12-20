import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day19/input.txt").toURI()

    val lines = Files.readAllLines(Paths.get(inputPath))

    val availableSubPatterns = lines.first().split(", ")

    val patterns = lines.drop(2)

    val possiblePatterns = patterns.filter { isPatternPossible(it, availableSubPatterns) }.toList()

    println(possiblePatterns.size)
}

fun isPatternPossible(pattern: String, availableSubPatterns: List<String>): Boolean {
    val potentialSubPatterns = availableSubPatterns.filter { pattern.contains(it) }

    var solutionBranches = mutableSetOf<String>()

    potentialSubPatterns.filter { pattern.startsWith(it) }.forEach { solutionBranches += listOf(it) }

    do {
        solutionBranches = solutionBranches.flatMap { branch ->
            val nextBranches = potentialSubPatterns.filter { pattern.startsWith(branch + it) }

            return@flatMap nextBranches.map { branch + it }
        }.toMutableSet()

        if (solutionBranches.any { pattern == it }) {
            return true
        }
    } while (solutionBranches.isNotEmpty())

    return false
}