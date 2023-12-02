package d002

import println
import readInput
import toPair

data class Set(val red: Int, val green: Int, val blue: Int)
data class Game(val id:Int, val sets: List<Set>)

fun parse(line: String): Game {
    fun toSet(str: String): Set {
        val m = str.split(",").associate {
            val (no, label) = it.trim().split("""\s+""".toRegex()).toPair()
            label to no.toInt()
        }
        return Set(
            m.getOrDefault("red",0 ),
            m.getOrDefault("green",0),
            m.getOrDefault("blue",0)
        )
    }

    val groups =""".*?(\d+):(.+)""".toRegex().find(line)!!.groups

    val id = groups[1]!!.value.toInt()
    val sets = groups[2]!!.value.split(';').map { toSet(it) }

    return Game(id, sets)
}

fun toGame(input: List<String>) = input.map { parse(it) }

fun part1(input: List<String>): Int {
    fun possible(g: Game) = g.sets.all {
        it.red <= 12 && it.green <= 13 && it.blue <= 14
    }

    return toGame(input)
        .filter { possible(it) }
        .sumOf{it.id}
}


fun part2(input: List<String>): Any {
    val powers = toGame(input).map { game ->
        game.sets.maxOf { it.red }.toLong() *
            game.sets.maxOf { it.green } *
            game.sets.maxOf { it.blue }
    }
    return powers.sum()
}

fun main() {

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    val input = readInput("Day02")
    part1(testInput).println()
    part1(input).println()

    part2(testInput).println()
    part2(input).println()
}
