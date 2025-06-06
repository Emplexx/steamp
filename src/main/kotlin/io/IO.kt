@file:OptIn(ExperimentalSerializationApi::class)

package moe.emi.steamp.io

import io.github.j4ckofalltrades.steam_webapi.types.OwnedGames
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*

val json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

private val resDir = run {
    val projectDir = System.getProperty("user.dir")
    val resDir = "\\src\\main\\resources\\"
    "$projectDir$resDir"
}

fun ownedGamesFile(): Path = Paths.get(resDir, "ownedGames")
fun gameInfoFile(id: Long): Path = Paths.get(resDir, "$id")

suspend fun getGames(context: Context): List<GameAchievements> = with(context) {
    if (ownedGamesFile().exists() && promptRefresh()) refreshOwnedGames()
    return getAllGames()
}

private fun promptRefresh() = run {
    println("Try refresh cached games? y/n")
    when (readln()) {
        "y", "Y" -> true
        else -> false
    }
}

suspend fun Context.refreshOwnedGames() {

    val latestGames = apiGetOwnedGames()

    val gamesToRefresh = run {

        val localGames = readFromFile<OwnedGames>(ownedGamesFile()).games.associateBy { it.appId }

        latestGames.response.games
            .partition { it.appId in localGames.keys }
            .let { (sameGames, newGames) ->

                sameGames
                    .filter {
                        val local = localGames[it.appId] ?: return@filter false
                        it.playtimeForever > local.playtimeForever
                    }
                    .plus(newGames)
            }
    }

    println("Found ${gamesToRefresh.size} games to refresh")

    for (game in gamesToRefresh) getGameFromFileOrDownload(game.appId, force = true)

    latestGames.response.overwriteToFile(ownedGamesFile())
}

suspend fun Context.getAllGames(): List<GameAchievements> {

    val file = ownedGamesFile()

    val games: OwnedGames =
        if (file.exists()) readFromFile(file)
        else {
            println("Fetching owned games")
            apiGetOwnedGames()
                .response
                .also { it.overwriteToFile(file) }
        }

    return games.games.mapNotNull { game ->
        getGameFromFileOrDownload(game.appId)
            ?.let { GameAchievements(game.appId, it.gameName, it.achievements) }
    }
}

suspend fun Context.getGameFromFileOrDownload(gameId: Long, force: Boolean = false): FuckingAchievements? {

    val file = gameInfoFile(gameId)

    return if (force || !file.exists()) {
        println("Fetching $gameId")
        api.userStatsApi()
            .getAchievementsGood(key, steamId, gameId)
            .also { it.overwriteToFile(file) }
    } else readFromFile(file)
}

inline fun <reified T> T.overwriteToFile(file: Path) = json.encodeToString(this)
    .also {
        if (file.exists()) file.deleteExisting()
        file.createFile()
    }
    .let(file::writeText)

inline fun <reified T> readFromFile(file: Path): T = file.readText().let { json.decodeFromString(it) }