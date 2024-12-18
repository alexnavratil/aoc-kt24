import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day17/input.txt").toURI()

    val inputLines = Files.readString(Paths.get(inputPath))

    val interpreterExp =
        Regex("""Register A: (?<a>\d*)(\r\n|\r|\n)Register B: (?<b>\d*)(\r\n|\r|\n)Register C: (?<c>\d*)(\r\n|\r|\n)(\r\n|\r|\n)Program: (?<program>.*)""")
    val interpreterMatch = interpreterExp.find(inputLines)!!

    val registerB = interpreterMatch.groups["b"]!!.value.toLong()
    val registerC = interpreterMatch.groups["c"]!!.value.toLong()
    val programCodeStr = interpreterMatch.groups["program"]!!.value
    val programCode: Program = programCodeStr
        .split(",")
        .map { it.toLong() }
        .chunked(2)
        .map { Pair<Long, Long>(it.first(), it.last()) }

    val programCodeOutput = programCode.flatMap { listOf(it.first, it.second) }.map { it.toInt() }

    var result = 0L

    (0 until programCodeOutput.size).reversed().forEach { digitIdx ->
        val matchingNum = (0..Int.MAX_VALUE).first { inputNum ->
            var trial = result + (1L.shl(digitIdx * 3) * inputNum)

            val computer = ThreeBitComputer(programCode, trial, registerB, registerC)
            computer.run()

            return@first computer.output.drop(digitIdx) == programCodeOutput.drop(digitIdx)
        }

        result += 1L.shl(digitIdx * 3) * matchingNum
        println("step: $result (bin: ${result.toString(2)}) (oct: ${result.toString(8)})")
    }

    println(result)
}
