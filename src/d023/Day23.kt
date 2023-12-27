package d023

import d010.next
import println
import readInput

fun CharArray.pathIdx() = this.indexOfFirst { it == '.'  }


typealias Point = Pair<Int, Int>

operator fun Point.plus(other: Point) = (this.first + other.first) to (this.second + other.second)

val p1Mapper : (Char) -> Sequence<Point> = {  when (it) {
    '.' -> sequenceOf(
        -1 to 0,
        1 to 0,
        0 to -1,
        0 to 1)
    '>' -> sequenceOf(0 to 1)
    '<' -> sequenceOf(0 to-1)
    '^' -> sequenceOf(-1 to 0)
    'v' -> sequenceOf(1 to 0)
    '#' -> sequenceOf()
    else -> throw IllegalArgumentException("not allowed $it")
}}

val p2Mapper : (Char) -> Sequence<Point> = {  when (it) {
    '#' -> sequenceOf()
    else -> sequenceOf(
        -1 to 0,
        1 to 0,
        0 to -1,
        0 to 1)
}}

fun part1(input: Array<CharArray>): Int {
    val plane = Plane(input, p1Mapper)

    val start = 0 to input[0].pathIdx()
    val end = input.lastIndex to input.last().pathIdx()


    val visited = hashSetOf<Point>()
    val paths = mutableListOf<List<Point>>()

    fun Point.next() =  plane.neighbours(this)
        .filter { it !in visited }

    fun dfs(p: Point) {
        if(p == end) {
            paths.add(visited.toList())
        }
        visited.add(p)

        p.next().forEach {
            dfs(it)
        }
        visited.remove(p)
    }

    dfs(start)

    return paths.map { it.size }.max()!!
}

data class Plane(val data: Array<CharArray>, private val mapper: (Char) -> Sequence<Point>) {
    private val cols = data[0].size
    private val rows = data.size
    val start = 0 to data[0].pathIdx()
    val end = data.lastIndex to data.last().pathIdx()

    fun neighbours(p: Point): Sequence<Point> {
        return mapper(data[p.first][p.second])
            .map { p + it }
            .filter { it.first in 0 until rows }
            .filter { it.second in 0 until cols }
            .filter { data[it.first][it.second] != '#'  }
    }
}

fun decisionPoints(plane: Plane): List<Point> {
    fun Point.next() =  plane.neighbours(this)

    return plane.data.flatMapIndexed { i, row ->
        row.indices.map { j -> i to j}
    }
        .map { p -> p to p.next().count() }
        .filter { it.second > 2 }
        .map { it.first }
}

fun toGraphE(plane: Plane): Map<Point, List<Pair<Point, Int>>> {
    val start = 0 to plane.data[0].pathIdx()
    val end = plane.data.lastIndex to plane.data.last().pathIdx()
    val vertexes = decisionPoints(plane) + start + end
    fun Point.next() = plane.neighbours(this)

    fun traverse(s: Point, blocked: Point): Pair<Point, Int>? {
        val visited = hashSetOf(s, blocked)
        var next = s.next().filter { it !in visited }.toList()
        var prev = s
        while(next.size == 1) {
            visited.add(next.first())
            prev = next.first()
            next = prev.next().filter { it !in visited }.toList()
        }
        return if(next.isNotEmpty() || prev == end) prev to (visited.size - 1) else null
    }

    return vertexes.associateWith { p -> p.next().map { traverse(it, p) }.filterNotNull().toList() }
}

fun part2(input: Array<CharArray>): Int {
    val plane = Plane(input, p2Mapper)
    val graph = toGraphE(plane)
    val visited = hashSetOf<Point>()
    val paths = mutableListOf<Int>()

    fun dfs(p: Point, length: Int) {
        if(p in visited) return

        if(p == plane.end) {
            paths.add(length)
            return
        }

        visited.add(p)
        graph[p]?.forEach {
            dfs(it.first, length + it.second)
        }
        visited.remove(p)
    }

    dfs(plane.start, 0)
    return paths.maxOrNull()!!
}

fun parse(input: List<String>): Array<CharArray> {
    return input.map { it.toCharArray() }.toTypedArray()
}

fun main() {
    val testInput = parse(readInput("Day23_test"))
    val input = parse(readInput("Day23"))
    part1(testInput).println()
    part1(input).println()

    part2(testInput).println()
    part2(input).println()
}