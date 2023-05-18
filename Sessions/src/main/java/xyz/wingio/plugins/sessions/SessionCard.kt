package xyz.wingio.plugins.sessions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.*
import android.graphics.drawable.shapes.*
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.PluginManager
import com.aliucord.utils.*
import com.aliucord.utils.DimenUtils.dp
import com.discord.utilities.color.ColorCompat
import com.lytefast.flexinput.R

class SessionCard(ctx: Context) : LinearLayout(ctx) {
    val name = TextView(ctx)
    val description = TextView(ctx)
    val icon = ImageView(ctx)
    val logOutBtn = ImageButton(ctx)

    init {
        val p: Int = DimenUtils.dpToPx(16)
        val p2 = p / 2
        gravity = Gravity.CENTER_VERTICAL

        val cardParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, p2)
            layoutParams = this
        }

        icon.apply {
            setPadding(10.dp, 10.dp, 10.dp, 10.dp)
            LayoutParams(
                50.dp,
                50.dp
            ).apply {
                setMargins(p, p, p, p)
                layoutParams = this
            }
            background = ShapeDrawable(OvalShape()).apply {
                paint.color = ColorCompat.getThemedColor(context!!, R.b.colorInteractiveNormal)
            }
            this@SessionCard.addView(this)
        }

        LinearLayout(ctx).apply {
            orientation = VERTICAL
            setLayoutParams(
                LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
            )

            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, p, p, p)

            val params = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 0) }

            name.apply {
                layoutParams = params
                textSize = 16f
                typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_bold)
                compoundDrawablePadding = DimenUtils.dpToPx(6)
                isSingleLine = false
                setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
            }

            description.apply {
                textSize = 14f
                typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium)
                isSingleLine = false
                layoutParams = params
                setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
            }

            addView(name)
            addView(description)
            this@SessionCard.addView(this)
        }

        LinearLayout(ctx).let {
            it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            it.gravity = Gravity.END or Gravity.CENTER_VERTICAL
            it.setPadding(0, 0, 16.dp, 0)

            logOutBtn.apply {
                val clearIcon = ContextCompat.getDrawable(ctx, R.e.ic_clear_24dp)?.apply {
                    setTint(ColorCompat.getThemedColor(ctx, R.b.colorButtonDangerBackground))
                }
                layoutParams = LayoutParams(40.dp, 40.dp)
                setImageDrawable(clearIcon)
                setBackgroundColor(Color.TRANSPARENT)
                visibility = GONE

                it.addView(this)
                this@SessionCard.addView(it)
            }
        }
    }

    var title: CharSequence
        set(value) { name.text = value }
        get() = name.text

    var location: CharSequence
        set(value) { description.text = value }
        get() = description.text

    var isCurrent: Boolean = false

    var isMobile: Boolean
        set(value) {
            val drawable = if(value) getPluginDrawable("ic_mobile_24dp") else getPluginDrawable("ic_desktop_24dp")
            icon.setImageDrawable(drawable.apply {
                mutate()
                setTint(ColorCompat.getThemedColor(context!!, R.b.colorBackgroundSecondaryAlt))
            })
        }
        get() = title.startsWith("Android") || title.startsWith("iOS")

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getPluginDrawable(name: String): Drawable {
        val res = PluginManager.plugins["Sessions"]!!.resources
        return res.getDrawable(res.getIdentifier(name, "drawable", "com.aliucord.plugins"), null)
    }
}