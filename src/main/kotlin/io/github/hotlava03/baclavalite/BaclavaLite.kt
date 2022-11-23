package io.github.hotlava03.baclavalite

import io.github.hotlava03.baclavalite.commands.basic.Help
import io.github.hotlava03.baclavalite.listeners.ChatListener
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.requests.GatewayIntent

class BaclavaLite {
    private val chatListener = ChatListener()

    fun start(token: String) {
        val jda = JDABuilder.create(token, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .addEventListeners(
                        *eventListeners().toTypedArray(), // Spread list for it to work in a vararg.
                ).build()

        (chatListener.commandHandler["help"] as Help).initHelp()

        chatListener.commandHandler.getAll()
                .filter { it.isSlashCommandCompatible }
                .forEach {
                    val commandData = CommandData(it.name, it.description
                            ?: "No description provided.")
                    commandData.addOptions(it.slashCommandOptions)
                    jda.upsertCommand(commandData).queue()
                }
    }

    private fun eventListeners(): List<ListenerAdapter> = listOf(
            chatListener,
    )
}