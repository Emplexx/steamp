@file:OptIn(ExperimentalSerializationApi::class)

package moe.emi.steamp.io

import io.github.j4ckofalltrades.steam_webapi.core.SteamWebApi
import io.github.j4ckofalltrades.steam_webapi.core.WebApiClient
import io.github.j4ckofalltrades.steam_webapi.wrapper.IPlayerServiceWrapper
import io.ktor.client.plugins.logging.*
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import java.io.FileInputStream
import java.util.*

data class Context(
    val key: String,
    val steamId: String,
) {
    val api = SteamWebApi(key)
    val playerApi = IPlayerServiceWrapper(
        key,
        WebApiClient.default().config {
            install(Logging) {
                this.level = LogLevel.ALL
            }
        }
    )
}

fun localPropContext() = with(Properties()) {

    File(System.getProperty("user.dir") + "\\local.properties")
        .also(File::createNewFile)
        .let(::FileInputStream)
        .let(::load)

    require(containsKey("webApiKey")) { "local.properties has no webApiKey property. Find your key here: https://steamcommunity.com/dev/apikey" }
    require(containsKey("steamId")) { "local.properties has no steamId property. Find your Steam ID here: https://store.steampowered.com/account/" }

    Context(
        getProperty("webApiKey"),
        getProperty("steamId"),
    )
}