package moe.emi.steamp.test

import moe.emi.steamp.avg

fun main() {

    println(15.0 / 16.0)
    val obraDinnP = 100 - ((15.0 / 16.0) * 100)
    val predicted = obraDinnP.div(123)

    println("predicted diff.: $predicted")

//    val pNoWitness = 60.73773583832617
//    val witness =

}

fun main1() {
    val perfect = 0

    val start = listOf(
        90,
        60,
        90,
        90,
        67
    ).map { it.toDouble() }

    val other = listOf(
        90,
        100,
        90,
        90,
        67
    ).map { it.toDouble() }

    val total = (perfect * 100.0) + other.sum()
    val count = other.size + perfect

    println("Avg.: ${total / count}")

    println("start Avg.: ${start.avg()}")

    println("diff.: ${start.avg() - (total / count)}")

    val predicted = (100 - 60.0).div(start.size)

    println("predicted diff.: $predicted")
}

