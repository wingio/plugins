@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS")

package xyz.wingio.plugins.morehighlight

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.aliucord.Utils.createCheckedSetting
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.utils.MDUtils
import com.aliucord.views.Divider
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R

private val INFO_TEXT = """
    **Currently supports:**
    
    **Ported**
    - **Headers** (# Header 1, ## Header 2, ### Header 3)
    - **Subtext** (-# tiny greyed out text)
    - **Bullets** (* bulletpoint or - bulletpoint)
    - **Slash Commands** (</command:id>, *ex. </airhorn:816437322781949972>*)
    
    **Custom**
    - **Reddit** (<r/[Subreddit]>, <u/[User]>, *ex. <r/Aliucord>*)
    - **Github** (<username/repo#issue> <gh:username/repo>, *ex. <Aliucord/Aliucord#127>*)
    - **Plugin Settings** (ac://[Plugin Name], *ex. ac://MoreHighlight*)
    - **Colors** (*ex. #1f8b4c*)
""".trimIndent()

@SuppressLint("SetTextI18n")
class PluginSettings(
    plugin: MoreHighlight
): SettingsPage() {

    private val settings: SettingsAPI = plugin.settings
    private val padding = 16.dp

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        setActionBarTitle("MoreHighlight")
        setActionBarSubtitle("Settings")
        setPadding(0)

        val ctx = view.context
        val layout = linearLayout.apply { setPadding(0, 0, 0, 0) }
        val currentScale = (settings.getFloat(MoreHighlight.PREF_HEADER_SIZE, 1.0f) * 100).toInt()

        layout.addView(
            createSlider(
                context = ctx,
                label = "Header Size Scale",
                subtext = "Adjust the size of headers (100% is default)",
                maxValue = 200,
                defaultValue = currentScale,
                valuePostfix = "%",
                onSeekChanged = {
                    settings.setFloat(MoreHighlight.PREF_HEADER_SIZE, it / 100f)
                }
            )
        )

        layout.addView(
            createSwitch(
                context = ctx,
                sets = settings,
                key = MoreHighlight.PREF_SHOW_REPO_NAME,
                label = "Show repo name in issue/pr link",
                subtext = null,
                defaultValue = false
            )
        )

        layout.addView(Divider(ctx))

        TextView(ctx, null, 0, R.i.UiKit_TextView).apply {
            text = MDUtils.render(INFO_TEXT)
            setPadding(padding, padding, padding, 0)
            layout.addView(this)
        }
    }

    @Suppress("SameParameterValue")
    private fun createSwitch(
        context: Context,
        sets: SettingsAPI,
        key: String,
        label: String,
        subtext: String?,
        defaultValue: Boolean
    ): CheckedSetting {
        return createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext).apply {
            l.b().run { setPadding(0, padding, padding, padding) }
            isChecked = sets.getBool(key, defaultValue)
            setPadding(0, 0, 0, 0)
            setOnCheckedListener { checked: Boolean? ->
                sets.setBool(key, checked!!)
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun createSlider(
        context: Context,
        label: String,
        subtext: String?,
        maxValue: Int,
        defaultValue: Int,
        onSeekChanged: (Int) -> Unit,
        valuePostfix: String = ""
    ): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padding, padding, padding, padding)

            TextView(context, null, 0, R.i.UiKit_Settings_Item_Label).apply {
                text = label
                setPadding(0, 0, 0, 8.dp)
                addView(this)
            }

            TextView(context, null, 0, R.i.UiKit_Settings_Item_SubText).apply {
                text = subtext
                setPadding(0, 0, 0, 8.dp)
                addView(this)
            }

            val headerSizeSlider = SeekBar(context).apply {
                max = maxValue
                progress = defaultValue
                setPadding(0, 8.dp, 0, 0)
                addView(this)
            }

            val headerSizeValue = TextView(context, null, 0, R.i.UiKit_TextView).apply {
                text = "$defaultValue$valuePostfix"
                setPadding(0, 4.dp, 0, 0)
                addView(this)
            }

            headerSizeSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    onSeekChanged(progress)
                    headerSizeValue.text = "$progress$valuePostfix"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        }
    }

}
