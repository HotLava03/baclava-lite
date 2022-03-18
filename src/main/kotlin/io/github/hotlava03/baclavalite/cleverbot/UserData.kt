package io.github.hotlava03.baclavalite.cleverbot

import io.github.hotlava03.baclavalite.cleverbot.messages.Message
import io.github.hotlava03.baclavalite.cleverbot.messages.MessageBundle
import io.github.hotlava03.baclavalite.cleverbot.messages.MessageSender
import io.ktor.util.date.*
import net.dv8tion.jda.api.entities.User
import java.util.Optional

open class UserData(private val timeout: Long) {
    private val userDataMap = HashMap<String, MessageBundle>()

    fun userContext(user: User): List<String> {
        // Get the user context for this user.
        var bundle = findById(user.id).orElse(MessageBundle(arrayOf(), user))

        // Get the previous conversation's timestamp.
        val now = getTimeMillis()
        val oldTimestamp = bundle.messages.lastOrNull()?.timestamp ?: now

        // Check if it's greater than the timeout (default is 10 minutes).
        if (now - oldTimestamp > timeout) {
            deleteById(user.id)
            bundle = MessageBundle(arrayOf(), user)
        }

        return bundle.messages.filter { it.sender == MessageSender.BOT }.map { it.content }
    }

    fun registerUserMessage(userId: String, message: String) {
        val bundle = findById(userId).get()
        bundle.messages = arrayOf(*bundle.messages, Message(
            sender = MessageSender.USER,
            content = message,
            timestamp = System.currentTimeMillis(),
        ))

        save(bundle)
    }

    fun pushContext(user: User, message: String) {
        val messageObj = Message(
                sender = MessageSender.BOT,
                content = message,
                timestamp = System.currentTimeMillis(),
        )
        val bundle = findById(user.id).orElse(MessageBundle(arrayOf(messageObj), user))
        bundle.messages = arrayOf(*bundle.messages, messageObj)

        save(bundle)
    }

    private fun findById(id: String): Optional<MessageBundle> {
        val bundle = userDataMap[id]
        bundle ?: return Optional.empty<MessageBundle>()
        return Optional.of<MessageBundle>(bundle)
    }

    private fun deleteById(id: String) {
        userDataMap.remove(id)
    }

    private fun save(bundle: MessageBundle) {
        userDataMap[bundle.id] = bundle
    }
}