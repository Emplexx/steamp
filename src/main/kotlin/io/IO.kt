@file:OptIn(ExperimentalSerializationApi::class)

package moe.emi.steamp.io

import io.github.j4ckofalltrades.steam_webapi.types.Game
import io.github.j4ckofalltrades.steam_webapi.types.GetOwnedGamesParams
import io.github.j4ckofalltrades.steam_webapi.types.OwnedGames
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
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

fun getOwnedGamesFile() = Paths.get(resDir, "ownedGames")
fun getGameInfoFile(id: Long) = Paths.get(resDir, "$id")

//suspend fun Context.getOwnedGames(): OwnedGames {
//    val file = getOwnedGamesFile()
//
//    return if (file.exists()) {
//        file.readText().let { json.decodeFromString(it) }
//    }
//    else {
//        println("Fetching owned games")
//        val results = api.playerService().getOwnedGames(steamId, GetOwnedGamesParams(includePlayedFreeGames = true))
//        val resStr = json.encodeToString(results.response)
//        val path = getOwnedGamesFile().createFile()
//        path.writeText(resStr)
//
//        results.response
//    }
//}

suspend fun Context.askAndRefresh() {

    val refresh = !getOwnedGamesFile().exists() || run {
        println("Try refresh cached games? y/n")
        when (readln()) {
            "y", "Y" -> true
            else -> false
        }
    }

    if (refresh) refreshOwnedGames()
}

suspend fun Context.refreshOwnedGames() {
    val (gamesToFetch, newGamesList) = run {
        val lastGames = getOwnedGamesFile().readText().let { json.decodeFromString<OwnedGames>(it) }.games
        val lastGamesIds = lastGames.map { it.appId }

        val updatedGames = api.playerService()
            .getOwnedGames(steamId, GetOwnedGamesParams(includePlayedFreeGames = true))
        val (sameGames, newGames) = updatedGames.response.games
            .partition { it.appId in lastGamesIds }

        val playedSince = mutableListOf<Game>()
        for (game in lastGames) {
            val updatedGame = sameGames.find { it.appId == game.appId } ?: continue
            if (updatedGame.playtimeForever > game.playtimeForever) playedSince.add(updatedGame)
        }
        newGames
            .let { playedSince.addAll(it) }

        playedSince to updatedGames
    }

    println("Found ${gamesToFetch.size} games to refresh")

    for (g in gamesToFetch) getOrDownloadFile(g.appId, force = true)

    val resStr = json.encodeToString(newGamesList.response)
    val path = getOwnedGamesFile().also {
        if (it.exists()) it.deleteExisting()
        if (!it.exists()) it.createFile()
    }

    path.writeText(resStr)
}

suspend fun Context.getAllGames(): List<GameAchievements> {
    val games: OwnedGames = if (getOwnedGamesFile().exists()) {
        getOwnedGamesFile().readText().let { json.decodeFromString(it) }
    }
    else {
        println("Fetching owned games")
        val results = playerApi.getOwnedGames(steamId, GetOwnedGamesParams(includePlayedFreeGames = true))
        val resStr = json.encodeToString(results.response)
        val path = getOwnedGamesFile().createFile()
        path.writeText(resStr)

        results.response
    }
    return games.games.mapNotNull { game ->
        getOrDownloadFile(game.appId)?.let {
            GameAchievements(
                game.appId,
                it.gameName,
                it.achievements,
            )
        }
    }
}

suspend fun Context.getOrDownloadFile(gameId: Long, force: Boolean = false): FuckingAchievements? {
    val file = getGameInfoFile(gameId)

    return if (force || !file.exists()) {
        println("Fetching $gameId")

        val results = api.userStatsApi().getAchievementsGood(key, steamId, gameId)
        val resStr = json.encodeToString(results)
        val path = file.also {
            if (it.exists()) it.deleteExisting()
            if (!it.exists()) it.createFile()
        }
        path.writeText(resStr)

        results
    } else {
        return file.readText().let {
            json.decodeFromString(it)
        }
    }
}