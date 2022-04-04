@file:JvmName("GlobalFunctions")
package io.github.hotlava03.baclavalite.functions

import io.github.hotlava03.baclavalite.BaclavaLite
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

fun getLogger(): Logger {
    return LogManager.getLogger(BaclavaLite::class.java)
}

fun currentClassLoader() : ClassLoader {
    return Thread.currentThread().contextClassLoader
}
