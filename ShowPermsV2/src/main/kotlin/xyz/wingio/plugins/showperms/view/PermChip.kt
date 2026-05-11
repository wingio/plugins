package xyz.wingio.plugins.showperms.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.utils.DimenUtils.dp
import com.discord.utilities.color.ColorCompat
import com.google.android.material.card.MaterialCardView
import com.lytefast.flexinput.R
import xyz.wingio.plugins.showperms.util.Settings

/**
 * A chip that is designed to emulate the appearance of the role chip
 */
class PermChip(
    private val ctx: Context
): MaterialCardView(ctx) {

    private val label: TextView
    private val roleDot: ImageView

    init {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        radius = 4.dp.toFloat()
        setCardBackgroundColor(ColorCompat.getThemedColor(ctx, R.b.colorBackgroundTertiary))

        val root = LinearLayout(ctx).apply {
            setVerticalGravity(Gravity.CENTER_VERTICAL)
            setPadding(8.dp, 6.dp, 8.dp, 6.dp)
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }

        label = TextView(ctx, null, 0, R.i.UiKit_TextAppearance_Semibold).apply {
            textSize = 12f
            maxLines = 1
        }

        roleDot = ImageView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(12.dp, 12.dp).apply {
                marginEnd = 8.dp
            }
        }
        setDotColor(0)

        if (Settings.showDot) root.addView(roleDot)
        root.addView(label)
        addView(root)
    }

    fun setPermName(name: String) {
        label.text = name
    }

    fun setDotColor(color: Int) {
        val clr = if (color == 0) ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal) else Color.parseColor("#%06x".format(color))
        val dotIcon = ContextCompat.getDrawable(ctx, R.e.drawable_circle_white_12dp)!!.mutate()
        dotIcon.setTint(clr)
        roleDot.setImageDrawable(dotIcon)
    }

    companion object {

        fun detailsButton(ctx: Context) = PermChip(ctx).apply {
            val shieldIcon = ContextCompat.getDrawable(ctx, R.e.ic_shieldstar_24dp)!!.mutate()
            shieldIcon.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))

            label.visibility = GONE
            roleDot.apply {
                setImageDrawable(shieldIcon)
                (layoutParams as LinearLayout.LayoutParams).apply {
                    height = 16.dp
                    width = 16.dp
                    marginEnd = 0
                }
            }

            setOnClickListener {

            }
        }

    }

}