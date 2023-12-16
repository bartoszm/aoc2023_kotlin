package d010

import println
import readInput
import toPair

fun parse(input: List<String>): List<List<Char>> = input.map { it.toCharArray().toList() }
typealias Point = Pair<Int, Int>

enum class Dir {N, E, S, W}

val dirMap = mapOf(
    '|' to {from: Dir -> when(from) {
        Dir.N, Dir.S -> from
        else -> null
    }},
    '-' to {from: Dir -> when(from) {
        Dir.E, Dir.W -> from
        else -> null
    }},
    'L' to {from: Dir -> when(from) {
        Dir.S -> Dir.E
        Dir.W -> Dir.N
        else -> null
    }},
    'J' to {from: Dir -> when(from) {
        Dir.S -> Dir.W
        Dir.E -> Dir.N
        else -> null
    }},
    '7' to {from: Dir -> when(from) {
        Dir.N -> Dir.W
        Dir.E -> Dir.S
        else -> null
    }},
    'F' to {from: Dir -> when(from) {
        Dir.N -> Dir.E
        Dir.W -> Dir.S
        else -> null
    }}
).withDefault { _ -> null }

fun Point.next(dir: Dir): Point {
    return when(dir) {
        Dir.N -> this.first - 1 to this.second
        Dir.S -> this.first + 1 to this.second
        Dir.W -> this.first to this.second - 1
        Dir.E -> this.first to this.second + 1
    }
}

class Board(private val positions: List<String>) {
    val cols = positions[0].length

    val start: Point by lazy {
        positions.mapIndexed{ idx, it ->
           idx to it.indexOf('S')
        }.first { it.second != -1  }
    }
    operator fun get(p:Point) = if(p.first in positions.indices && p.second in 0..<cols) {
        positions[p.first][p.second]
    } else { null }
}

fun traverse(board: Board, dir: Dir): List<Point>? {
    var pointing = dir
    val visited = mutableListOf(board.start)
    var current : Point = board.start.next(pointing)

    while(current != board.start) {
        visited.add(current)
        //turn
        val type = board[current]
        pointing = type?.let { dirMap[it]?.invoke(pointing) } ?: return null
        current = current.next(pointing)
    }
    return visited
}

fun startPipe(b: Board): Char {
    val d = Dir.entries
        .map { it to b.start.next(it) }
        .filter { dirMap[b[it.second]]?.invoke(it.first) != null}
        .map { it.first }
        .toPair()
    return when(d) {
        Dir.N to Dir.E ->  'L'
        Dir.N to Dir.S ->  '|'
        Dir.N to Dir.W ->  'J'
        Dir.E to Dir.S ->  'F'
        Dir.E to Dir.W ->  '-'
        Dir.S to Dir.W ->  '7'
        else -> error("Invalid start pipe")
    }
}

fun part1(input: List<String>): Any {
    val board = Board(input)
    return Dir.entries.firstNotNullOf { traverse(board, it) }.let { (it.size + 1) / 2 }
}

fun part2(input: List<String>) : Any {
    val board = Board(input)
    val charS = startPipe(board)
    val solution = Dir.entries.firstNotNullOf { traverse(board, it) }

    val pointsInRow = solution.filter { board[it] != '-' }.groupBy{it.first}.mapValues { it.value.sortedBy { it.second }}

    fun isInside(p: Point): Boolean {
        val candidates = pointsInRow.getOrDefault(p.first, listOf())
            .filter { it.second >  p.second }
            .sortedBy { it.second }

        val dec = candidates
            .map { if(it == board.start) charS else board[it] }
            .zipWithNext().count { "${it.first}${it.second}" in setOf("L7", "FJ") }
        return (candidates.count() - dec) % 2 == 1
    }

    return pointsInRow.values.sumOf { row ->
        (row[0].second + 1 until board.cols)
            .map { row[0].first to it }
            .filter { !solution.contains(it) }
            .count { p -> isInside(p) }
    }
}

fun main() {
    val testInput = readInput("Day10_test")
    val input = readInput("Day10")

    part1(testInput).println()
    part1(input).println()

    part2(testInput).println()
    part2(input).println()
}