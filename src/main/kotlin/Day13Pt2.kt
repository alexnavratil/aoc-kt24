import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.LUDecomposition
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day13/input.txt").toURI()

    val buttonAExpr = Regex("""Button A: X\+(?<x>\d*), Y\+(?<y>\d*)""")
    val buttonBExpr = Regex("""Button B: X\+(?<x>\d*), Y\+(?<y>\d*)""")
    val priceExpr = Regex("""Prize: X=(?<x>\d*), Y=(?<y>\d*)""")

    val prizeOffset = 10000000000000

    val games = Files.readAllLines(Paths.get(inputPath))
        .withIndex()
        .groupBy { floor(it.index / 4.0) }
        .mapValues { entry ->
            val buttonAStr = entry.value.first().value
            val buttonBStr = entry.value[1].value
            val prizeStr = entry.value[2].value

            val buttonAMatch = buttonAExpr.find(buttonAStr)!!
            val buttonBMatch = buttonBExpr.find(buttonBStr)!!
            val prizeMatch = priceExpr.find(prizeStr)!!

            Game(
                buttonAX = buttonAMatch.groups["x"]!!.value.toLong(),
                buttonAY = buttonAMatch.groups["y"]!!.value.toLong(),
                buttonBX = buttonBMatch.groups["x"]!!.value.toLong(),
                buttonBY = buttonBMatch.groups["y"]!!.value.toLong(),
                prizeX = prizeMatch.groups["x"]!!.value.toLong() + prizeOffset,
                prizeY = prizeMatch.groups["y"]!!.value.toLong() + prizeOffset
            )
        }.values

    val solutions = mutableMapOf<Game, Set<Pair<Long, Long>>>()

    games.forEach { game ->
        val coefficients = arrayOf(
            doubleArrayOf(game.buttonAX.toDouble(), game.buttonBX.toDouble()),
            doubleArrayOf(game.buttonAY.toDouble(), game.buttonBY.toDouble())
        )
        val matrix = Array2DRowRealMatrix(coefficients)

        // Konstante Terme
        var constants = doubleArrayOf(game.prizeX.toDouble(), game.prizeY.toDouble())
        val constantsVector = ArrayRealVector(constants)

        val luDecomposition = LUDecomposition(matrix)
        val det = luDecomposition.determinant

        if (abs(det) > 1e-6) {
            // Lösung des Gleichungssystems
            val solver = LUDecomposition(matrix).solver
            val solution = solver.solve(constantsVector)

            // Ausgabe der Lösungen
            val a = solution.getEntry(0)
            val b = solution.getEntry(1)

            val aRounded = round(a).toLong()
            val bRounded = round(b).toLong()

            if (game.hasWon(aRounded, bRounded)) {
                solutions[game] = setOf(Pair(aRounded, bRounded))
            }
        }
    }

    val prize = solutions.filter { it.value.isNotEmpty() }
        .mapValues { it.value.minBy { solution -> it.key.calculatePrize(solution.first, solution.second) } }
        .map { it.key.calculatePrize(it.value.first, it.value.second) }.sum()

    println(prize)
}