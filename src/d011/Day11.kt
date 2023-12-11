package d011

import println
import readInput
import kotlin.math.absoluteValue


typealias Point = Pair<Long, Long>

data class Universe(val dim: Pair<Long, Long>, val galaxies: List<Point>)

fun parse(input: List<String>): Universe {
    val galaxies = input.flatMapIndexed{ x, line ->
        line.mapIndexed { y, c ->
            if(c == '#') Point(x.toLong(),y.toLong()) else null
        }.filterNotNull()
    }
    return Universe(input.size.toLong() to input[0].length.toLong(), galaxies)
}

operator fun Point.plus(p: Point) = Point(this.first + p.first, this.second + p.second)

fun expand(universe: Universe, factor: Long = 1): Universe {
    val missingX = (0..<universe.dim.first).toSortedSet() subtract universe.galaxies.map { it.first }.toSet()
    val missingY = (0..<universe.dim.second).toSortedSet() subtract universe.galaxies.map { it.second }.toSet()
    val nG = universe.galaxies.map { g ->
        val offsetX = missingX.count { it < g.first } * factor
        val offsetY = missingY.count { it < g.second } * factor
        g + Point(offsetX, offsetY)
    }
    val nDim = universe.dim.first + missingX.count() to universe.dim.second + missingY.count()

    return Universe(nDim, nG)
}

fun sumDistances(universe: Universe): Long {
    return sequence {
        for (i in 0 until universe.galaxies.size) {
            for (j in i + 1 until universe.galaxies.size) {
                yield(Pair(universe.galaxies[i], universe.galaxies[j]))
            }
        }
    }
        .map { taxi(it.first, it.second) }
        .sum()
}

fun part1(input: List<String>): Any {
    val universe = expand(parse(input))
    return sumDistances(universe)
}

fun part2(input: List<String>): Any {
    val universe = expand(parse(input), 1_000_000 - 1)
    return sumDistances(universe)
}

fun taxi(a: Point, b: Point) = (a.first - b.first).absoluteValue + (a.second - b.second).absoluteValue

fun main() {
    val testInput = readInput("Day11_test")
    val input = readInput("Day11")
    part1(testInput).println()
    part1(input).println()
    part2(testInput).println()
    part2(input).println()

}