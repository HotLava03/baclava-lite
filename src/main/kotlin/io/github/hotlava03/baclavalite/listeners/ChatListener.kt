package io.github.hotlava03.baclavalite.listeners

import io.github.hotlava03.baclavalite.util.simplifyMessage
import io.github.hotlava03.baclavalite.cleverbot.cleverbot
import io.github.hotlava03.baclavalite.commands.Command
import io.github.hotlava03.baclavalite.commands.CommandEvent
import io.github.hotlava03.baclavalite.commands.CommandHandler
import io.github.hotlava03.baclavalite.functions.getLogger
import io.github.hotlava03.baclavalite.util.OWNER
import io.github.hotlava03.baclavalite.util.PREFIX
import io.github.hotlava03.baclavalite.util.SELF
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.GenericMessageEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.coroutines.CoroutineContext

class ChatListener : ListenerAdapter(), CoroutineScope {
    // Initialize Coroutine context.
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job
    // Storage containing last message from AI chat per-user (userId => msgId).
    private val lastMessages: MutableMap<String, String> = HashMap()

    private lateinit var mentionRegex: Regex
    private val commandHandler = CommandHandler()

    override fun onGenericMessage(e: GenericMessageEvent) {
        e.channel.retrieveMessageById(e.messageId).queue(::checkMessage) {
            getLogger().warn("Error whilst retrieving message ID ${e.messageId}.")
        }
    }

    private fun checkMessage(message: Message) {
        if (!::mentionRegex.isInitialized) mentionRegex = "^<@(!?)${SELF}> ".toRegex()
        if (message.author.isBot) return

        val reply = message.referencedMessage?.id
        val lastMessage = lastMessages[message.author.id]

        // Handle AI.
        if (message.contentRaw.contains(mentionRegex) || (reply == lastMessage && reply !== null)) {
            message.channel.sendTyping().queue()
            launch {
                val response = cleverbot(
                        message.contentRaw.replace(mentionRegex, "").substring(1),
                        message.author
                ) ?: return@launch message.channel.sendMessage("my brain died, say that again").queue()

                message.reply(simplifyMessage(response)).queue {
                    lastMessages[message.author.id] = it.id
                }
            }

            return
        } else if (reply !== null && message.referencedMessage?.author?.id == SELF) {
            // It's an invalid reply.
            message.reply("**Invalid reply. " +
                    "Replying only works on my latest sent message to you.**").queue()
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