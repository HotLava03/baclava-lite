package io.github.hotlava03.baclavalite.commands.owner

import io.github.hotlava03.baclavalite.commands.Command
import io.github.hotlava03.baclavalite.commands.CommandEvent
import io.github.hotlava03.baclavalite.commands.CommandHandler
import io.github.hotlava03.baclavalite.util.eval
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class Eval(private val commandHandler: CommandHandler) : Command(), CoroutineScope {
    private val codeBlockRegex = "^```\\w*\\s+(.+)\\s*```".toRegex(RegexOption.DOT_MATCHES_ALL)

    // Initialize Coroutine context.
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    init {
        name = "eval"
        category = Category.OWNER
        description = "Evaluate Kotlin code."
        aliases = arrayOf("keval")
        usage = "<kotlin code>"
        minArgs = 1
    }

    override fun onCommand(e: CommandEvent) {
        // Message can't ever be null since this can't be run from slash commands.
        val content = e.message!!.contentRaw.substring(e.message.contentRaw.indexOf(" ") + 1)

        val code: String = if (content.contains(codeBlockRegex)) content.replace(codeBlockRegex, "$1")
        else content

        val variables: MutableMap<String, Any> = HashMap()
        variables["e"] = e
        variables["event"] = e
        variables["jda"] = e.jda
        variables["client"] = e.jda
        if (e.isFromGuild) variables["guild"] = e.guild!!
        variables["channel"] = e.channel
        variables["author"] = e.author
        if (e.isFromGuild) variables["member"] = e.member!!
        variables["message"] = e.message
        variables["commandHandler"] = commandHandler
        variables["handler"] = commandHandler

        e.reply("Compiling...") {
            launch {
                val result = eval(code, variables)
                    .replace(e.jda.token.toRegex(), "censored")
                it.editMessage("```\n${result}```").queue()
            }
        }
    }
}