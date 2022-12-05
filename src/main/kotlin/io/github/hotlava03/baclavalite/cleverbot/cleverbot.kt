@file:JvmName("CleverBot")

package io.github.hotlava03.baclavalite.cleverbot

import io.github.hotlava03.baclavalite.cleverbot.messages.MessageBundle
import io.github.hotlava03.baclavalite.functions.getLogger
import io.github.hotlava03.baclavalite.util.DEBUG_MODE

const val CONVERSATION_TIMEOUT = 600000L

private val conversationData = ConversationData(CONVERSATION_TIMEOUT)
private val wrapper = CleverBotWrapper()

suspend fun cleverbot(stimulus: String, userId: String, currentMessageId: String?): Pair<String?, MessageBundle?> {
    val ctx = conversationData.messageContext(currentMessageId, userId)

    val response = wrapper.makeRequest(stimulus, ctx)
    var bundle: MessageBundle? = null
    if (response !== null) {
        val providedBundle = conversationData.registerUserMessage(userId, stimulus, currentMessageId)
        bundle = conversationData.pushContext(userId, response, currentMessageId, providedBundle)
    }

    if (DEBUG_MODE) {
        if (bundle === null) getLogger().info("[debug] $userId initialized a conversation.")
        else getLogger().info("[debug] $userId replied to conversation ${bundle.messages[0].correspondingId}:\n$bundle")
    }
    return response to bundle
}
