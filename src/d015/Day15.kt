package d015

import println
import readInput



fun String.hash() = this.fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }
fun part1(input: List<String>): Any {
    return input[0].split(",").sumOf { it.hash() }
}

data class Operation(val label: String, val type: Char, val value: Int = -1) {
    val remove = type == '-'
}

fun parse(input: List<String>): List<Operation> {
    return input[0].split(',').map {
        if(it.endsWith('-')) {
            Operation(it.dropLast(1), '-')
        } else {
            val (l, v) = it.split('=')
            Operation(l, '=', v.toInt())
        }
    }
}

fun part2(input: List<String>): Any {
    val map = Array<List<Operation>>(256){ emptyList() }
    val ops = parse(input)
    ops.forEach{op ->
        val idx = op.label.hash()
        if(op.remove) {
            map[idx] = map[idx].filter { it.label != op.label }
        } else {
            if(map[idx].any{ it.label == op.label }) {
                map[idx] = map[idx].map { if(it.label == op.label) op else it }
            } else {
                map[idx] = map[idx] + op
            }
        }
    }
    return map.mapIndexed { boxId, box ->
        box.mapIndexed { i, op -> (i+1) * op.value }.sum() * (boxId + 1)
    }.sum()
}

fun main() {
    val testInput = readInput("Day15_test")
    val input = readInput("Day15")

    part1(testInput).println()
    part1(input).println()

    part2(testInput).println()
    part2(input).println()

}