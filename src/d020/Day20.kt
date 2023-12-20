package d020

import println
import readInput
import toPair
import java.util.*


sealed interface Element {
    fun trigger(s: Boolean, src: String): Boolean?
}

class FlipFlop : Element {
    var state = false
    override fun trigger(s: Boolean, src: String): Boolean? {
        return if (!s) {
            state = !state
            return state

        } else null
    }
}

class Conjunctor(inputs: List<String>) : Element {
    private val state = inputs.associateWith { false }.toMutableMap()
    override fun trigger(s: Boolean, src: String): Boolean {
        state[src] = s
        return state.values.any { !it }
    }
}

class Broadcaster: Element {
    private val state = mutableMapOf<String, Boolean>()
    override fun trigger(s: Boolean, src: String): Boolean {
        state[src] = s
        return s
    }
}

class Board(val elements: Map<String, Element>, val connections: Map<String, List<String>>, var handler: (Signal) -> Unit = {}) {
    var countLow = 0
    var countHigh = 0

    fun trigger(signal:Boolean) {
        val (l, h) = triggerInternal(signal)
        countLow += l
        countHigh += h
    }

    data class Signal(val to: String, val from: String, val signal: Boolean)

    fun triggerInternal(signal: Boolean): Pair<Int, Int> {
        var l = if (signal) 0 else 1
        var h = if (signal) 1 else 0
        val queue: Queue<Signal> = LinkedList()
        queue.add(Signal("broadcaster", "button", signal))
        while(queue.isNotEmpty()) {
            val signal  = queue.poll()
            val element = elements[signal.to]
            if(element == null) {
                handler(signal)
                continue
            }
            val next = connections[signal.to]
            val result = element.trigger(signal.signal, signal.from)
            if(result != null) {
                val times = next?.size ?: 1
                if(result) h += times else l += times

                next?.forEach {
                    queue.add(Signal(it, signal.to, result))
                }
            }
        }
        return l to h
    }
}

fun parse(input: List<String>): Board {
    val elementNames = mutableMapOf<String, String>()
    val connections = mutableMapOf<String, List<String>>()

    fun parse(line: String) {
        // broadcaster -> a
        val (type, rest) = line.split("->").let { it.map { it.trim() }}.toPair()
        val next = rest.split(",").map { it.trim() }
        val (name, element) = when {
            type == "broadcaster" -> type to "b"
            type.startsWith("%") -> type.drop(1) to "ff"
            type.startsWith("&") -> type.drop(1) to "c"
            else -> throw IllegalArgumentException("Unknown element $type")
        }

        elementNames[name] = element
        connections[name] = next
    }

    fun inConnections(name: String): List<String> {
        return connections.filter { it.value.contains(name) }.map { it.key }
    }

    input.forEach { parse(it) }

    val elements = elementNames.mapValues { (k, v) ->
        when(v) {
            "b" -> Broadcaster()
            "ff" -> FlipFlop()
            "c" -> Conjunctor(inConnections(k))
            else -> throw IllegalArgumentException("Unknown element $v")
        }
    }

    return Board(elements, connections)
}

fun part1(input: Board): Long {
    repeat(1000, { input.trigger(false) })
    return input.countHigh.toLong() * input.countLow
}

fun part2(input: Board): Int {
    TODO()
}

fun main() {
    val testInput = parse(readInput("Day20_test"))
    val input = parse(readInput("Day20"))

    part1(testInput).println()
    part1(input).println()

//    part2(testInput).println()
    part2(input).println()

}