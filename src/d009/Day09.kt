package d009

import println
import readInput


fun parse(input: List<String>): List<List<Int>> {
    fun parse(input: String) = input.split("""\s+""".toRegex()).map { it.toInt() }
    return input.map { parse(it) }
}
fun nextValue(vals: List<Int>): Int {
    return if(vals.all { it == 0 }) {
        0
    } else {
        val nv = vals.drop(1).zip(vals).map { (a,b) -> a - b }
        vals.last() + nextValue(nv)
    }
}

fun prevValue(vals: List<Int>): Int {
    return if(vals.all { it == 0 }) {
        0
    } else {
        val nv = vals.drop(1).zip(vals).map { (a,b) -> a - b }
        val x = prevValue(nv)
        vals.first()- x
    }
}

fun part1(input: List<List<Int>>): Any {
    return input.sumOf { nextValue(it) }
}

fun part2(input: List<List<Int>>): Any {
    return input.sumOf { prevValue(it) }
}
fun main() {
    val x = listOf(1,2,3,4)
    println(x.drop(1).zip(x).map { (a,b) -> a - b })
    println(nextValue(x))
    val testInput = parse(readInput("Day09_test"))
    val input = parse(readInput("Day09"))

    part1(testInput).println()
    part1(input).println()

    part2(testInput).println()
    part2(input).println()
}