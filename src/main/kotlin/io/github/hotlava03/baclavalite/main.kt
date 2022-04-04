@file:JvmName("Main")
package io.github.hotlava03.baclavalite

import io.github.hotlava03.baclavalite.util.initKotlinEvalEngine

fun main() {
    val token = System.getenv("TOKEN")
    if (token === null) return println("You must set the bot's token " +
            "in the TOKEN environment variable. Aborting...")
    initKotlinEvalEngine()
    val bot = BaclavaLite()
    bot.start(token)
}
