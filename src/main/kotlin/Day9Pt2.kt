import java.nio.file.Files
import java.nio.file.Paths

typealias Block = Triple<BlockType, Int, Int?>

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day9/input.txt").toURI()

    val diskMapCompressed = Files.readString(Paths.get(inputPath)).toCharArray().map { it.toString().toInt() }

    val diskMapExpanded = diskMapCompressed.mapIndexed { index, num ->
        if (index % 2 == 0) {
            // file block
            Block(BlockType.File, num, (index / 2).toInt())
        } else {
            // free space block
            Block(BlockType.Free, num, null)
        }
    }

    val diskMapDefragmented = diskMapExpanded.toMutableList()

    var i = diskMapDefragmented.lastIndex

    while (i >= 0) {
        if (diskMapDefragmented[i].first == BlockType.File) {
            val firstFreeFittingBlockIdx = diskMapDefragmented.indexOfFirst { it.first == BlockType.Free && it.second >= diskMapDefragmented[i].second }

            if (firstFreeFittingBlockIdx != -1 && firstFreeFittingBlockIdx < i) {
                diskMapDefragmented[firstFreeFittingBlockIdx] = diskMapDefragmented[firstFreeFittingBlockIdx].copy(second = diskMapDefragmented[firstFreeFittingBlockIdx].second - diskMapDefragmented[i].second)
                diskMapDefragmented.add(firstFreeFittingBlockIdx, diskMapDefragmented[i])
                diskMapDefragmented.set(i + 1, Block(BlockType.Free, diskMapDefragmented[firstFreeFittingBlockIdx].second, null))
                i += 1
            }
        }

        i--
    }

    val defragmentedExpanded = diskMapDefragmented.filter { it.second > 0 }.flatMap { it.expand() }

    val checksum = defragmentedExpanded.mapIndexed { index, id -> index * (id?.toLong() ?: 0) }.sum()

    println(checksum)
}

enum class BlockType {
    File,
    Free
}

fun Block.expand(): List<Int?> {
    return if (first == BlockType.Free) {
        List(second) { null }
    } else {
        List(second) { third }
    }
}