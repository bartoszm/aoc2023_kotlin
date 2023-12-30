package d005

import println
import readInput
import toPair
data class Item(val value: Long, val name: String)

data class NamedRange(val name: String, val range: LongRange) {
    constructor(name: String, from: Long, length: Long) :  this(name,
        if(length < 1) { LongRange.EMPTY } else { from..<from+length })
    fun rename(name: String) = NamedRange(name, range)
    fun split(value: Long): Pair<NamedRange, NamedRange>? {
        if(value !in range) return null
        val first = NamedRange(name, range.first..<value)
        val second = NamedRange(name, value.. range.last)
        return first to second
    }
}

class Converter(val from: NamedRange, val to: NamedRange) {
    fun convert(value: Long): Long? {
        if(value !in from.range) return null
        return value - from.range.first + to.range.first
    }

    fun convert(item: Item): Item? {
        val converted = convert(item.value) ?: return null
        return Item(converted, to.name)
    }

    override fun toString(): String {
        return "${from.name} ${from.range} -> ${to.name} ${to.range}"
    }
}

data class Puzzle(val inputs: List<Long>, val converters: List<Converter>)

fun parse(inputs: List<String>): Puzzle {

    fun seedsParser(input: String): List<Long> = input
        .split(": ")[1].split(" ")
        .map { it.trim().toLong()}

    fun parseName(input: String) = input.split(" ")[0].split("-to-").toPair()
    val seeds = seedsParser(inputs[0])
    var converterText = inputs.drop(2)

    var names: Pair<String, String>? = null

    val converters = sequence<Converter> {
        converterText.forEach { line ->
            if(line.isNotEmpty()) {
                if(line[0].isLetter()) {
                    names = parseName(line)
                } else if(line[0].isDigit()) {
                    val values = line.split(" ").map { it.toLong() }
                    yield(Converter(
                        from = NamedRange(names!!.first, values[1], values[2]),
                        to = NamedRange(names!!.second, values[0], values[2])
                    ))
                }
            }
        }
    }.toList()
    return Puzzle(seeds, converters)
}

fun part1(inputs: List<String>): Long {
    val puzzle = parse(inputs)
    val converters = puzzle.converters.groupBy { it.from.name }
    fun convert(input: Item): Item {
        val forInput = converters[input.name]!!
        return forInput.map { c ->
            c.convert(input)
        }.find { it != null } ?: Item(input.value, forInput[0].to.name)
    }

    fun convertTo(input: Item, target: String): Item {
        var result = input
        while(result.name != target) {
            result = convert(result)
        }
        return result
    }

    return puzzle.inputs
        .map { Item(it, "seed") }
        .map { convertTo(it, "location") }.minBy{ it.value }.value
}

fun LongRange.split(range: LongRange): Pair<List<LongRange>, LongRange> {
    var r = this

    if(this.last < range.first) return listOf(this) to  LongRange.EMPTY
    if(range.last < this.first) return listOf(LongRange.EMPTY) to this

    val ranges = mutableListOf<LongRange>()
    if(this.first < range.first) {
        ranges.add(this.first..<range.first)
        r = range.first..r.last
    }

    return if(range.last < this.last) {
        ranges.add(r.first..range.last)
        ranges to (range.last + 1 .. this.last)
    } else {
        ranges.add(r)
        ranges to LongRange.EMPTY
    }
}

fun part2(inputs: List<String>): Any {
    val puzzle = parse(inputs)
    val map = puzzle.converters
        .groupBy { it.from.name }
        .mapValues {(_, v) ->
            v.sortedBy { it.from.range.first }
        }

    fun Converter.convert(r: NamedRange): Pair<List<NamedRange>, NamedRange>? {
        if(r.range.isEmpty()) return null
        val (ranges, rest) = r.range.split(this.from.range)
        val toMap = ranges.map {
            if (it.first in this.from.range) {
                val from = this.convert(it.first)!!
                val to = this.convert(it.last)!!
                NamedRange(this.to.name, from..to)
            } else {
                NamedRange(this.to.name, it)
            }
        }
        return toMap to NamedRange(this.from.name, rest)
    }


    fun convert(input: NamedRange): List<NamedRange> {
        var rest = input
        val converters = map[input.name]!!
        val toName = converters[0].to.name

        return sequence {
            for(c in converters) {
                val (n, r) = c.convert(rest) ?: break
                yieldAll(n.filter { ! it.range.isEmpty() })
                rest = r
            }
            if(! rest.range.isEmpty()) yield(rest.rename(toName))
        }.toList()
    }

    fun convertTo(input: List<NamedRange>, target: String): List<NamedRange> {
        var result = input
        while(result[0].name != target) {
            result = result.flatMap { convert(it) }.distinct()
            val tmp  = result.filter {
                result
                    .filter { t -> it != t }
                    .none { t -> it.range.first in t.range && it.range.last in t.range }
            }
            result = tmp
        }
        return result
    }

    val  inputs = puzzle.inputs.windowed(2, 2).map {
        NamedRange("seed", it[0], it[1])
    }

    val converted = convertTo(inputs, "location")

    return converted.map { it.range.first }.min()
}

fun main() {
    val testInput = readInput("Day05_test")
    val input = readInput("Day05")
    part1(testInput).println()
    part1(input).println()
    part2(testInput).println()
    part2(input).println()
}