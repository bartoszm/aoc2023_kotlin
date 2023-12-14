package d014

import println
import readInput

typealias Point = Pair<Int, Int>



data class Platform(val rowSize: Int, val colSize: Int, val rolls: Array<Array<Boolean>>, val rocks: Array<Array<Boolean>>) {
    private fun Array<Array<Boolean>>.col(idx: Int): List<Point> = this.mapIndexedNotNull { row, col -> if(col[idx]) row to idx else null }.reversed()
    private fun maxTop(point: Point): Int {
        return rocks.col(point.second).firstOrNull { it.first < point.first }?.let {
            return it.first + 1
        } ?: 0
    }

    private fun countRocks(rows: IntRange, col:Int): Int {
        if(rows.last < 0) { return 0 }
        return rolls.col(col).count{it.first in rows }
    }

    private fun calculatePos(point: Point) : Point {
        val max = maxTop(point)
        val offset = countRocks(max..<point.first, point.second)
        return max + offset to point.second
    }

    private fun List<Point>.to2DArray(): Array<Array<Boolean>> {
        val array = Array(rowSize) { Array(colSize) { false } }
        this.forEach { array[it.first][it.second] = true }
        return array
    }

    private fun Array<Array<Boolean>>.rotate(): Array<Array<Boolean>> {
        val array = Array(colSize) { Array(rowSize) { false } }
        this.forEachIndexed { row, cols ->
            cols.forEachIndexed { col, v ->
                array[col][rowSize - 1 - row] = v
            }
        }
        return array
    }

    private fun from(rolls: Array<Array<Boolean>>, rocks: Array<Array<Boolean>>): Platform {
        return Platform(rowSize, colSize, rolls, rocks)
    }

    fun tilted() = from(rolls.flatMapIndexed { rI, row -> row
        .mapIndexedNotNull { cI, v -> if(v) rI to cI else null } }
        .map { calculatePos(it) }.to2DArray(), rocks)

    fun rotated() = from(rolls.rotate(), rocks.rotate())
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Platform

        if (rowSize != other.rowSize) return false
        if (colSize != other.colSize) return false
        if (!rolls.contentDeepEquals(other.rolls)) return false
        if (!rocks.contentDeepEquals(other.rocks)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rowSize
        result = 31 * result + colSize
        result = 31 * result + rolls.contentDeepHashCode()
        result = 31 * result + rocks.contentDeepHashCode()
        return result
    }

    override fun toString(): String {
        return (0 ..<rowSize).map {r ->
            (0..<colSize).joinToString("") { c ->
                when {
                    rolls[r][c] -> "O"
                    rocks[r][c] -> "#"
                    else -> "."
                }
            }
        }.joinToString("\n")
    }
}

fun parse(input: List<String>): Platform {
    fun to2dArray(input: List<String>, char: Char): Array<Array<Boolean>> = input
        .map { line -> line.toCharArray().map { it == char }.toTypedArray() }
        .toTypedArray()

    val rolls = to2dArray(input, 'O')
    val rocks = to2dArray(input, '#')
    return Platform(input.size, input[0].length, rolls, rocks)
}

fun weight(platform: Platform): Int {
    val rowCounts = platform.rolls.map { r-> r.count { it } }
    return rowCounts.mapIndexed { idx, v -> (platform.rowSize - idx) * v }.sum()
}

fun part1(input: List<String>): Any {
    val platform = parse(input)
    val newPos = platform.tilted()
    return weight(newPos)
}

fun part2(input: List<String>): Any {

    fun Platform.cycle() =(1..4).fold(this){ acc, _ -> acc.tilted().rotated() }
    val platform = parse(input)

    val cache = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()
    val resultCache = mutableListOf<Int>()
    val configCache = mutableListOf<Platform>()

    var currentPlatform = platform
    val finalCycle = 1_000_000_000

    for(round in 0 ..< finalCycle) {
        currentPlatform = currentPlatform.cycle()
        val w = weight(currentPlatform)
        val md5 = currentPlatform.rolls.contentDeepHashCode()
        val list = cache.getOrPut(w) { mutableListOf() }
        val first = list.firstOrNull { it.first == md5 }?.second

        if(first != null) {
            val cycle = round - first
            val idx = (finalCycle - 1 - first) % cycle + first
            return resultCache[idx]

        } else {
            list.add(md5 to round)
            resultCache.add(w)
            configCache.add(currentPlatform)
        }
    }

    error("No solution found")
}

fun main() {
    val test = readInput("Day14_test")
    val input = readInput("Day14")
    part1(test).println()
    part1(input).println()
    part2(test).println()
    part2(input).println()
}