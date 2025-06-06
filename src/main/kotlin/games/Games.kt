package moe.emi.steamp.games

import moe.emi.steamp.io.GameAchievements
import moe.emi.steamp.manualInput
import moe.emi.steamp.percentage

data class Ach(
    val achieved: Int,
    val total: Int,
    val id: Long? = null,
) {
    val percent = achieved.toDouble() / total.toDouble() * 100
}

data class GameWithCompletion(
    val game: GameAchievements,
) {
    val completionPercent = game.achievements.percentage { it.achieved == 1 }
}

fun partitionByIfCounts(games: List<GameAchievements>) = games
    .map(::GameWithCompletion)
    .partition(countsTowardsAvg)


val countsTowardsAvg: (GameWithCompletion) -> Boolean = { (it) ->
    it.achievements.isNotEmpty() && it.achievements.any { a -> a.achieved != 0 }
}

val abc = { it: GameWithCompletion -> it.game.gameName.lowercase().removePrefix("the ") }

val completionThenAbcComparator = compareByDescending<GameWithCompletion> { it.completionPercent }
    .thenBy(abc)


fun combineGames(games: List<GameWithCompletion>) = games
    .map { (it) ->
        val total = it.achievements.size
        val achieved = it.achievements.count { it.achieved == 1 }
        Ach(achieved, total, it.gameId)
    }
    .plus(manualInput)

fun getBestGamesToCompleteNext(list: List<Ach>): List<Ach> =
    list
        .filter { it.percent < 100 }
        .sortedByDescending { calcEfficiencyLinear(it.achieved, it.total, list.size) }