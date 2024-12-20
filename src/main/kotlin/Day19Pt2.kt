import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day19/input.txt").toURI()

    val lines = Files.readAllLines(Paths.get(inputPath))

    val availableSubPatterns = lines.first().split(", ")

    val patterns = lines.drop(2)

    val possiblePatternCombinations = patterns
        .filter { isPatternPossible(it, availableSubPatterns) }
        .sumOf { evaluatePossiblePatterns(it, availableSubPatterns) }

    println(possiblePatternCombinations)
}

fun evaluatePossiblePatterns(pattern: String, availableSubPatterns: List<String>): Long {
    val potentialSubPatterns = availableSubPatterns.filter { pattern.contains(it) }

    var solutionBranches = mutableSetOf<Pair<List<String>, Long>>()
    var solutionCount = 0L

    potentialSubPatterns.filter { pattern.startsWith(it) }.forEach { solutionBranches += Pair(listOf(it), 1L) }

    do {
        solutionBranches = solutionBranches.flatMap { branchPair ->
            val (branch, count) = branchPair
            var branchStr = branch.joinToString("")
            val nextBranches = potentialSubPatterns.filter { pattern.startsWith(branchStr + it) }

            return@flatMap nextBranches.map { Pair(branch + it, count) }
        }.toMutableSet()

        solutionBranches = solutionBranches.groupBy { it.first.joinToString("") }
            .mapValues { it.value.sumOf { branchPair -> branchPair.second } }
            .map { Pair(listOf(it.key), it.value) }
            .toMutableSet()

        solutionBranches.filter { pattern == it.first.joinToString("") }.forEach { solutionCount += it.second }
        solutionBranches.removeIf { pattern == it.first.joinToString("") }
    } while (solutionBranches.isNotEmpty())

    return solutionCount
}