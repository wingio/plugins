package xyz.wingio.plugins.usertags

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin

@AliucordPlugin
class UserTags: Plugin() {

    override fun start(context: Context) {
        patcher.patchTagInMessage()
        patcher.patchTagInMemberList()
        patcher.patchTagInProfile()

        commands.registerCommand()
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }

    companion object {
        const val DEV_ID = 298295889720770563L
    }

}
