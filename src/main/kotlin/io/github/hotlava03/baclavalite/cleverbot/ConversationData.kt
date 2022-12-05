package io.github.hotlava03.baclavalite.cleverbot

import io.github.hotlava03.baclavalite.cleverbot.messages.Message
import io.github.hotlava03.baclavalite.cleverbot.messages.MessageBundle
import io.github.hotlava03.baclavalite.cleverbot.messages.MessageSender
import io.ktor.util.date.*
import net.dv8tion.jda.api.entities.User
import java.util.Optional

open class ConversationData(private val timeout: Long) {
    private val conversationData: MutableList<MessageBundle> = mutableListOf()

    fun messageContext(involvedMessageId: String?, userId: String): List<String> {
        // Get the user context for this message.
        var bundle = if (involvedMessageId === null) {
            MessageBundle(arrayOf(), arrayOf(userId))
        } else {
            findByPreviousMessage(involvedMessageId).orElse(MessageBundle(arrayOf(), arrayOf(userId)))
        }


        // Get the previous conversation's timestamp.
        val now = getTimeMillis()
        val oldTimestamp = bundle.messages.lastOrNull()?.timestamp ?: now

        // Check if it's greater than the timeout (default is 10 minutes).
        if (now - oldTimestamp > timeout) {
            val (_, involvedUsers) = bundle
            delete(bundle)
            bundle = MessageBundle(arrayOf(), involvedUsers)
        }

        // Routine check for other conversations
        conversationData.filter { now - it.dateCreated!! > timeout }.forEach { delete(it) }

        return bundle.messages.map { it.content }
    }

    fun registerUserMessage(userId: String, message: String, messageId: String?): MessageBundle {
        val messageObj = Message(
                sender = MessageSender.USER,
                content = message,
                timestamp = System.currentTimeMillis(),
                messageId,
        )
        val bundle = if (messageId === null) {
            MessageBundle(arrayOf(), arrayOf(userId), System.currentTimeMillis())
        } else {
            findByPreviousMessage(messageId).orElse(MessageBundle(arrayOf(), arrayOf(userId)))
        }
        bundle.messages = arrayOf(*bundle.messages, messageObj)
        if (userId !in bundle.involvedUsers) bundle.involvedUsers = arrayOf(*bundle.involvedUsers, userId)

        save(bundle)
        return bundle
    }

    fun pushContext(userId: String, message: String, messageId: String?, providedBundle: MessageBundle): MessageBundle {
        val messageObj = Message(
                sender = MessageSender.BOT,
                content = message,
                timestamp = System.currentTimeMillis(),
                messageId,
        )

        providedBundle.messages = arrayOf(*providedBundle.messages, messageObj)
        if (userId !in providedBundle.involvedUsers) providedBundle.involvedUsers = arrayOf(*providedBundle.involvedUsers, userId)
        save(providedBundle)
        return providedBundle
    }

    private fun findByPreviousMessage(messageId: String): Optional<MessageBundle> {
        var bundle: MessageBundle? = null
        for (convo in conversationData) {
            for (message in convo.messages) {
                if (message.correspondingId == messageId) {
                    bundle = convo
                    break
                }
            }
        }

        bundle ?: return Optional.empty<MessageBundle>()
        return Optional.of<MessageBundle>(bundle)
    }

    private fun delete(bundle: MessageBundle) {
        conversationData.remove(bundle)
    }

    private fun save(bundle: MessageBundle) {
        val found = conversationData.indexOf(bundle)
        if (found > -1) conversationData[found] = bundle
        else conversationData.add(bundle)
    }
}