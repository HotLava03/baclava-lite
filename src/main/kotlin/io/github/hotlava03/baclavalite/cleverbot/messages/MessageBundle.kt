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
        var involvedUsers: Array<String>,
        var dateCreated: Long? = null
) : Serializable {
    fun setLastId(id: String) {
        val last = messages[messages.size - 1]
        val secondLast = messages[messages.size - 2]

        last.correspondingId = id
        secondLast.correspondingId = id

        messages[messages.size - 1] = last
        messages[messages.size - 2] = secondLast
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessageBundle

        if (!messages.contentEquals(other.messages)) return false

        return true
    }

    override fun hashCode(): Int {
        return messages.contentHashCode()
    }

    override fun toString(): String {
        return buildString {
            append("Message history:\n")
            append(messages.joinToString(separator = "\n- ", prefix = "- "))
            append("\nInvolved users: ")
            append(involvedUsers.joinToString())
        }
    }
}