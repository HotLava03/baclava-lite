package io.github.hotlava03.baclavalite.cleverbot.messages

import net.dv8tion.jda.api.entities.User
import java.io.Serializable

/**
 * What is sent to the client when all messages
 * are requested.
 *
 * Also serves as a model for the Redis database.
 */
data class MessageBundle(
        var messages: Array<Message>,
        var user: User,
) : Serializable {
    var id: String = user.id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessageBundle

        if (!messages.contentEquals(other.messages)) return false
        if (user != other.user) return false

        return true
    }

    override fun hashCode(): Int {
        var result = messages.contentHashCode()
        result = 31 * result + user.hashCode()
        return result
    }
}