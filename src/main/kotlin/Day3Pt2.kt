import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day3/input.txt").toURI()

    val content = Files.readString(Paths.get(inputPath))
        .replace("\n", "")

    var beginningEnabledMulExpr = Regex("""(?:(?!don't\(\))(?!mul\(\d{1,3},\d{1,3}\)).)*mul\((?<x>\d{1,3}),(?<y>\d{1,3})\)|don't\(\)(?<stop>.*)""")

    val beginningMulRes = beginningEnabledMulExpr.findAll(content)
        .takeWhile { result -> result.groups["stop"] == null }
        .sumOf { result -> result.groups["x"]!!.value.toInt() * result.groups["y"]!!.value.toInt() }

    var doStrExpr = Regex("""do\(\)(?:(?!don't\(\)).)*""")

    val mulExpr = Regex("""mul\((?<x>\d{1,3}),(?<y>\d{1,3})\)""")
    val doMulRes = doStrExpr.findAll(content)
        .flatMap { result -> mulExpr.findAll(result.groups[0]!!.value) }
        .sumOf { result -> result.groups["x"]!!.value.toInt() * result.groups["y"]!!.value.toInt() }

    val finalResult = beginningMulRes + doMulRes
    println(finalResult)
}
