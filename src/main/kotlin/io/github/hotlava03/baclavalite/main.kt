@file:JvmName("Main")
package io.github.hotlava03.baclavalite

fun main() {
    val token = System.getenv("TOKEN")
    if (token === null) return println("You must set the bot's token " +
            "in the TOKEN environment variable. Aborting...")

    val bot = BaclavaLite()
    bot.start(token)
}
