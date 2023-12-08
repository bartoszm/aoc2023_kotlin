package d006

import println
import readInput


data class Race(val time: Long, val distance: Long)


fun minTime(r: Race): Long {
    fun LongProgression.firstMatching() = this
        .map { t -> t to t * (r.time - t) }
        .first { it.second > r.distance }.first

    val min = (0 .. r.time).firstMatching()
    val max = (0 .. r.time).reversed().firstMatching()
    return max -  min + 1
}

fun part1(input: List<String>): Long {
    fun parse(input: List<String>): List<Race> {
        fun toInts(str: String) = str.split(":")[1]
            .let { s -> s.split(" ")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { it.toLong() } }
        return toInts(input[0]).zip(toInts(input[1])).map { (t,d) -> Race(t,d) }
    }
    val times = parse(input).map { minTime(it) }
    return times.reduce { acc, v -> acc * v }
}

fun part2(input: List<String>): Long {
    fun parse(input: List<String>): Race {
        fun toInt(str: String): Long = str.split(":")[1].split(" ")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString("").toLong()
        return Race(toInt(input[0]), toInt(input[1]))
    }

    return minTime(parse(input))
}

fun main() {
    val testInput = readInput("Day06_test")
    val input = readInput("Day06")
    part1(testInput).println()
    part1(input).println()
    part2(testInput).println()
    part2(input).println()
}