import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

const val width = 101
const val height = 103
const val maxSeconds = 100

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day14/input.txt").toURI()

    val robotExp = Regex("""p=(?<px>-?\d*),(?<py>-?\d*) v=(?<vx>-?\d*),(?<vy>-?\d*)""")

    val robots = Files.readAllLines(Paths.get(inputPath))
        .map {
            val match = robotExp.find(it)!!
            val startX = match.groups["px"]!!.value.toLong()
            val startY = match.groups["py"]!!.value.toLong()
            val velocityX = match.groups["vx"]!!.value.toLong()
            val velocityY = match.groups["vy"]!!.value.toLong()

            Robot(startX, startY, velocityX, velocityY)
        }

    (0 until maxSeconds).forEach {
        robots.forEach { it.move() }
    }

    val topY = 0 until floor(height / 2.0).toLong()
    val bottomY = ceil(height / 2.0).toLong() until height

    val leftX = 0 until floor(width / 2.0).toLong()
    val rightX = ceil(width / 2.0).toLong() until width

    val topLeftQuad = robots.filter { it.x in leftX && it.y in topY }
    val bottomLeftQuad = robots.filter { it.x in leftX && it.y in bottomY }
    val topRightQuad = robots.filter { it.x in rightX && it.y in topY }
    val bottomRightQuad = robots.filter { it.x in rightX && it.y in bottomY }

    val safetyFactor = topLeftQuad.count() * bottomLeftQuad.count() * topRightQuad.count() * bottomRightQuad.count()
    println(safetyFactor)
}

fun Robot.move() {
    this.x = (x + velocityX) % width
    this.y = (y + velocityY) % height

    this.x = (x + (width * ceil(abs(x) / width.toDouble())).toLong()) % width
    this.y = (y + (height * ceil(abs(y) / height.toDouble())).toLong()) % height
}

data class Robot(val startX: Long, val startY: Long, val velocityX: Long, val velocityY: Long) {
    var x = startX
    var y = startY
}