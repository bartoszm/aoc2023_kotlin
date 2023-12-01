package d001

import convert
import println
import readInput

fun part1(input: List<String>): Int {
    fun parse(input: List<String>): List<List<Char>> {
        return input.convert { s -> s.asSequence()
            .filter { it.isDigit() }
            .toList()
        }
    }

    return parse(input).map {
        Integer.parseInt("" + it.first() + it.last())
    }.sum()
}

fun parseLine(input: String): List<Char> {
    val digits =  listOf(
        "one" to '1',
        "two" to '2',
        "three" to '3',
        "four" to '4',
        "five" to '5',
        "six" to '6',
        "seven" to '7',
        "eight" to '8',
        "nine" to '9'
    )

    var remaining = input

    return sequence {
        while(remaining.isNotEmpty()) {
            if (remaining.get(0).isDigit()) {
                yield(remaining.get(0))
            } else {
                digits.filter {
                    remaining.startsWith(it.first)
                }.firstOrNull()?.let { yield(it.second) }

            }
            remaining = remaining.drop(1)
        }
    }.toList()
}

fun part2(input: List<String>): Any {

    fun parse(input: List<String>) = input.map { parseLine(it) }

    return parse(input).sumOf { "${it.first()}${it.last()}".toInt() }
}

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)
    val input = readInput("Day01")
    part1(testInput).println()
    part1(input).println()

    val testInput2 = readInput("Day01_test2")
    part2(testInput2).println()
    part2(input).println()
}
