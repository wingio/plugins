@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS")

package xyz.wingio.plugins.betterchannelicons

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toolbar

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils.defaultPadding
import com.aliucord.views.Button
import com.aliucord.views.ToolbarButton

import com.lytefast.flexinput.R

import xyz.wingio.plugins.betterchannelicons.recycler.IconListAdapter

class PluginSettings(private val settings: SettingsAPI) : SettingsPage() {

    private val settingsId = View.generateViewId()

    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)
        val ctx = view.context
        val layout = linearLayout
        val icons: MutableMap<String, String> = settings.getObject("icons", HashMap(), Utils.iconStoreType)

        setActionBarTitle("BetterChannelIcons")

        Button(ctx).apply {
            text = "Add Icon"

            setOnClickListener {
                AddChannelSheet(this@PluginSettings, settings).show(
                    parentFragmentManager, "add_channel_sheet"
                )
            }
            layout.addView(this)
        }

        RecyclerView(ctx).apply {
            layoutManager = LinearLayoutManager(ctx)
            adapter = IconListAdapter(this@PluginSettings, icons)

            layout.addView(this)
        }

        val toolbarButtons = LinearLayout(ctx).apply {
            id = settingsId
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setHorizontalGravity(Gravity.END)

            addView(
                ToolbarButton(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        Toolbar.LayoutParams.WRAP_CONTENT,
                        Toolbar.LayoutParams.WRAP_CONTENT
                    ).apply { marginEnd = defaultPadding }

                    setImageDrawable(ContextCompat.getDrawable(ctx, R.e.ic_guild_settings_24dp))
                    setOnClickListener {
                        SettingsSheet(settings).show(parentFragmentManager, "Settings")
                    }
                }
            )
        }

        val toolbar = headerBar as ViewGroup
        if (toolbar.findViewById<View?>(settingsId) == null) toolbar.addView(toolbarButtons)
    }

}