package io.github.hotlava03.baclavalite.commands.utility

import com.deepl.api.Language
import com.deepl.api.LanguageType
import com.deepl.api.Translator
import io.github.hotlava03.baclavalite.commands.Command
import io.github.hotlava03.baclavalite.commands.CommandEvent
import io.github.hotlava03.baclavalite.functions.getLogger
import java.lang.IllegalArgumentException

class Translate : Command() {
    private val apiKey: String?
    private var translator: Translator? = null
    private val sourceLanguages: List<Language>?
    private val targetLanguages: List<Language>?

    private val languageAliases = mapOf(
        "english" to "English (American)",
        "portuguese" to "Portuguese (European)",
        "chinese" to "Chinese (simplified)",
    )

    init {
        name = "translate"
        category = Category.UTILITY
        description = "Translate some text."
        aliases = arrayOf()
        usage = "<source language> <target language> <text...>"
        minArgs = 3
        apiKey = System.getenv("DEEPL_API_KEY")
        try {
            translator = Translator(apiKey)
        } catch (ex: IllegalArgumentException) {
            getLogger().warn("The current DeepL API key is not valid. Translate disabled.")
        }
        sourceLanguages = translator?.getLanguages(LanguageType.Source)
        targetLanguages = translator?.getLanguages(LanguageType.Target)
    }

    override fun onCommand(e: CommandEvent) {
        if (apiKey === null || translator === null) {
            return e.reply("**This feature is currently disabled.**")
        } else if (sourceLanguages === null || targetLanguages === null) {
            return e.reply("**Could not retrieve languages.**")
        }

        val sourceLanguageStr = e.args[0]
        val targetLanguageStr = e.args[1]
        val sourceLanguage = sourceLanguages.find { it.name.lowercase() == sourceLanguageStr.lowercase() }
        val targetLanguage = targetLanguages.find { it.name.lowercase() == targetLanguageStr.lowercase() }
                ?: findTargetLanguageByAlias(targetLanguageStr)

        if (sourceLanguage === null) {
            return e.reply("**Invalid source language.**")
        } else if (targetLanguage === null) {
            return e.reply("**Invalid target language.**")
        }

        val text = e.args.copyOfRange(2, e.args.size).joinToString(" ")
        val result = translator?.translateText(text, sourceLanguage, targetLanguage)

        if (result === null) {
            return e.reply("**Could not translate.**")
        }

        e.reply("**Translation result:** ${result?.text}")
    }

    private fun findTargetLanguageByAlias(alias: String): Language? {
        return targetLanguages?.find { it.name == languageAliases[alias.lowercase()] }
    }
}
