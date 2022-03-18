@file:JvmName("CleverBot")
package io.github.hotlava03.baclavalite.cleverbot

import net.dv8tion.jda.api.entities.User

const val CONVERSATION_TIMEOUT = 600000L

private val userData = UserData(CONVERSATION_TIMEOUT)
private val wrapper = CleverBotWrapper(userData)

suspend fun cleverbot(stimulus: String, user: User): String? {
    val ctx = userData.userContext(user)

    val response = wrapper.makeRequest(stimulus, ctx)
    if (response !== null) {
        userData.pushContext(user, response)
        userData.registerUserMessage(user.id, stimulus)
    }
    return response
}
