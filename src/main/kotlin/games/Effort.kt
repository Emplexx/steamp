package moe.emi.steamp.games

/**
 * Calculate the percentage by which the average will be raised in case of completion
 */
fun calcBenefit(
    achievementHave: Int,
    achievementTotal: Int,
    totalGames: Int
): Double {

    require(achievementTotal > 0)
    require(achievementHave <= achievementTotal)

    val f = achievementHave.toDouble() / achievementTotal
    val p = f * 100
    return (100.0 - p) / totalGames
}

fun calcEffort(
    achievementHave: Int,
    achievementTotal: Int,
): Int {

    require(achievementTotal > 0)
    require(achievementHave <= achievementTotal)

    return achievementTotal - achievementHave
}

fun calcEfficiencyLinear(
    achievementHave: Int,
    achievementTotal: Int,
    totalGames: Int
): Double {
    val e = calcEffort(achievementHave, achievementTotal)
    val benefit = calcBenefit(achievementHave, achievementTotal, totalGames)
    return benefit / e
}
