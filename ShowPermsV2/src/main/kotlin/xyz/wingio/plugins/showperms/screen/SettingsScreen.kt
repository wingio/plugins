@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS", "MISSING_DEPENDENCY_SUPERCLASS_WARNING")

package xyz.wingio.plugins.showperms.screen

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.views.Divider
import com.discord.views.CheckedSetting
import com.discord.views.RadioManager
import com.lytefast.flexinput.R
import xyz.wingio.plugins.showperms.util.Settings

@Suppress("SetTextI18n")
class SettingsScreen: SettingsPage() {

    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("ShowPerms")
        setActionBarSubtitle("Settings")
        setPadding(0)

        val ctx = view.context
        val layout = linearLayout.apply { setPadding(0, 0, 0, 0) }

        layout.addView(
            createSwitch(
                context = ctx,
                label = "Show Dot",
                sublabel = "Display a colored dot in the permission chip to indicate the role that grants this permission",
                default = Settings.showDot
            ) { Settings.showDot = it }
        )

        layout.addView(
            createSwitch(
                context = ctx,
                label = "Show Role Count",
                sublabel = "Display the number of roles a user has next to the server name",
                default = Settings.showRoleCount
            ) { Settings.showRoleCount = it }
        )

        layout.addView(
            createSwitch(
                context = ctx,
                label = "Apply Channel Overwrites",
                sublabel = "Apply channel-specific permission overwrites. Keep off to show global permissions",
                default = Settings.applyOverwrites
            ) { Settings.applyOverwrites = it }
        )

        layout.addView(Divider(ctx))

        TextView(context, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = "Format"
            setPadding(16.dp, 24.dp, 16.dp, 16.dp)
            layout.addView(this)
        }

        layout.createRadioGroup(
            labels = mapOf(
                "Default" to null,
                "Show All Permissions for Admin" to "Show every permission if at least one of the roles has admin permissions.",
                "Show Only Admin" to "If the user is admin only show the Administrator permission"
            ),
            default = Settings.format
        ) {
            Settings.format = it
        }

        layout.addView(Divider(ctx))

        TextView(context, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = "Sort by Name"
            setPadding(16.dp, 24.dp, 16.dp, 16.dp)
            layout.addView(this)
        }

        layout.createRadioGroup(
            labels = mapOf(
                "A to Z" to null,
                "Z to A" to null,
            ),
            default = Settings.permissionNameSort
        ) {
            Settings.permissionNameSort = it
        }

        layout.addView(Divider(ctx))

        TextView(context, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = "Sort by Role Position"
            setPadding(16.dp, 24.dp, 16.dp, 16.dp)
            layout.addView(this)
        }

        layout.createRadioGroup(
            labels = mapOf(
                "Lowest to Highest" to null,
                "Highest to Lowest" to null,
            ),
            default = Settings.rolePosSort
        ) {
            Settings.rolePosSort = it
        }
    }

    private fun createSwitch(
        context: Context,
        label: String,
        sublabel: String,
        default: Boolean,
        onChecked: (Boolean) -> Unit
    ): CheckedSetting {
        return Utils.createCheckedSetting(
            context = context,
            type = CheckedSetting.ViewType.SWITCH,
            text = label,
            subtext = sublabel
        ).apply {
            l.b().run { setPadding(0, 16.dp, 16.dp, 16.dp) }
            isChecked = default
            setPadding(0, 0, 0, 0)
            setOnCheckedListener(onChecked)
        }
    }

    private fun createRadio(
        context: Context,
        label: String,
        sublabel: String?,
        default: Boolean
    ): CheckedSetting {
        return Utils.createCheckedSetting(
            context = context,
            type = CheckedSetting.ViewType.RADIO,
            text = label,
            subtext = sublabel
        ).apply {
            l.b().run { setPadding(0, 16.dp, 16.dp, 16.dp) }
            isChecked = default
            setPadding(0, 0, 0, 0)
        }
    }

    private inline fun <reified E: Enum<E>> LinearLayout.createRadioGroup(
        labels: Map<String, String?>,
        default: E,
        crossinline onSelected: (E) -> Unit
    ) {
        val radios = enumValues<E>().map { labels.entries.toList()[it.ordinal] }.mapIndexed { i, (label, sublabel) ->
            createRadio(context, label, sublabel, i == default.ordinal)
        }

        val radioManager = RadioManager(radios)

        for (i in radios.indices) {
            val radio = radios[i]
            radio.e {
                onSelected(enumValues<E>()[i])
                radioManager.a(radio)
            }
            addView(radio)
            if (i == default.ordinal) radioManager.a(radio)
        }
    }

}