@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS")

package xyz.wingio.plugins.betterchannelicons

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

import com.aliucord.Utils.getResId
import com.aliucord.Utils.showToast
import com.aliucord.Utils.tintToTheme
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.views.Button
import com.aliucord.views.TextInput
import com.aliucord.widgets.BottomSheet

import com.lytefast.flexinput.R

class IconListSheet(
    private val settings: SettingsAPI,
    private val addChannelSheet: AddChannelSheet
) : BottomSheet() {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        val isAdvanced = settings.getBool("advanced_mode", false)
        val ctx = requireContext()

        setPadding(16.dp)

        val items = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
        }

        if (isAdvanced) {
            val drawableNameInput = TextInput(ctx).apply {
                editText.setText("")
                setHint("Drawable name")
                setPadding(16.dp)
                this@IconListSheet.addView(this)
            }

            Button(ctx).apply {
                text = "Use Icon"
                setOnClickListener {
                    val iconName = drawableNameInput.editText.text.toString()
                    if (iconName.isNotEmpty()) {
                        returnAndSetIcon(iconName)
                    }
                }
                this@IconListSheet.addView(this)
            }
        } else {
            setPadding(0)

            for ((iconName, iconId) in Constants.presetIconMap) {
                val icon = tintToTheme(ContextCompat.getDrawable(
                    /* context = */ ctx,
                    /* id = */ getResId(
                        name = iconId,
                        type = "drawable"
                    )
                )!!.mutate())

                TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                    text = iconName
                    setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
                    setOnClickListener { returnAndSetIcon(iconId) }
                    items.addView(this)
                }
            }
        }

        addView(items)
    }

    private fun returnAndSetIcon(icon: String) {
        val resources = requireContext().resources

        try {
            ResourcesCompat.getDrawable(resources, getResId(icon, "drawable"), null)
        } catch (e: Throwable) {
            showToast("Icon not found", false)
            return
        }

        addChannelSheet.setCurrentIcon(icon)
        dismiss()
    }

}