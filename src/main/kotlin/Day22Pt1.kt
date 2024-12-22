import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.floor

const val SpecialModValue = 16777216L
const val MaxSecretNumbersPerDay = 2000L

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day22/input.txt").toURI()

    val inputLines = Files.readAllLines(Paths.get(inputPath)).map { it.toLong() }
    val result = inputLines.sumOf { it.calculateLastSecretNumber(MaxSecretNumbersPerDay) }
    println(result)
}

fun Long.calculateLastSecretNumber(times: Long): Long {
    var currentNumber = this

    (0 until times).forEach {
        currentNumber = currentNumber.calculateNextSecretNumber()
    }

    return currentNumber
}

fun Long.calculateNextSecretNumber(): Long {
    var secretNumber = this
    var a = secretNumber * 64L
    secretNumber = secretNumber.xor(a).mod(SpecialModValue)

    var b = floor(secretNumber / 32.0).toLong()
    secretNumber = secretNumber.xor(b).mod(SpecialModValue)

    var c = secretNumber * 2048L
    secretNumber = secretNumber.xor(c).mod(SpecialModValue)

    return secretNumber
}
