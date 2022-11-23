package io.github.hotlava03.baclavalite.commands.basic

import io.github.hotlava03.baclavalite.commands.Command
import io.github.hotlava03.baclavalite.commands.CommandEvent
import io.github.hotlava03.baclavalite.util.VERSION

class Version : Command() {
    init {
        name = "version"
        category = Category.BASIC
        description = "Retrieve bot version information."
        aliases = arrayOf("ver", "about")
        isSlashCommandCompatible = true
    }

    override fun onCommand(e: CommandEvent) {
        e.reply("**Baclava Lite** `$VERSION` running on JDK 17." +
                "\nWritten with Kotlin." +
                "\nhttps://github.com/HotLava03/baclava-lite")
    }
}