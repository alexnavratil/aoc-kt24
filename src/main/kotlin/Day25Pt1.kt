import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day25/input.txt").toURI()

    val inputLines = Files.readString(Paths.get(inputPath))

    val inputKeysOrLocks = inputLines.split(Regex("""\r?\n\r?\n"""))

    val inputKeys = inputKeysOrLocks
        .filter { it.lines().last().count { it == '#' } == 5 }
        .map { it.parseLockOrKey() }

    val inputLocks = inputKeysOrLocks
        .filter { it.lines().first().count { it == '#' } == 5 }
        .map { it.parseLockOrKey() }

    val matchingLocks = inputKeys.sumOf { key -> inputLocks.count { lock -> key.keyMatchesLock(lock) } }
    println(matchingLocks)
}

fun IntArray.keyMatchesLock(lock: IntArray): Boolean {
    return this.mapIndexed { index, value -> value + lock[index] }.none { it > 5 }
}

fun String.parseLockOrKey(): IntArray {
    val dataLines = this.lines()

    return (0 until 5).map { i -> dataLines.count { it[i] == '#' } - 1 }.toIntArray()
}