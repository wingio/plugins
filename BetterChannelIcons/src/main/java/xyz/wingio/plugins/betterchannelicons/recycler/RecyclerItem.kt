@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS")

package xyz.wingio.plugins.betterchannelicons.recycler

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

import androidx.core.content.ContextCompat

import com.aliucord.utils.DimenUtils.dp
import com.aliucord.views.ToolbarButton

import com.lytefast.flexinput.R

class RecyclerItem(ctx: Context?) : LinearLayout(ctx) {

    val name: TextView = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon)

    val delete: ToolbarButton = ToolbarButton(ctx).apply {
        setImageDrawable(ContextCompat.getDrawable(ctx!!, R.e.ic_delete_24dp))
    }

    private val buttons = LinearLayout(ctx).apply {
        setHorizontalGravity(Gravity.END)
        setVerticalGravity(Gravity.CENTER_VERTICAL)
        orientation = HORIZONTAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            marginEnd = 16.dp
        }

        addView(delete)
    }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

        addView(name)
        addView(buttons)
    }

}