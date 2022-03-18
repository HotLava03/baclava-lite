package io.github.hotlava03.baclavalite.commands

import io.github.hotlava03.baclavalite.util.PREFIX

abstract class Command {
    // Command properties.
    lateinit var name: String
        protected set
    lateinit var category: Category
        protected set
    var description: String? = null
        protected set
    var aliases: Array<String> = arrayOf()
        protected set
    var usage: String? = null
        protected set(usage) {
            field = "$PREFIX$name $usage"
        }
        get() {
            return if (field === null) PREFIX + name
            else field
        }
    var minArgs: Int = 0

    // Categories.
    enum class Category {
        BASIC,
        UTILITY,
        OWNER,;

        fun format() = toString()[0] + toString().lowercase().substring(1)
    }

    abstract fun onCommand(e: CommandEvent)
}
