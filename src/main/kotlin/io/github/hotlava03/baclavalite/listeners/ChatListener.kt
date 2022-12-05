package io.github.hotlava03.baclavalite.listeners

import io.github.hotlava03.baclavalite.cleverbot.cleverbot
import io.github.hotlava03.baclavalite.commands.Command
import io.github.hotlava03.baclavalite.commands.CommandEvent
import io.github.hotlava03.baclavalite.commands.CommandHandler
import io.github.hotlava03.baclavalite.functions.getLogger
import io.github.hotlava03.baclavalite.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.GenericMessageEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.coroutines.CoroutineContext

class ChatListener : ListenerAdapter(), CoroutineScope {
    // Initialize Coroutine context.
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private lateinit var mentionRegex: Regex
    val commandHandler = CommandHandler()

    override fun onGenericMessage(e: GenericMessageEvent) {
        e.channel.retrieveMessageById(e.messageId).queue(::checkMessage) {
            getLogger().warn("Error whilst retrieving message ID ${e.messageId}.")
        }
    }

    override fun onSlashCommand(e: SlashCommandEvent) {
        val command = commandHandler[e.name]
        if (command === null) {
            return e.reply("This command does not exist.").setEphemeral(true).queue()
        }

        command.onCommand(CommandEvent(
                e.jda,
                e.channel,
                e.channelType,
                e.guild,
                e.isFromGuild,
                null,
                null,
                if (e.channel is PrivateChannel) e.privateChannel else null,
                e.textChannel,
                null,
                e.user,
                e.member,
                arrayOf(),
                true,
                e,
        ))
    }

    private fun checkMessage(message: Message) {
        if (!::mentionRegex.isInitialized) mentionRegex = "^<@(!?)${SELF}> ".toRegex()
        if (message.author.isBot) return

        val reply = message.referencedMessage?.id
        if (reply !== null) {
            val self = message.jda.selfUser.id
            val id = message.referencedMessage!!.author.id
            if (self != id) return
        }

        // Handle AI.
        if (message.contentRaw.contains(mentionRegex) || reply !== null) {
            message.channel.sendTyping().queue()
            val toSend = if (reply !== null) message.contentRaw else
                message.contentRaw.replace(mentionRegex, "")
            launch {
                var (response, bundle) = cleverbot(
                        toSend,
                        message.author.id,
                        reply
                )

                response ?: return@launch message.channel.sendMessage("my brain died, say that again").queue()

                if (DEBUG_MODE) response = "[debug] $response"

                message.reply(response).queue {
                    bundle!!.setLastId(it.id)
                }
            }

            return
        }

        if (!message.contentRaw.startsWith(PREFIX)) return

        val splitInput = message.contentRaw.substring(PREFIX.length).split("\\s+".toRegex())
        val commandName = splitInput[0]
        val args = splitInput.toTypedArray().copyOfRange(1, splitInput.size)
        if (commandName.startsWith(" ")) return

        val command = commandHandler[commandName]
        if (command === null) return

        if ((command.category == Command.Category.OWNER)
                && OWNER != message.author.id
        ) {
            return message.channel.sendMessage("**Aww look, you have achieved comedy. No.**").queue()
        } else if (args.size < command.minArgs) {
            return message.channel.sendMessage("**Usage:** ${command.usage}").queue()
        } else command.onCommand(
                CommandEvent(
                        message.jda,
                        message.channel,
                        message.channelType,
                        if (message.isFromGuild) message.guild else null,
                        message.isFromGuild,
                        message.id,
                        message.idLong,
                        if (message.channelType == ChannelType.PRIVATE) message.privateChannel else null,
                        if (message.channelType == ChannelType.TEXT) message.textChannel else null,
                        message,
                        message.author,
                        message.member,
                        args,
                )
        )
    }
}