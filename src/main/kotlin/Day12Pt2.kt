import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputPath = ClassPathMarker::class.java.getResource("files/day12/input.txt").toURI()

    val board = Files.readAllLines(Paths.get(inputPath))
        .mapIndexed { rowIndex, line ->
            line.toCharArray().mapIndexed { colIndex, char -> GardenPlot(rowIndex, colIndex, char) }
        }

    board.linkNeighbors()

    val regions = board.findRegions()

    val regionSides = board.getSides(regions)

    val fenceCost = regions.sumOf { it.getArea() * regionSides[it]!! }

    println(fenceCost)
}

fun List<List<GardenPlot>>.getSides(regions: List<Region>): Map<Region, Int> {
    val sideMap = mutableMapOf<GardenPlot, Int>()

    val outsidePlots = this.flatten().filter { it.neighbors.count() < 4 }

    outsidePlots.forEach { plot ->
        if (plot.isTopRightCorner()) {
            sideMap.increase(plot)

            plot.getTopRightNeighbor(this)?.let { sideMap.increase(it) }
        }

        if (plot.isBottomRightCorner()) {
            sideMap.increase(plot)

            plot.getBottomRightNeighbor(this)?.let { sideMap.increase(it) }
        }

        if (plot.isBottomLeftCorner()) {
            sideMap.increase(plot)

            plot.getBottomLeftNeighbor(this)?.let { sideMap.increase(it) }
        }

        if (plot.isTopLeftCorner()) {
            sideMap.increase(plot)

            plot.getTopLeftNeighbor(this)?.let { sideMap.increase(it) }
        }
    }

    return regions.associate { region -> region to region.sumOf { plot -> sideMap[plot] ?: 0 } }
}

fun MutableMap<GardenPlot, Int>.increase(plot: GardenPlot) {
    this[plot] = (this[plot] ?: 0) + 1
}

fun GardenPlot.isTopRightCorner() = this.topNeighbor?.type != this.type && this.rightNeighbor?.type != this.type
fun GardenPlot.isBottomRightCorner() = this.bottomNeighbor?.type != this.type && this.rightNeighbor?.type != this.type
fun GardenPlot.isBottomLeftCorner() = this.bottomNeighbor?.type != this.type && this.leftNeighbor?.type != this.type
fun GardenPlot.isTopLeftCorner() = this.topNeighbor?.type != this.type && this.leftNeighbor?.type != this.type

fun GardenPlot.getTopRightNeighbor(board: List<List<GardenPlot>>): GardenPlot? {
    var hasTopRightCornerNeighbor = this.getNeighbor(Direction.Up, board)?.type == this.getNeighbor(Direction.Right, board)?.type && this.getNeighbor(Direction.Right, board)?.topNeighbor != null && this.getNeighbor(Direction.Up, board)?.rightNeighbor != null

    if (hasTopRightCornerNeighbor) {
        return this.getNeighbor(Direction.Up, board)!!.rightNeighbor
    }

    return null
}

fun GardenPlot.getBottomRightNeighbor(board: List<List<GardenPlot>>): GardenPlot? {
    var hasBottomRightCornerNeighbor = this.getNeighbor(Direction.Down, board)?.type == this.getNeighbor(Direction.Right, board)?.type && this.getNeighbor(Direction.Right, board)?.bottomNeighbor != null && this.getNeighbor(Direction.Down, board)?.rightNeighbor != null

    if (hasBottomRightCornerNeighbor) {
        return this.getNeighbor(Direction.Down, board)!!.rightNeighbor
    }

    return null
}

fun GardenPlot.getBottomLeftNeighbor(board: List<List<GardenPlot>>): GardenPlot? {
    var hasBottomLeftCornerNeighbor = this.getNeighbor(Direction.Down, board)?.type == this.getNeighbor(Direction.Left, board)?.type && this.getNeighbor(Direction.Left, board)?.bottomNeighbor != null && this.getNeighbor(Direction.Down, board)?.leftNeighbor != null

    if (hasBottomLeftCornerNeighbor) {
        return this.getNeighbor(Direction.Down, board)!!.leftNeighbor
    }

    return null
}

fun GardenPlot.getTopLeftNeighbor(board: List<List<GardenPlot>>): GardenPlot? {
    var hasTopLeftCornerNeighbor = this.getNeighbor(Direction.Up, board)?.type == this.getNeighbor(Direction.Left, board)?.type && this.getNeighbor(Direction.Left, board)?.topNeighbor != null && this.getNeighbor(Direction.Up, board)?.leftNeighbor != null

    if (hasTopLeftCornerNeighbor) {
        return this.getNeighbor(Direction.Up, board)!!.leftNeighbor
    }

    return null
}

fun GardenPlot.getNeighbor(direction: Direction, board: List<List<GardenPlot>>): GardenPlot? = board.getOrNull(this.rowIndex + direction.getRowModifier())?.getOrNull(this.colIndex + direction.getColModifier())