package moe.emi.steamp.io

import io.github.j4ckofalltrades.steam_webapi.core.AppId
import io.github.j4ckofalltrades.steam_webapi.core.SteamId
import io.github.j4ckofalltrades.steam_webapi.types.PlayerAchievement
import io.github.j4ckofalltrades.steam_webapi.wrapper.ISteamUserStatsWrapper
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

suspend fun ISteamUserStatsWrapper.getAchievementsGood(webApiKey: String, steamId: SteamId, appId: AppId): FuckingAchievements? =
    this.webApiClient.get("/ISteamUserStats/GetPlayerAchievements/v1") {
        parameter("key", webApiKey)
        parameter("steamid", steamId)
        parameter("appid", appId)
    }
        .also {
            println(it.status)
            println(it.bodyAsText())
        }

        .let {
            if (it.status.isSuccess())  json.decodeFromString(PlayerStatsWrapperGood.serializer(), it.bodyAsText()).playerstats
            else null
        }

typealias FuckingAchievements = PlayerStatsGood

@Serializable
data class PlayerStatsWrapperGood(
    val playerstats: PlayerStatsGood,
)

@Serializable
data class PlayerStatsGood(
    @SerialName("steamID")
    val steamId: SteamId,
    val gameName: String,
    val achievements: List<PlayerAchievement> = emptyList(),
    val success: Boolean,
)

data class GameAchievements(
    val gameId: Long,
    val gameName: String,
    val achievements: List<PlayerAchievement> = emptyList()
)