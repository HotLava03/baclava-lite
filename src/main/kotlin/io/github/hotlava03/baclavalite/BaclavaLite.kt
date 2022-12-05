package io.github.hotlava03.baclavalite

import io.github.hotlava03.baclavalite.functions.getLogger
import io.github.hotlava03.baclavalite.commands.basic.Help
import io.github.hotlava03.baclavalite.listeners.ChatListener
import io.github.hotlava03.baclavalite.util.DEBUG_MODE
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.GatewayIntent

class BaclavaLite : ListenerAdapter() {
    private val chatListener = ChatListener()

    fun start(token: String) {
        val jda = JDABuilder.create(token, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .addEventListeners(
                        *eventListeners().toTypedArray(), // Spread list for it to work in a vararg.
                ).build()

        val help = (chatListener.commandHandler["help"] as Help)
        help.helpMessage = help.initHelp()

        chatListener.commandHandler.getAll()
                .filter { it.isSlashCommandCompatible }
                .forEach {
                    val commandData = CommandData(it.name, it.description
                            ?: "No description provided.")
                    commandData.addOptions(it.slashCommandOptions)
                    jda.upsertCommand(commandData).queue()
                }
    }

    override fun onReady(event: ReadyEvent) {
        getLogger().info("Baclava started.")
        if (DEBUG_MODE) {
            event.jda.presence.setPresence(OnlineStatus.IDLE, Activity.watching("debuggers..."))
            getLogger().warn("Debug mode is on.")
        } else {
            event.jda.presence.setPresence(OnlineStatus.ONLINE, Activity.listening("to you."))
            getLogger().info("Set to production.")
        }
    }

    private fun eventListeners(): List<ListenerAdapter> = listOf(
            this,
            chatListener,
    )
}