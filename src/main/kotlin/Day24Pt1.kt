import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.floor
import kotlin.mod

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day24/example.txt").toURI()

    val inputLines = Files.readAllLines(Paths.get(inputPath))

    // example: ntg XOR fgs -> mjb
    val gateExpr = Regex("""(?<firstVariableName>...) (?<gateType>.{2,3}) (?<secondVariableName>...) -> (?<outputVariableName>...)""")

    val initialVariables = inputLines.takeWhile { it.isNotEmpty() }.map { it.split(": ") }.map { Pair(it[0], it[1].toInt()) }
    val logic = inputLines.dropWhile { it.isNotEmpty() }.drop(1).map {
        val matchResult = gateExpr.find(it)!!

        Gate(matchResult.groups["firstVariableName"]!!.value, matchResult.groups["secondVariableName"]!!.value, matchResult.groups["gateType"]!!.value.toGateType(), matchResult.groups["outputVariableName"]!!.value)
    }

    val variables = initialVariables.associate { it.first to it.second }.toMutableMap()

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

    println(binaryResult.toLong(radix = 2))
}

data class Gate(val firstInputName: String, val secondInputName: String, val gateType: GateType, val outputVariableName: String) {
    fun evaluate(variables: MutableMap<String, Int>) {
        val result = when(gateType) {
            GateType.AND -> variables[firstInputName]!!.and(variables[secondInputName]!!)
            GateType.OR -> variables[firstInputName]!!.or(variables[secondInputName]!!)
            GateType.XOR -> variables[firstInputName]!!.xor(variables[secondInputName]!!)
        }

        variables[outputVariableName] = result
    }

    fun canEvaluate(variables: MutableMap<String, Int>): Boolean = variables.containsKey(firstInputName) && variables.containsKey(secondInputName)
}

enum class GateType {
    AND,
    OR,
    XOR
}

fun String.toGateType(): GateType = when (this) {
    "AND" -> GateType.AND
    "OR" -> GateType.OR
    "XOR" -> GateType.XOR
    else -> throw IllegalArgumentException("Unknown gate type $this")
}
