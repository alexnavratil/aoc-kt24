import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

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

    val output = StringBuilder()

    (0 until 11000).forEachIndexed { index, _ ->
        robots.forEach { it.move() }

        val maxRobotsPerRow = robots.groupBy { it.y }.mapValues { it.value.size }.entries.maxBy { it.value }?.value ?: 0
        val maxRobotsPerCol = robots.groupBy { it.x }.mapValues { it.value.size }.entries.maxBy { it.value }?.value ?: 0

        if (maxRobotsPerRow > 18 || maxRobotsPerCol > 18) {
            output.appendLine("Round ${index + 1}:")
            robots.printBoard(output)

            output.append("\n\n\n")
        }
    }

    val outputFile = File("output.txt")

    if (!outputFile.exists()) {
        outputFile.createNewFile()
    }

    outputFile.bufferedWriter().use { it.write(output.toString()) }

    println(outputFile.absolutePath)
}

fun List<Robot>.printBoard(stringBuilder: StringBuilder) {
    val board = MutableList(height) { MutableList(width) { '⠀' } }

    this.forEach { board[it.y.toInt()][it.x.toInt()] = '█' }

    (0 until width).forEach { stringBuilder.append('█') }
    stringBuilder.appendLine()

    board.forEach {
        stringBuilder.append('█')
        it.forEach { stringBuilder.append("$it") }
        stringBuilder.append('█')
        stringBuilder.appendLine()
    }

    (0 until width).forEach { stringBuilder.append('█') }
    stringBuilder.appendLine()
}