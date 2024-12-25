import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.floor
import kotlin.mod

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day24/input.txt").toURI()

    val inputLines = Files.readAllLines(Paths.get(inputPath))

    // example: ntg XOR fgs -> mjb
    val gateExpr =
        Regex("""(?<firstVariableName>...) (?<gateType>.{2,3}) (?<secondVariableName>...) -> (?<outputVariableName>...)""")

    val initialVariables =
        inputLines.takeWhile { it.isNotEmpty() }.map { it.split(": ") }.map { Pair(it[0], it[1].toInt()) }
    val logic = inputLines.dropWhile { it.isNotEmpty() }.drop(1).map {
        val matchResult = gateExpr.find(it)!!

        Gate(
            matchResult.groups["firstVariableName"]!!.value,
            matchResult.groups["secondVariableName"]!!.value,
            matchResult.groups["gateType"]!!.value.toGateType(),
            matchResult.groups["outputVariableName"]!!.value
        )
    }

    val variables = initialVariables.associate { it.first to it.second }.toMutableMap()

    // observation: all logic lines resulting in an output (z**) must use XOR as gate type, or it is the last output (no carry)
    var firstRuleViolations =
        logic.filter { it.outputVariableName.startsWith("z") && it.gateType != GateType.XOR && it.outputVariableName != "z45" }

    // observation: all intermediate logics (not resulting in z**) that do not involve x or y as gate operands must be AND or OR, but not XOR
    // these seem to be all faulty carries
    var secondRuleViolations = logic
        .filter {
            !it.outputVariableName.startsWith("z") &&
            !it.firstInputName.startsWith("x") &&
                    !it.firstInputName.startsWith("y") &&
                    !it.secondInputName.startsWith("x") &&
                    !it.secondInputName.startsWith("y")
        }.filter { it.gateType == GateType.XOR }

    var correctedLogic = logic.flatMap {
        if (it in firstRuleViolations) {
            val adderIndex = it.outputVariableName.drop(1).toInt()
            val exchangeOutput = secondRuleViolations.find { getOriginatingAdderFromFaultyCarry(it.outputVariableName, logic) == adderIndex }!!

            listOf(it.copy(outputVariableName = exchangeOutput.outputVariableName), exchangeOutput.copy(outputVariableName = it.outputVariableName))
        } else if (it in secondRuleViolations) {
            emptyList()
        } else {
            listOf(it)
        }
    }

    val correctedResult = evaluate(correctedLogic, variables)

    val inputX = variables.filter { it.key.startsWith("x") }.toList().sortedBy { it.first }.reversed().map { it.second }.joinToString("").toLong(2)
    val inputY = variables.filter { it.key.startsWith("y") }.toList().sortedBy { it.first }.reversed().map { it.second }.joinToString("").toLong(2)
    val correctResult = inputX + inputY

    val remainingFaultyCarryAdderIndex = correctResult.xor(correctedResult.toLong(2)).countTrailingZeroBits()
    val faultyCarryGates = correctedLogic.filter { it.firstInputName.endsWith(remainingFaultyCarryAdderIndex.toString()) && it.secondInputName.endsWith(remainingFaultyCarryAdderIndex.toString()) }

    println((firstRuleViolations + secondRuleViolations + faultyCarryGates).sortedBy { it.outputVariableName }.joinToString(",") { it.outputVariableName })
}

fun evaluate(logic: List<Gate>, variables: MutableMap<String, Int>): String {
    var allEvaluated = false

    while(!allEvaluated) {
        allEvaluated = true
        logic.forEach {
            if (it.canEvaluate(variables)) {
                it.evaluate(variables)
            } else {
                allEvaluated = false
            }
        }
    }

    val binaryResult = variables.filter { it.key.startsWith("z") }.toList().sortedBy { it.first }.reversed().map { it.second }.joinToString("")
    return binaryResult
}

fun getOriginatingAdderFromFaultyCarry(faultyCarryName: String, gates: List<Gate>): Int {
    val usages = gates.filter { it.firstInputName == faultyCarryName || it.secondInputName == faultyCarryName }
    val resultingZ = usages.find { it.outputVariableName.startsWith("z") }

    if (resultingZ != null) {
        return resultingZ.outputVariableName.drop(1).toInt() - 1 // take the previous adder as the carry carries a value to the next adder already
    }

    // continue lookup
    return getOriginatingAdderFromFaultyCarry(usages.first().outputVariableName, gates)
}
