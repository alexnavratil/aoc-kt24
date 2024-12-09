import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day9/input.txt").toURI()

    val diskMapCompressed = Files.readString(Paths.get(inputPath)).toCharArray().map { it.toString().toInt() }

    val diskMapExpanded = diskMapCompressed.flatMapIndexed { index, num ->
        if (index % 2 == 0) {
            // file block
            List(num) { (index / 2).toInt() }
        } else {
            // free space block
            List(num) { null }
        }
    }

    val diskMapDefragmented = mutableListOf<Int>()
    var lastDiskMapBlockIdx = diskMapExpanded.indexOfLast { it != null }

    var i = 0

    while (i <= lastDiskMapBlockIdx) {
        if (diskMapExpanded[i] == null) {
            val endBlockIdx = diskMapExpanded.subList(0, lastDiskMapBlockIdx + 1).indexOfLast { it != null }
            diskMapDefragmented.add(diskMapExpanded[endBlockIdx]!!)
            lastDiskMapBlockIdx = endBlockIdx - 1
        } else {
            diskMapDefragmented.add(diskMapExpanded[i]!!)
        }

        i++
    }

    val checksum = diskMapDefragmented.mapIndexed { index, id -> index * id.toLong() }.sum()

    println(checksum)
}