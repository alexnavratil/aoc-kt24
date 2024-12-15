import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.floor
import kotlin.math.min

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day13/input.txt").toURI()

    val buttonAExpr = Regex("""Button A: X\+(?<x>\d*), Y\+(?<y>\d*)""")
    val buttonBExpr = Regex("""Button B: X\+(?<x>\d*), Y\+(?<y>\d*)""")
    val priceExpr = Regex("""Prize: X=(?<x>\d*), Y=(?<y>\d*)""")

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
                prizeX = prizeMatch.groups["x"]!!.value.toLong(),
                prizeY = prizeMatch.groups["y"]!!.value.toLong()
            )
        }.values

    val solutions = mutableMapOf<Game, Set<Pair<Long, Long>>>()

    games.forEach { game ->
        solutions[game] = (0..game.buttonAMaxMultiplier).flatMap { a ->
            (0..game.buttonBMaxMultiplier).filter { b ->
                game.hasWon(
                    a,
                    b
                )
            }.map { b -> Pair(a, b) }
        }.toSet()
    }

    val prize = solutions.filter { it.value.isNotEmpty() }
        .mapValues { it.value.minBy { solution -> it.key.calculatePrize(solution.first, solution.second) } }
        .map { it.key.calculatePrize(it.value.first, it.value.second) }.sum()

    println(prize)
}

fun Game.hasWon(a: Long, b: Long): Boolean {
    return (buttonAX * a + buttonBX * b) == prizeX && (buttonAY * a + buttonBY * b) == prizeY
}

fun Game.calculatePrize(a: Long, b: Long) = a * 3 + b * 1

data class Game(
    val buttonAX: Long,
    val buttonAY: Long,
    val buttonBX: Long,
    val buttonBY: Long,
    val prizeX: Long,
    val prizeY: Long
) {
    val buttonAMaxMultiplier: Long
        get() = min(floor(prizeX / buttonAX.toDouble()), floor(prizeY / buttonAY.toDouble())).toLong()

    val buttonBMaxMultiplier: Long
        get() = min(floor(prizeX / buttonBX.toDouble()), floor(prizeY / buttonBY.toDouble())).toLong()
}
