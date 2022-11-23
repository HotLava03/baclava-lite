package io.github.hotlava03.baclavalite.commands

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import java.util.function.Consumer


class CommandEvent(
    val jda: JDA,
    val channel: MessageChannel,
    val channelType: ChannelType,
    val guild: Guild?,
    val isFromGuild: Boolean,
    val messageId: String?,
    val messageIdLong: Long?,
    val privateChannel: PrivateChannel?,
    val textChannel: TextChannel?,
    val message: Message?,
    val author: User,
    val member: Member?,
    val args: Array<String>,
    val isFromSlashCommand: Boolean = false,
    val slashCommandEvent: SlashCommandEvent? = null
) {
    fun reply(text: CharSequence, callback: Consumer<Message>? = null) {
        val txt = text.replace(jda.token.toRegex(), "censored")
        if (isFromSlashCommand) {
            slashCommandEvent!!.reply(txt).queue()
        } else {
            channel.sendMessage(txt).queue(callback)
        }
    }

    fun reply(embed: MessageEmbed, callback: Consumer<Message>? = null) {
        if (isFromSlashCommand) {
            slashCommandEvent!!.reply(MessageBuilder().setEmbeds(embed).build()).queue()
        } else {
            channel.sendMessageEmbeds(embed).queue(callback)
        }
    }

    fun reply(message: Message, callback: Consumer<Message>? = null) {
        if (isFromSlashCommand) {
            slashCommandEvent!!.reply(message).queue()
        } else {
            channel.sendMessage(message).queue(callback)
        }
    }
}