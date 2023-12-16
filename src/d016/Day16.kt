package d016

import println
import readInput
import kotlin.experimental.and
import kotlin.experimental.or

fun parse(input: List<String>): Array<CharArray> {
    return input.map { it.toCharArray() }.toTypedArray()
}


enum class Dir {N, E, S, W}


fun solve(input: Array<CharArray>, row: Int, col:Int, dir: Dir): Int {
    val visited = Array(input.size) { ByteArray(input[0].size){0} }
    val rIdx = input.size
    val cIdx = input[0].size
    fun inBounds(row: Int, col: Int) = row in 0..<rIdx && col in 0..< cIdx

    fun Dir.toByte() = when(this) {
        Dir.N -> 1 shl 0
        Dir.E -> 1 shl 1
        Dir.S -> 1 shl 2
        Dir.W -> 1 shl 3
    }.toByte()


    fun isVisited(row: Int, col: Int, from:Dir): Boolean {
        return (visited[row][col] and from.toByte()) != 0.toByte()
    }

    fun visit(row: Int, col: Int, from:Dir) {
        visited[row][col] = visited[row][col] or from.toByte()
    }


    fun next(row: Int, col: Int, from: Dir): List<Triple<Int, Int, Dir>> {
        fun np(d: Dir): Triple<Int, Int, Dir> {
            return when(d) {
                Dir.N -> Triple(row + 1, col, Dir.N)
                Dir.E -> Triple(row, col - 1, Dir.E)
                Dir.S -> Triple(row - 1, col, Dir.S)
                Dir.W -> Triple(row, col + 1, Dir.W)
            }
        }

        return when(val current = input[row][col]) {
            '|' -> {
                if(from == Dir.W || from == Dir.E) {
                    listOf(Triple(row - 1, col, Dir.S), Triple(row + 1, col, Dir.N))
                } else {
                    listOf(np(from))
                }
            }

            '-' -> {
                if(from == Dir.N || from == Dir.S) {
                    listOf(Triple(row, col - 1, Dir.E), Triple(row, col + 1, Dir.W))
                } else {
                    listOf(np(from))
                }
            }

            '\\' -> {
                val nextDir = when(from) {
                    Dir.N -> Dir.W
                    Dir.E -> Dir.S
                    Dir.S -> Dir.E
                    Dir.W -> Dir.N
                }
                listOf(np(nextDir))
            }
            '/' -> {
                val nextDir = when(from) {
                    Dir.N -> Dir.E
                    Dir.E -> Dir.N
                    Dir.S -> Dir.W
                    Dir.W -> Dir.S
                }
                listOf(np(nextDir))
            }
            '.' -> {
                listOf(np(from))
            }
            else -> {
                error("Unknown char $current at [$row, $col]")
            }
        }
    }

    fun dfs(row: Int, col: Int, from: Dir) {
        if(inBounds(row, col).not() || isVisited(row, col, from)) {
            return
        }
        visit(row, col, from)
        val next = next(row, col, from)
        next.forEach { (r, c, d) ->
            dfs(r, c, d)
        }
    }

    dfs(row, col, dir)

    return visited.sumOf { it.count { it != 0.toByte() } }
}

fun part1(input: Array<CharArray>) : Any {
    return solve(input, 0, 0, Dir.W)
}

fun part2(input: Array<CharArray>) : Any {
    val forRows = input.indices.flatMap { idx ->
        listOf(
            solve(input, idx, 0, Dir.W),
            solve(input, idx, input[0].size - 1, Dir.E)
        )
    }
    val forCols = input[0].indices.flatMap { idx ->
        listOf(
            solve(input, 0, idx, Dir.N),
            solve(input, input.size - 1, idx, Dir.S)
        )
    }
    return (forRows + forCols).max()
}

fun main() {
    val testInput = parse(readInput("Day16_test"))
    val input = parse(readInput("Day16"))
    part1(testInput).println()
    part1(input).println()
    part2(testInput).println()
    part2(input).println()
}