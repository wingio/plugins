package xyz.wingio.plugins.friendnicknames

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.component1
import com.aliucord.patcher.component2
import com.discord.api.channel.Channel
import com.discord.models.member.GuildMember
import com.discord.models.user.User

@AliucordPlugin
class FriendNicknames: Plugin() {

    private lateinit var pluginIcon: Drawable

    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET)
        needsResources = true
    }

    override fun start(context: Context?) {
        pluginIcon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_editfriend", "drawable", "com.aliucord.plugins"), null)!!
        registerCommand(commands, settings)

        patcher.after<GuildMember.Companion>("getNickOrUsername", User::class.java, GuildMember::class.java, Channel::class.java, List::class.java) { (callFrame, user: User) ->
            var nickname = settings.getString(user.id.toString(), null) ?: return@after
            val showUsername = settings.getBool("showUsername", false)

            if (showUsername) nickname += " (${user.username})"
            callFrame.result = nickname
        }
    }

    override fun stop(context: Context?) = patcher.unpatchAll()

}