package io.github.hotlava03.baclavalite.commands

import io.github.hotlava03.baclavalite.commands.basic.Help
import io.github.hotlava03.baclavalite.commands.basic.Ping
import io.github.hotlava03.baclavalite.commands.basic.Version
import io.github.hotlava03.baclavalite.commands.owner.Eval
import io.github.hotlava03.baclavalite.commands.utility.Color
import io.github.hotlava03.baclavalite.commands.utility.Translate

class CommandHandler {
    private val commands: MutableMap<String, Command> = mutableMapOf(
            "eval" to Eval(this),
            "help" to Help(this),
            "version" to Version(),
            "ping" to Ping(),
            "color" to Color(),
            "translate" to Translate(),
    )

    operator fun get(name: String): Command? {
        var found: Command? = commands[name]
        if (found === null) {
            val matches = commands.filter { it.value.aliases.contains(name.lowercase()) }
            if (matches.isNotEmpty()) found = matches.values.first()
        }

        return found
    }

    operator fun set(name: String, command: Command) {
        commands[name] = command
    }

    fun getAll(): MutableCollection<Command> {
        return commands.values
    }
}