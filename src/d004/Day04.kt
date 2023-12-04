package d004

import println
import readInput
import toPair
import kotlin.system.measureTimeMillis


data class Card(val id: String, val winning: List<Int>, val value: List<Int>) {
    val shared  = (this.winning.toSet() intersect this.value.toSet()).size
}
fun parse(str: String): Card {
    fun String.toInts() = this.trim().split("""\s+""".toRegex()).map { it.toInt() }

    val (id, numbers) = str.split(":").toPair()
    val (winning, value) = numbers.split("|").toPair()
    return Card(id, winning.toInts(), value.toInts())
}

fun part1(lines: List<String>): Long {
    val points = lines.map { parse(it) }
        .map {card ->
            if(card.shared == 0) 0 else 1.toLong() shl (card.shared-1)
        }
    return points.sum()
}

fun part2(lines: List<String>): Long {
    val counter = LongArray(lines.size) {1}
    var cards = lines.map { parse(it) }
    cards.forEachIndexed { idx, card ->
        if(card.shared > 0) {
            for(j in 1..card.shared) {
                counter[idx+j] += counter[idx]
            }
        }
    }
    return counter.sum()
}

fun main() {
    val testInput = readInput("Day04_test")
    val input = readInput("Day04")
    part1(testInput).println()
    part1(input).println()
    part2(testInput).println()
    val time = measureTimeMillis {
        part2(input).println()
    }
    println("Execution time $time ms")
}