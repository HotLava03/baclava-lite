package io.github.hotlava03.baclavalite.commands.basic

import io.github.hotlava03.baclavalite.commands.Command
import io.github.hotlava03.baclavalite.commands.CommandEvent
import io.github.hotlava03.baclavalite.commands.CommandHandler
import io.github.hotlava03.baclavalite.util.BACLAVA_COLOR
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.awt.Color
import java.time.Instant

class Help(private val commandHandler: CommandHandler) : Command() {
    lateinit var helpMessage: String

    init {
        name = "help"
        category = Category.BASIC
        description = "Get general bot help."
        aliases = arrayOf("halp", "hulp")
        isSlashCommandCompatible = true
        usage = "[command]"
    }

    override fun onCommand(e: CommandEvent) {
        val option = e.slashCommandEvent?.getOption("command")?.asString
        if (e.args.isNotEmpty() || option !== null) {
            val cmd = commandHandler[if (option !== null) option else e.args[0]]
            if (cmd == null) e.reply("**Unknown command.**")
            else e.reply(
                    """**Help for command: ${cmd.name}**
                    |${cmd.description}
                    |**Usage:** ${cmd.usage}
                    ${
                        if (cmd.aliases.isNotEmpty())
                            "|**Aliases:** ${cmd.aliases.joinToString("`, `", "`", "`")}"
                        else ""
                    }
                    |*Arguments in `<>` are mandatory and arguments in `[]` are optional.*
                """.trimMargin()
            )

            return
        }

        val embed = EmbedBuilder()
                .setTitle("Baclava Help")
                .setDescription(helpMessage)
                .setTimestamp(Instant.now())
                .setFooter("Use >>help [command] for individual usage.")
                .setColor(Color.decode(BACLAVA_COLOR))
                .build()

        e.reply(embed)
    }

    fun initHelp(): String {
        slashCommandOptions = listOf(
                generateCommandOption()
        )
        return buildString {
            var previousCategory: Category? = null
            commandHandler.getAll().sortedBy { it.category }.forEach {
                if (previousCategory != it.category) {
                    if (it.category == Category.OWNER) return@forEach
                    appendLine("\n**${it.category.format()} commands**")
                    previousCategory = it.category
                }

                appendLine("**${it.name}** » ${it.description}")
            }
            appendLine("\n**How to use the Chatbot system**")
            appendLine("**@mentioning** » Starts a new conversation")
            appendLine("**replying** » Replies to a conversation, using previous context")
        }
    }

    private fun generateCommandOption(): OptionData {
        val data = OptionData(OptionType.STRING, "command",
                "The command to get help from.", false)
        commandHandler.getAll()
                .filter { it.isSlashCommandCompatible }
                .forEach { data.addChoice(it.name, it.name) }
        return data
    }
}
