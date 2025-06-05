package moe.emi.steamp

import moe.emi.steamp.games.Ach

suspend fun main() {

    runProgram { steamGames ->

        steamGames
            // Hypothetically complete any game by its ID:
            .override(210970, 1.0)

            // Or add a hypothetical game that would count towards average:
            // .plusFull()
            // .plus(Ach(1, 50))
    }

}

/**
 * Add games that are not available in your Steam library, but that you still have achievements in, here.
 * This could be games you refunded, or that are not available anymore (for some reason).
 */
val manualInput = listOf<Ach>(
    Ach(1, 10), // BELOW
    Ach(1, 37), // Chained echoes
    Ach(1, 11), // Iconoclasts
    Ach(4, 66), // NFS Hot Pursuit
    Ach(1, 13), // No umbrellas allowed
    Ach(1, 15), // Sonic Adventure DX
    Ach(1, 15), // Sonic Adventure 2
)

fun List<Ach>.override(id: Long, completion: Double) = toMutableList().apply {
    val index = indexOfFirst { it.id == id }
    if (index == -1) return@apply

    val item = this[index]
    this[index] = item.copy(achieved = (item.total * completion).toInt())
}.toList()

fun List<Ach>.plusFull() = this + Ach(1, 1)