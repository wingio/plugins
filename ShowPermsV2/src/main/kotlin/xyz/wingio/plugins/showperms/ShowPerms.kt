package xyz.wingio.plugins.showperms

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import xyz.wingio.plugins.showperms.patch.addPermissionSection
import xyz.wingio.plugins.showperms.screen.SettingsScreen
import xyz.wingio.plugins.showperms.util.Settings

@AliucordPlugin
class ShowPerms: Plugin() {

    init {
        settingsTab = SettingsTab(SettingsScreen::class.java)
    }

    override fun start(context: Context) {
        Settings.migrate()
        patcher.addPermissionSection()
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }

}