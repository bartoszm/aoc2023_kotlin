package d012

import println
import readInput

data class Record(val assignments: String, val groups: List<Int>) {
    val empty = groups.isEmpty()
}

fun parse(input: List<String>): List<Record> {
    return input.map {
        val (assignments, groups) = it.split(" ").map { it.trim() }
        Record(assignments, groups.split(",").map { it.toInt() })
    }
}

fun count(r: Record, cache: MutableMap<Record, Long>): Long {
    if(r in cache) return cache[r]!!
    if(r.assignments == "") return if(r.empty) 1 else 0
    if(r.empty) return if(r.assignments.contains("#")) 0 else 1
    var result = 0L
    if(r.assignments[0] in setOf('.', '?')) {
        result += count(r.copy(assignments = r.assignments.drop(1)), cache)
    }

    fun canBeGroup(size: Int, assignments: String): Boolean {
        if(size > assignments.length) return false
        if(assignments.take(size).contains('.')) return false
        if(assignments.isEmpty()) return false
        return size == assignments.length || assignments[size] != '#'
    }

    if(r.assignments[0] in setOf('#', '?')) {
        if(canBeGroup(r.groups[0], r.assignments)) {
            result += count(r.copy(assignments = r.assignments.drop(r.groups[0] + 1), groups = r.groups.drop(1)), cache)
        }
    }
    cache[r] = result
    return result
}

fun part1(input: List<Record>): Any {
    return input.sumOf { count(it, mutableMapOf()) }
}


fun part2(input: List<Record>): Any {
    return input.sumOf { count(it.repeat(5), mutableMapOf()) }
}

fun Record.repeat(times: Int = 5 ): Record {
    val nA = List(times) { this.assignments }.joinToString("?")
    val nG = List(times) { this.groups }.flatten()
    return Record(nA, nG)
}

fun main() {
    val testInput = parse(readInput("Day12_test"))
    val input = parse(readInput("Day12"))

    part1(testInput).println()
    part1(input).println()

    part2(testInput).println()
    part2(input).println()
}



