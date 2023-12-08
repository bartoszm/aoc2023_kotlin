package d005

import println
import readInput
import toPair
data class Item(val value: Long, val name: String)

data class NamedRange(val name: String, val range: LongRange) {
    constructor(name: String, from: Long, length: Long) : this(name, from..<from+length)
}

class Converter(val from: NamedRange, val to: NamedRange) {
    fun convert(value: Long): Long? {
        if(value !in from.range) return null
        return value - from.range.first + to.range.first
    }

    fun convert(value: Item): Item? {
        val converted = convert(value.value) ?: return null
        return Item(converted, to.name)
    }

    override fun toString(): String {
        return "${from.name} ${from.range} -> ${to.name} ${to.range}"
    }
}

data class Puzzle(val inputs: List<Item>, val converters: List<Converter>)


fun parse(inputs: List<String>, seedsParser: (String) -> List<Item>): Puzzle {


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
                        NamedRange(names!!.first, values[1], values[2]),
                        NamedRange(names!!.second, values[0], values[2])
                    ))
                }
            }

        }
    }.toList()
    return Puzzle(seeds, converters)
}

fun part1(inputs: List<String>): Long {
    fun parseSeeds(seeds: String): List<Item> = seeds
        .split(": ")[1].split(" ")
        .map {
            Item(it.trim().toLong(), "seed")

        }
    val puzzle = parse(inputs, ::parseSeeds)
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

    return puzzle.inputs.map { convertTo(it, "location") }.minBy{ it.value }.value
}

fun main() {
    val testInput = readInput("Day05_test")
    val input = readInput("Day05")
    part1(testInput).println()
    part1(input).println()
//    part2(testInput).println()
//    val time = measureTimeMillis {
//        part2(input).println()
//    }
//    println("Execution time $time ms")
}