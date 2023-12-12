package d007

import println
import readInput



data class Hand(val cards: String)

data class Turn(val hand: Hand, val bid: Int)

fun parse(input: List<String>): List<Turn> {
    fun parse(turn: String): Turn {
        val (cards, bid) = turn.split(" ")
        val hand = Hand(cards)
        return Turn(hand, bid.toInt())
    }
    return input.map { parse(it) }
}

fun String.strength(): Int {
    val groups = this.groupBy { it }.map { it.value.size }.sortedDescending()

    return when(groups[0]) {
        5 -> 7
        4 -> 6
        3 -> if(groups[1] == 2) 5 else 4
        2 -> if(groups[1] == 2) 3 else 2
        1 -> 1
        else -> error("Unknown")
    }
}

fun sort(input: List<Turn>, cardsRank: Map<Char, Int>, strength: (Hand) -> Int ): List<Turn> {
    return input.sortedWith{a,b ->
        if(strength(a.hand) == strength(b.hand)) {
            val aHand = a.hand.cards.map { cardsRank[it]!! }
            val bHand = b.hand.cards.map { cardsRank[it]!! }

            aHand.zip(bHand).first { it.first != it.second }.let { (a,b) ->
                a - b }

        } else {
            strength(a.hand) - strength(b.hand)
        }
    }
}

fun part1(input: List<Turn>): Any {
    val cardsRank = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
        .reversed()
        .mapIndexed { i, v -> v to i }
        .toMap()

    fun Hand.strength() = cards.strength()

    val sorted = sort(input, cardsRank, Hand::strength)

    return sorted.mapIndexed {idx, turn ->  turn.bid.toLong() * (idx + 1) }.sum()

}

fun part2(input: List<Turn>): Any {
    val cards = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J').reversed()
    val cardsRank = cards
        .mapIndexed { i, v -> v to i }
        .toMap()

    val strongest: (Hand) -> Int = { hand ->
        cards
            .drop(1)
            .map { c -> hand.cards.replace('J', c) }
            .maxOf { it.strength() }
    }

    val sorted = sort(input, cardsRank, strongest)

    return sorted.mapIndexed {idx, turn ->  turn.bid.toLong() * (idx + 1) }.sum()
}

fun main() {
    val testInput = parse(readInput("Day07_test"))
    val input = parse(readInput("Day07"))

    part1(testInput).println()
    part1(input).println()

    part2(testInput).println()
    part2(input).println()
}


