@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS")

package xyz.wingio.plugins.betterchannelicons

import android.content.Context
import android.os.Bundle
import android.view.View

import com.aliucord.Utils.createCheckedSetting
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.BottomSheet

import com.discord.views.CheckedSetting

class SettingsSheet(private val settings: SettingsAPI) : BottomSheet() {

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        val ctx = requireContext()

        addView(
            /* view = */ createSwitch(
                context = ctx,
                sets = settings,
                key = "advanced_mode",
                label = "Advanced Mode",
                subtext = "Allows you to set whatever drawable you want for a channel icon",
                defaultValue = false
            )
        )

        addView(
            /* view = */ createSwitch(
                context = ctx,
                sets = settings,
                key = "setToolbarIcon",
                label = "Set Toolbar Icon",
                subtext = "Change the channel icon in the toolbar",
                defaultValue = true
            )
        )
    }

    private fun createSwitch(
        context: Context,
        sets: SettingsAPI,
        key: String,
        label: String,
        subtext: CharSequence,
        defaultValue: Boolean
    ): CheckedSetting {
        val cs = createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext)
        cs.isChecked = sets.getBool(key, defaultValue)
        cs.setOnCheckedListener { c: Boolean? -> sets.setBool(key, c!!) }
        return cs
    }

}