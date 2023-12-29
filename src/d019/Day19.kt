package d019


import println
import readInput
import toPair

typealias Part = Map<String, Int>

enum class Operation { GT, LT, GOTO}

data class Operator(val name: String, val o: Operation, val value: Int, val next: String)

data class Workflow(val name: String, val operators: List<Operator>)
fun parse(readInput: List<String>): Pair<List<Workflow>, List<Part>> {
    val workflows = mutableListOf<Workflow>()
    val parts = mutableListOf<Part>()
    var readWorkflows = true

    fun toOperation(i: Char): Operation {
        return when(i) {
            '>' -> Operation.GT
            '<' -> Operation.LT
            else -> error("Unknown operation $i")
        }
    }

    fun toWorkflow(i: String): Workflow {
        val (name, rest) = i.split("{").toPair()
        val operators = rest.dropLast(1).split(",").map { o ->
            if(o.contains(":")) {
                val (r, next) = o.split(":").toPair()
                Operator(r[0].toString(), toOperation(r[1]), r.drop(2).toInt(), next)
            } else {
                Operator("", Operation.GOTO, -1, o)
            }
        }
        return Workflow(name, operators)
    }

    fun toPart(i: String): Part {
        return i.drop(1).dropLast(1)
            .split(",")
            .map { it.trim() }
            .map {e ->
                e.split("=").toPair().let { (k, v) ->
                k to v.toInt()
            }
        }.associate { it }
    }

    for (line in readInput) {
        if(line.isEmpty()) {
            readWorkflows = false
        } else {
            if(readWorkflows) {
                workflows.add(toWorkflow(line))
            } else {
                parts.add(toPart(line))
            }
        }
    }
    return workflows to parts
}

fun Operator.next(p: Part): String? {
    return when(this.o) {
        Operation.GOTO -> this.next
        Operation.GT -> if((p[name] ?: Int.MIN_VALUE) > this.value) this.next else null
        Operation.LT -> if((p[name] ?: Int.MAX_VALUE) < this.value) this.next else null
    }
}

fun part1(input: Pair<List<Workflow>, List<Part>>): Int {
    val (workflows, parts) = input
    val map = workflows.associateBy { it.name }
    val start = map["in"]!!

    fun Workflow.next(p: Part) = operators
        .mapNotNull { it.next(p) }
        .firstOrNull()

    fun isAccepted(p: Part): Boolean {
        var decision = start.next(p)!!
        while(! setOf("A", "R").contains(decision)) {
            val nextW = map[decision]!!
            decision = nextW.next(p)!!
        }
        return decision == "A"
    }

    val accepted = parts.filter { p -> isAccepted(p) }
    return accepted.sumOf { it.values.sum() }

}

data class Range(val from: Int = 1, val to: Int = 4000) {
    val count: Int by lazy  { to - from + 1 }
    fun contains(x: Int) = x in from..to
    infix fun intersect(p: Range): Range {
        if(p.from > to || p.to < from) {
            return EMPTY
        }
        //good enough for this use case
        return if(this.from < p.from) {
            Range(p.from, minOf(to, p.to))
        } else {
            Range(from, minOf(to, p.to))
        }
    }

    companion object {
        val EMPTY = Range(0, -1)
    }
}

typealias Options = Map<String, Range>

fun Operator.asRange() = when(this.o) {
    Operation.GOTO -> Range.EMPTY
    Operation.GT -> Range(from = this.value + 1)
    Operation.LT -> Range(to = this.value - 1)
}

fun Operator.asCompletingRange() = when(this.o) {
    Operation.GOTO -> Range.EMPTY
    Operation.GT -> Range(to = this.value)
    Operation.LT -> Range(from = this.value)
}

fun part2(workflows: List<Workflow>): Long {
    val map = workflows.associateBy { it.name }
    val initial = sequenceOf("a", "x", "m", "s").associateWith { Range() }
    fun Workflow.execute(opt: Options) : List<Pair<String,Options>> {
        var current = opt
        return sequence {
            for(o in this@execute.operators) {

                if(o.o == Operation.GOTO) {
                    yield(o.next to current)
                    continue
                } else {
                    val range = current[o.name] ?: Range.EMPTY
                    val take = range intersect o.asRange()
                    val leave = range intersect o.asCompletingRange()
                    yield(o.next to current + (o.name to take))
                    current = current + (o.name to leave)
                }
            }
        }.toList()
    }

    val queue = ArrayDeque(listOf("in" to initial))
    val result = sequence {
        while(queue.isNotEmpty()) {
            val (name, opt) = queue.removeFirst()
            val next = map[name]!!.execute(opt)
            queue.addAll(next.filter { it.first != "A" && it.first != "R" })
            yieldAll(next.filter { it.first == "A" })
        }
    }.toList()
    return result.map { it.second }.map { m ->
        m.values.map { it.count.toLong() }.reduce { acc, i -> acc * i }
    }.sum()
}
fun main() {
    val testInput = parse(readInput("Day19_test"))
    val input = parse(readInput("Day19"))

    part1(testInput).println()
    part1(input).println()

    part2(testInput.first).println()
    part2(input.first).println()
}