@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS")

package xyz.wingio.plugins.betterchannelicons

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast

import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

import com.aliucord.Utils.getResId
import com.aliucord.Utils.tintToTheme
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.views.Button
import com.aliucord.views.TextInput
import com.aliucord.widgets.BottomSheet

import com.lytefast.flexinput.R

import xyz.wingio.plugins.BetterChannelIcons

import java.util.Locale

class AddChannelSheet : BottomSheet {

    private var page: SettingsPage? = null
    private var settings: SettingsAPI
    private var currentIcon: String? = null
    private var icon: ImageView? = null
    private var channelId: Long? = null

    constructor(page: SettingsPage?, settings: SettingsAPI) {
        this.page = page
        this.settings = settings
    }

    constructor(channelId: Long?, settings: SettingsAPI) {
        this.channelId = channelId
        this.settings = settings
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        val ctx = requireContext()
        val resources = ctx.resources
        val badge = tintToTheme(ResourcesCompat.getDrawable(resources, R.e.ic_open_in_new_grey_24dp, null)!!.mutate())
        val iconSets: MutableMap<String, String?> =
            settings.getObject("icons", HashMap(), Utils.iconStoreType)

        setPadding(16.dp)

        val iconLayout = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16.dp) }
        }

        val channelNameInput = TextInput(ctx).apply {
            editText.setText(if (channelId == null || channelId == 0L) "" else "id:$channelId")
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setHint("Channel name")
        }

        icon = ImageView(ctx).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            layoutParams = LinearLayout.LayoutParams(32.dp, 32.dp).apply {
                setMargins(8.dp, 0, 8.dp, 0)
            }

            setImageDrawable(badge)
            setOnClickListener {
                IconListSheet(
                    settings = settings,
                    addChannelSheet = this@AddChannelSheet
                ).show(parentFragmentManager, "icon_list")
            }
        }

        val add = Button(ctx).apply {
            text = "Add"

            setOnClickListener {
                val name = channelNameInput.editText.text.toString()

                if (name.isEmpty()) {
                    Toast.makeText(ctx, "Please enter a channel name", Toast.LENGTH_SHORT).show()
                } else {
                    try {
                        val channelName = name.lowercase(Locale.getDefault())

                        // This is just done to verify if the provided icon actually exists
                        ContextCompat.getDrawable(
                            /* context = */ ctx,
                            /* id = */ getResId(
                                name = currentIcon!!,
                                type = "drawable"
                            )
                        )

                        iconSets[channelName] = currentIcon
                        settings.setObject("icons", iconSets)

                        Toast.makeText(ctx, "Added icon", Toast.LENGTH_SHORT).show()

                        if (page != null) page!!.reRender()
                        dismiss()
                    } catch (e: Throwable) {
                        BetterChannelIcons.logger.error("Error setting icon", e)
                        Toast.makeText(ctx, "Drawable not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        iconLayout.addView(icon)
        iconLayout.addView(channelNameInput)

        addView(iconLayout)
        addView(add)
    }

    fun setCurrentIcon(icon: String?) {
        currentIcon = icon

        if (this.icon != null) {
            val ic = ContextCompat.getDrawable(
                /* context = */ requireContext(),
                /* id = */ getResId(
                    name = icon!!,
                    type = "drawable"
                )
            )!!.mutate()

            this.icon!!.setImageDrawable(tintToTheme(ic))
        }
    }

}