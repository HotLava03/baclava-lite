@file:JvmName("GlobalFunctions")
package io.github.hotlava03.baclavalite.functions

import io.github.hotlava03.baclavalite.BaclavaLite
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun getLogger(): Logger {
    return LoggerFactory.getLogger(BaclavaLite::class.java)
}

fun currentClassLoader() : ClassLoader {
    return Thread.currentThread().contextClassLoader
}
