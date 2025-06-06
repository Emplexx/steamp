package moe.emi.steamp

import moe.emi.steamp.games.*
import moe.emi.steamp.io.getGames
import moe.emi.steamp.io.localPropContext

suspend fun runProgram(additionalGames: (List<Ach>) -> List<Ach>) {

    val games = getGames(localPropContext())

    println("Games owned: ${games.size}")

    val (counted, notCounted) = partitionByIfCounts(games)

    println()
    println("Games in your library that don't count towards avg.:")
    notCounted.sortedBy(abc).onEach { println(it.game.gameName) }

    println()
    println("Games in your library that count towards avg.: ${counted.size}")
    counted
        .sortedWith(completionThenAbcComparator)
        .onEach {
            println("${it.completionPercent} | ${it.game.gameName} (${it.game.gameId})")
        }

    val combined = combineGames(counted).let(additionalGames)

    val average = combined.avgBy { it.percent }

    println()
    println("Avg. completion rate: $average in ${combined.size} games")

    val best = getBestGamesToCompleteNext(combined)

    println()
    println("Best games to complete next by least effort for most % (in order from best to worst):")
    println("[C: Contribution towards average | C/A: Contribution per achievement]")
    best.onEachIndexed { i, ach ->
        val name = counted.find { it.game.gameId == ach.id }?.game?.gameName
        val contribution = calcBenefit(ach.achieved, ach.total, combined.size)
        println("#$i. $name (${ach.id}) | C: $contribution | C/A: ${contribution / (ach.total - ach.achieved)}")
    }

}