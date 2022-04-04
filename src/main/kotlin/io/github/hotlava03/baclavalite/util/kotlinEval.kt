@file:JvmName("KotlinEval")
package io.github.hotlava03.baclavalite.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.requests.RestAction
import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinStandardJsr223ScriptTemplate
import java.io.File
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine

lateinit var engine: ScriptEngine

private val IMPORTS = listOf(
        "io.github.hotlava03.baclavalite.cleverbot.*",
        "io.github.hotlava03.baclavalite.cleverbot.messages.*",
        "io.github.hotlava03.baclavalite.commands.*",
        "io.github.hotlava03.baclavalite.util.*",
        "io.github.hotlava03.baclavalite.*",
        "java.lang.*",
        "java.io.*",
        "java.math.*",
        "java.time.*",
        "java.awt.*",
        "java.awt.image.*",
        "javax.imageio.*",
        "java.time.format.*",
        "kotlinx.coroutines.*",
        "kotlin.reflect.*",
        "kotlin.reflect.jvm.*",
        "kotlin.reflect.full.*",
        "kotlin.system.*",
        "kotlin.io.*",
        "kotlin.random.*",
        "kotlin.concurrent.*",
        "kotlin.properties.*",
)

fun initKotlinEvalEngine() {
    engine = BaclavaKotlinScriptEngineFactory().scriptEngine
}

suspend fun eval(code: String, variables: Map<String, Any>): String = coroutineScope {
    // Get the engine.
    // val engine = ScriptEngineManager().getEngineByExtension("kts")
    for ((key, value) in variables) engine.put(key, value)

    val scriptPrefix = buildString {
        for ((key, value) in engine.getBindings(ScriptContext.ENGINE_SCOPE)) {
            if ("." !in key) {
                val name: String = value.javaClass.name.replace("object", "`object`")
                val bind = """val $key = bindings["$key"] as $name"""
                appendLine(bind)
            }
        }
    }

    val toEval = "import " + IMPORTS.joinToString("\nimport ") + "\n" + "\n" + scriptPrefix + "\n" + code
    try {
        val evaluated: Any? = engine.eval(toEval, engine.context)
        if (evaluated !== null) {
            return@coroutineScope withContext(Dispatchers.Default) {
                when (evaluated) {
                    is RestAction<*> -> {
                        evaluated.queue()
                        "Unit"
                    }
                    is Unit -> "Unit"
                    is String -> "\"${evaluated.replace("\n", "\\n")}\""
                    else -> evaluated.toString()
                }
            }
        }

        return@coroutineScope "null"
    } catch (t: Throwable) {
        val err = t.stackTraceToString()
        return@coroutineScope if (err.length > 1990) t.stackTraceToString().substring(0, 1990)
                .replace("\\s+at .+$".toRegex(), "")
        else err
    }
}

internal class BaclavaKotlinScriptEngineFactory : KotlinJsr223JvmScriptEngineFactoryBase() {
    override fun getScriptEngine(): ScriptEngine {
        return KotlinJsr223JvmLocalScriptEngine(
                this,
                listOf(getJarFile()),
                KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
                { ctx, types -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray()) },
                arrayOf(Bindings::class)
        )
    }

    private fun getJarFile(): File {
        return File(BaclavaKotlinScriptEngineFactory::class.java
                .protectionDomain.codeSource.location.toURI())
    }

}
