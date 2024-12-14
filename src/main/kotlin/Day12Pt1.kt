import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

typealias Region = Set<GardenPlot>

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day12/input.txt").toURI()

    val board = Files.readAllLines(Paths.get(inputPath))
        .mapIndexed { rowIndex, line ->
            line.toCharArray().mapIndexed { colIndex, char -> GardenPlot(rowIndex, colIndex, char) }
        }

    board.linkNeighbors()

    val regions = board.findRegions()

    val fenceCost = regions.sumOf { it.getArea() * it.getPerimeter() }

    println(fenceCost)
}

fun Region.getArea() = count()

fun Region.getPerimeter() = sumOf { 4 - it.neighbors.size }

fun List<List<GardenPlot>>.findRegions(): List<Region> {
    val regions = mutableListOf<Region>()

    this.forEach { plots ->
        plots.forEach { plot ->
            if (regions.none { it.contains(plot) }) {
                regions += plot.getRegion()
            }
        }
    }

    return regions
}

fun List<List<GardenPlot>>.linkNeighbors() {
    this.forEach { plots ->
        plots.forEach { plot ->
            plot.linkWithNeighbors(this)
        }
    }
}

fun GardenPlot.getRegion(region: MutableSet<GardenPlot> = mutableSetOf<GardenPlot>()): Region {
    if (region.contains(this)) {
        return region
    }

    region += this

    this.neighbors.forEach { neighbor -> neighbor.getRegion(region)}

    return region
}

fun GardenPlot.linkWithNeighbors(board: List<List<GardenPlot>>) {
    var rowIdx = this.rowIndex
    var colIdx = this.colIndex

    val topRowIdx = rowIdx + Direction.Up.getRowModifier()
    val topColIdx = colIdx + Direction.Up.getColModifier()
    val topNeighbor = board.getOrNull(topRowIdx)?.getOrNull(topColIdx)

    if (topNeighbor?.type == this.type) {
        topNeighbor.bottomNeighbor = this
        this.topNeighbor = topNeighbor
    }

    val rightRowIdx = rowIdx + Direction.Right.getRowModifier()
    val rightColIdx = colIdx + Direction.Right.getColModifier()
    val rightNeighbor = board.getOrNull(rightRowIdx)?.getOrNull(rightColIdx)

    if (rightNeighbor?.type == this.type) {
        rightNeighbor.leftNeighbor = this
        this.rightNeighbor = rightNeighbor
    }

    val bottomRowIdx = rowIdx + Direction.Down.getRowModifier()
    val bottomColIdx = colIdx + Direction.Down.getColModifier()
    val bottomNeighbor = board.getOrNull(bottomRowIdx)?.getOrNull(bottomColIdx)

    if (bottomNeighbor?.type == this.type) {
        bottomNeighbor.topNeighbor = this
        this.bottomNeighbor = bottomNeighbor
    }

    val leftRowIdx = rowIdx + Direction.Left.getRowModifier()
    val leftColIdx = colIdx + Direction.Left.getColModifier()
    val leftNeighbor = board.getOrNull(leftRowIdx)?.getOrNull(leftColIdx)

    if (leftNeighbor?.type == this.type) {
        leftNeighbor.rightNeighbor = this
        this.leftNeighbor = leftNeighbor
    }
}

data class GardenPlot(val rowIndex: Int, val colIndex: Int, val type: Char) {
    var leftNeighbor: GardenPlot? = null
    var rightNeighbor: GardenPlot? = null
    var topNeighbor: GardenPlot? = null
    var bottomNeighbor: GardenPlot? = null

    val neighbors: Set<GardenPlot>
        get() = setOfNotNull(leftNeighbor, rightNeighbor, topNeighbor, bottomNeighbor)
}