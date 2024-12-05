import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day3/input.txt").toURI()

    val content = Files.readString(Paths.get(inputPath))

    var expr = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")

    var multSum = expr.findAll(content).map { it.groups[1]?.value?.toInt()?.times(it.groups[2]?.value?.toInt() ?: 0) ?: 0}.sum()

    println(multSum)
}
