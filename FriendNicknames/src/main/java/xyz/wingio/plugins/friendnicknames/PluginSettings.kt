@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS")

package xyz.wingio.plugins.friendnicknames

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aliucord.PluginManager
import com.aliucord.Utils.createCheckedSetting
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.LinearLayout
import com.discord.app.AppBottomSheet
import com.discord.utilities.color.ColorCompat
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R

class PluginSettings: AppBottomSheet() {

    override fun getContentViewResId() = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settings = PluginManager.plugins["FriendNicknames"]!!.settings
        val context = inflater.context
        val layout = LinearLayout(context).apply {
            setBackgroundColor(ColorCompat.getThemedColor(context, R.b.colorBackgroundPrimary))
        }

        layout.addView(
            createSwitch(
                context = context,
                sets = settings,
                key = "showUsername",
                label = "Show Username",
                subtext = "Adds the username in parenthesis after the nickname",
                defaultValue = false
            )
        )
        return layout
    }

    @Suppress("SameParameterValue")
    private fun createSwitch(
        context: Context,
        sets: SettingsAPI,
        key: String,
        label: String,
        subtext: String,
        defaultValue: Boolean
    ): CheckedSetting {
        val cs = createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext)
        cs.isChecked = sets.getBool(key, defaultValue)
        cs.setOnCheckedListener { c: Boolean? ->
            sets.setBool(
                key,
                c!!
            )
        }
        return cs
    }
}