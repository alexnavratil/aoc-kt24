import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.pow

typealias Opcode = Long
typealias Operand = Long
typealias Instruction = Pair<Opcode, Operand>
typealias Program = List<Instruction>

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day17/input.txt").toURI()

    val inputLines = Files.readString(Paths.get(inputPath))

    val interpreterExp =
        Regex("""Register A: (?<a>\d*)(\r\n|\r|\n)Register B: (?<b>\d*)(\r\n|\r|\n)Register C: (?<c>\d*)(\r\n|\r|\n)(\r\n|\r|\n)Program: (?<program>.*)""")
    val interpreterMatch = interpreterExp.find(inputLines)!!

    val registerA = interpreterMatch.groups["a"]!!.value.toLong()
    val registerB = interpreterMatch.groups["b"]!!.value.toLong()
    val registerC = interpreterMatch.groups["c"]!!.value.toLong()
    val programCode: Program = interpreterMatch.groups["program"]!!.value
        .split(",")
        .map { it.toLong() }
        .chunked(2)
        .map { Pair<Long, Long>(it.first(), it.last()) }


    val computer = ThreeBitComputer(programCode, registerA, registerB, registerC)
    computer.run()

    val output = computer.output.joinToString(",")

    println(output)
}

class ThreeBitComputer(
    private var programCode: Program,
    private var registerA: Long,
    private var registerB: Long,
    private var registerC: Long
) {
    var instructionPointer = 0
    var enableAutoIncreaseInstructionPointer = true
    var output = mutableListOf<Int>()

    fun run() {
        output.clear()

        while (instructionPointer < (programCode.size * 2)) {
            val instruction = programCode[instructionPointer / 2]

            executeInstruction(instruction)

            if (enableAutoIncreaseInstructionPointer) {
                instructionPointer += 2
            } else {
                enableAutoIncreaseInstructionPointer = true
            }
        }
    }

    private fun getComboOperand(operand: Operand): Long = when(operand) {
        0L, 1L, 2L, 3L -> operand
        4L -> registerA
        5L -> registerB
        6L -> registerC
        7L -> throw IllegalArgumentException("Combo Operand 7 is reserved. Program will halt!")
        else -> throw IllegalArgumentException("Combo Operand '$operand' is invalid. Program will halt!")
    }

    private fun executeInstruction(instruction: Instruction) {
        val (opcode, operand) = instruction
        when(opcode) {
            0L -> adv(operand)
            1L -> bxl(operand)
            2L -> bst(operand)
            3L -> jnz(operand)
            4L -> bxc(operand)
            5L -> out(operand)
            6L -> bdv(operand)
            7L -> cdv(operand)
            else -> throw IllegalArgumentException("Instruction '$opcode' is invalid. Program will halt!")
        }
    }

    private fun adv(operand: Operand) {
        registerA = (registerA / 2.0.pow(getComboOperand(operand).toInt())).toLong()
    }

    private fun bxl(operand: Operand) {
        registerB = registerB xor operand
    }

    private fun bst(operand: Operand) {
        registerB = getComboOperand(operand).mod(8L)
    }

    private fun jnz(operand: Operand) {
        if (registerA == 0L) return
        instructionPointer = operand.toInt()
        enableAutoIncreaseInstructionPointer = false
    }

    private fun bxc(operand: Operand) {
        registerB = registerB xor registerC
    }

    private fun out(operand: Operand) {
        output += getComboOperand(operand).mod(8)
    }

    private fun bdv(operand: Operand) {
        registerB = (registerA / 2.0.pow(getComboOperand(operand).toInt()).toInt()).toLong()
    }

    private fun cdv(operand: Operand) {
        registerC = (registerA / 2.0.pow(getComboOperand(operand).toInt()).toInt()).toLong()
    }
}
