package xyz.wingio.plugins

import android.content.Context
import android.graphics.drawable.Drawable

import androidx.core.content.ContextCompat
import com.aliucord.Logger

import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin

import com.lytefast.flexinput.R

import xyz.wingio.plugins.betterchannelicons.Patches
import xyz.wingio.plugins.betterchannelicons.PluginSettings

@AliucordPlugin
class BetterChannelIcons : Plugin() {

    private var pluginIcon: Drawable? = null

    companion object {
        val logger = Logger("BetterChannelIcons")
        lateinit var pluginSettings: SettingsAPI
    }

    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
        needsResources = true
    }

    override fun start(context: Context) {
        pluginSettings = settings
        pluginIcon = ContextCompat.getDrawable(context, R.e.ic_channel_text_white_a60_24dp)

        val patches = Patches(patcher)

        patches.addChannelAction()
        patches.setVoiceIcon()
        patches.setTextIcon(resources)
        patches.setToolbarIcon(resources)
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }

}