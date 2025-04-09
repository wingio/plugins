package xyz.wingio.plugins


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Utils.createCheckedSetting
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R
class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        val ctx = requireContext()


        val a = createCheckedSetting(
            ctx,
            CheckedSetting.ViewType.SWITCH,
            "Replace the Share option with Copy Url",
            "Not compatible with the others options"
        )

        val b =
            createCheckedSetting(
                ctx,
                CheckedSetting.ViewType.SWITCH,
                "Hide Share option",
                "Not compatible with Replace option"
            )

        val c = createCheckedSetting(
            ctx,
            CheckedSetting.ViewType.SWITCH,
            "Adds a Copy Url option",
            "Not compatible with Replace Share option"
        )

        TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Addition).run {
            text = "You will need to restart Aliucord for changes to effect."
            addView(this)
        }



        a.isChecked = settings.getBool("replaceShare", true)
        a.setOnCheckedListener { aBoolean: Boolean? ->
            settings.setBool(
                "replaceShare",
                aBoolean!!
            )
            if (aBoolean) {
                settings.setBool("hideShare", false);  if (b.isChecked) {b.toggle()}
                ; settings.setBool(
                    "addCopyUrl",
                    false
                );    if (c.isChecked) {c.toggle()}

            }


        }
        b.isChecked = settings.getBool("hideShare", false)
        b.setOnCheckedListener { aBoolean: Boolean? ->
            settings.setBool("hideShare", aBoolean!!)
            if (aBoolean) {
                settings.setBool("replaceShare", false);
                if (a.isChecked) {a.toggle()}

            }


        }

        c.isChecked = settings.getBool("addCopyUrl", false)
        c.setOnCheckedListener { aBoolean: Boolean ->
            settings.setBool("addCopyUrl", aBoolean)
            if (aBoolean) {
                settings.setBool("replaceShare", false);
                if (a.isChecked) {a.toggle()}
            }


        }


        addView(a)
        addView(b)
        addView(c)

    }
}




