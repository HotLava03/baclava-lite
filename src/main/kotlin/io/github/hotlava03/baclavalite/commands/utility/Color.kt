package io.github.hotlava03.baclavalite.commands.utility

import io.github.hotlava03.baclavalite.commands.Command
import io.github.hotlava03.baclavalite.commands.CommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

// Because Color is this class' name.
typealias AwtColor = java.awt.Color

const val INVALID_COLOR_MSG = "**Invalid color.** Examples:\n**HEX:** #FF00FF\n**RGB** 255,0,255\n" +
        "*Do not include spaces in RGB.*"

class Color : Command() {

    init {
        name = "color"
        category = Category.UTILITY
        description = "Visualize a color."
        aliases = arrayOf("getcolor", "colour", "getcolour")
        isSlashCommandCompatible = true
        slashCommandOptions = listOf(
                OptionData(OptionType.STRING, "color", "HEX or RGB for the color.", true)
        )
        usage = "<hex|rgb>"
        minArgs = 1
    }

    override fun onCommand(e: CommandEvent) {
        val input = if (isSlashCommandCompatible) e.slashCommandEvent!!.getOption("color")?.asString
            else e.args[0]
        if (input === null) return e.reply("Missing color code.")
        val color: AwtColor = if (!input.startsWith("#")) {
            val rgb = input.split(",")
            if (rgb.size != 3) return e.reply(INVALID_COLOR_MSG)

            val (red, green, blue) = rgb
            AwtColor(red.toInt(), green.toInt(), blue.toInt())
        } else {
            if (input.length != 7) return e.reply(INVALID_COLOR_MSG)
            AwtColor.decode(input)
        }

        val squareSize = 100

        // Create image with 2D graphics.
        val bufferedImage = BufferedImage(squareSize, squareSize, BufferedImage.TYPE_INT_RGB)
        val g2d = bufferedImage.createGraphics()
        g2d.color = color
        g2d.fillRect(0, 0, squareSize, squareSize)
        g2d.dispose()

        // Convert to byte array.
        val out = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "jpg", out)

        val toRespond = "**HEX:** #${Integer.toHexString(color.rgb).substring(2)} - " +
                "**RGB**: ${color.red},${color.green},${color.blue}"

        if (e.isFromSlashCommand) {
            e.slashCommandEvent!!.reply(toRespond)
                    .addFile(out.toByteArray(), "color.jpg")
                    .queue()
        } else {
            e.channel.sendMessage(toRespond)
                    .addFile(out.toByteArray(), "color.jpg")
                    .queue()
        }
    }
}
