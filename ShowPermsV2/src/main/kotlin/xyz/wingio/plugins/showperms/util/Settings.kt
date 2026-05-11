package xyz.wingio.plugins.showperms.util

import com.aliucord.PluginManager

object Settings {

    private val settings by lazy { PluginManager.plugins["ShowPermsV2"]!!.settings }

    var format by settings.enum(Format.Default)

    // Sort
    var permissionNameSort by settings.enum(SortOrder.Ascending)
    var rolePosSort by settings.enum(SortOrder.Descending)

    // Toggles
    var showDot by settings.boolean(true)
    var showRoleCount by settings.boolean(true)
    var applyOverwrites by settings.boolean(false)

    fun migrate() {
        if (settings.exists("invertOrder")) {
            rolePosSort = if (settings.getBool("invertOrder", false)) SortOrder.Ascending else SortOrder.Descending
            settings.remove("invertOrder")
        }
    }

}

enum class Format {
    Default,
    FullAdmin,
    MinAdmin
}

enum class SortOrder {
    Ascending,
    Descending
}