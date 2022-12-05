package io.github.hotlava03.baclavalite.commands.basic

import io.github.hotlava03.baclavalite.commands.Command
import io.github.hotlava03.baclavalite.commands.CommandEvent

class Ping : Command() {
    init {
        name = "ping"
        category = Category.BASIC
        description = "Get the bot's ping."
        isSlashCommandCompatible = true
    }

    override fun onCommand(e: CommandEvent) {
        e.jda.restPing.queue {
            e.reply("**REST Ping:** ${it}ms - **Gateway Ping:** ${e.jda.gatewayPing}ms")
        }
    }
}