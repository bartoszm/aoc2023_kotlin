package d013

import println
import readInput

fun parse(input: List<String>): List<List<String>> {
    var buffer = mutableListOf<String>()
    return sequence {
        for(line in input) {
            if(line.isEmpty()) {
                yield(buffer.toList())
                buffer = mutableListOf()
            } else {
                buffer.add(line)
            }
        }
        yield(buffer.toList())
    }.filter { it.isNotEmpty() }.toList()

}

fun List<String>.reflectHorizontal(idx: Int, diffChars: Int = 0,  diffSeg: Int = 0): Boolean {
    fun compare(a: String, b: String) = a.zip(b).count { it.first != it.second }
    val a = this.subList(0, idx).reversed()
    val b = this.subList(idx, this.size)
    val allPairs = a.zip(b)

    val counts = allPairs.map { (f, s) -> compare(f, s) }
        .groupBy { it }
        .entries.associate { it.key to it.value.size }

    return isValidResult(counts.toMutableMap(), allPairs.size, diffChars, diffSeg)
}

fun List<String>.reflectVertical(idx: Int , diffChars: Int = 0,  diffSeg: Int = 0): Boolean {
    val counts = this.map { row ->
        val a = row.substring(0, idx).reversed()
        val b = row.substring(idx, row.length)
        a.zip(b).count { (f,s) -> f != s }
    }.groupBy{it}.entries.associate { it.key to it.value.size }

    return isValidResult(counts.toMutableMap(), this.size, diffChars, diffSeg)
}

fun isValidResult(counts: MutableMap<Int, Int>, total: Int, diffChars: Int, diffSeg: Int): Boolean {
    if ((counts[0] ?: 0) + diffSeg != total) {
        return false
    }

    counts[0] = (counts[0] ?: 0) + diffSeg - total
    return (counts[diffChars] ?: -1) == diffSeg
}

fun part1(input: List<List<String>>): Any {

    fun computeReflections(shape: List<String>): Int {
        val h = shape.indices.drop(1).firstOrNull { idx ->
            shape.reflectHorizontal(idx)
        } ?: 0
        val v = shape[0].indices.drop(1).firstOrNull { idx ->
            shape.reflectVertical(idx)
        } ?: 0
        return h * 100 + v
    }

    return input.sumOf { computeReflections(it) }

}

fun part2(input: List<List<String>>): Any {
    fun computeReflections(shape: List<String>): Int {
        val h = shape.indices.drop(1).firstOrNull { idx ->
            shape.reflectHorizontal(idx, 1, 1)
        } ?: 0
        val v = shape[0].indices.drop(1).firstOrNull { idx ->
            shape.reflectVertical(idx, 1, 1)
        } ?: 0
        return h * 100 + v
    }

    return input.sumOf { computeReflections(it) }
}

fun main() {
    val testInput = parse(readInput("Day13_test"))
    val input = parse(readInput("Day13"))

    part1(testInput).println()
    part1(input).println()
    part2(testInput).println()
    part2(input).println()
}


