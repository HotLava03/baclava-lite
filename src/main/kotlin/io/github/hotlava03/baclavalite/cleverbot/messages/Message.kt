package io.github.hotlava03.baclavalite.cleverbot.messages

/**
 * Represents a single message. The sender
 * can either be the bot or the user.
 */
data class Message(
        val sender: MessageSender,
        val content: String,
        var timestamp: Long?,
        var correspondingId: String?,
)