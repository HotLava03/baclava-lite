package io.github.hotlava03.baclavalite

import io.github.hotlava03.baclavalite.listeners.ChatListener
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent

class BaclavaLite {
    fun start(token: String) {
        JDABuilder.create(token, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .addEventListeners(
                        *eventListeners().toTypedArray(), // Spread list for it to work in a vararg.
                ).build()
    }

    private fun eventListeners(): List<ListenerAdapter> = listOf(
            ChatListener(),
    )
}