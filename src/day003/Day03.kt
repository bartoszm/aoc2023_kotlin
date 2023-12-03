package day003

import println
import readInput
import kotlin.math.absoluteValue


sealed class Position(val row: Int, val range: IntRange)
class Number(val value: Int, row: Int, range: IntRange) : Position(row, range)
class Character(val value: String, row: Int, range: IntRange) : Position(row, range)

fun parse(line: String, rowId: Int): Sequence<Position> = sequence {
    var buffer = ""
    fun emitNumber(idx: Int): Number? {
        if(buffer.isNotEmpty()) {
            val result = Number(buffer.toInt(),  rowId, idx - buffer.length + 1 .. idx)
            buffer = ""
            return result
        }
        return null
    }
    line.forEachIndexed { idx, c ->
        if('.' == c) {
            emitNumber(idx - 1)?.let { yield(it) }
        } else if(! c.isDigit()) {
            emitNumber(idx - 1)?.let { yield(it) }
            yield(Character(c.toString(), rowId, idx - 1 .. idx + 1))
        } else {
            buffer += c
        }
    }
    emitNumber(line.length - 1)?.let { yield(it) }
}

fun parse(lines: List<String>): Sequence<Position> = lines.mapIndexed{idx, line -> parse(line, idx) }
    .asSequence().flatMap { it }

fun IntRange.overlap(r: IntRange): Boolean {
    var a = this
    var b = r

    if(a.first > b.first) {
        a = r; b = this
    }
    return b.first <= a.last
}

fun Number.overlap(char: Character): Boolean {
    if((this.row - char.row).absoluteValue <= 1) {
        return this.range.overlap(char.range)
    }
    return false
}

fun <T> Map<Int, List<T>>.getContext(rowId: Int) = (rowId - 1 .. rowId + 1)
    .map { this.getOrDefault(it, listOf()) }
    .flatMap { it.asSequence() }

fun part1(lines: List<String>): Int {
    val elements = parse(lines).toList()
    val numbers = elements.filterIsInstance<Number>()
    val chars = elements.filterIsInstance<Character>().groupBy { it.row }
    val toSum = numbers.filter { n -> chars.getContext(n.row).any { c -> n.overlap(c) } }
    return toSum.sumOf { it.value }
}

fun part2(lines: List<String>): Int {
    val elements = parse(lines).toList()
    val numbers = elements.filterIsInstance<Number>().groupBy { it.row }
    val gears = elements
        .filterIsInstance<Character>()
        .filter { it.value == "*" }

    val found = gears.map { g ->
        numbers.getContext(g.row).filter { it.overlap(g) }.toList()
    }.filter { it.size == 2 }
    return found.sumOf { it[0].value * it[1].value }
}

fun main() {
    val testInput = readInput("Day03_test")
    val input = readInput("Day03")
    part1(testInput).println()
    part1(input).println()
    part2(testInput).println()
    part2(input).println()
}