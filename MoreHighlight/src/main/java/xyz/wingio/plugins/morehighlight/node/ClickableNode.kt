package xyz.wingio.plugins.morehighlight.node

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.View
import com.discord.simpleast.core.node.Node
import com.discord.utilities.color.ColorCompat
import com.discord.utilities.spans.ClickableSpan
import com.lytefast.flexinput.R

class ClickableNode<MessageRenderContext>(
    private val content: String,
    private val context: Context,
    private val onClick: (View) -> Unit = { },
    private val onLongClick: (View) -> Unit = { }
): Node<MessageRenderContext>() {

    override fun render(builder: SpannableStringBuilder, renderContext: MessageRenderContext) {
        val i = builder.length
        builder.append(content)

        val clickableSpan = ClickableSpan(ColorCompat.getThemedColor(context, R.b.colorTextLink), false, onLongClick, onClick)
        builder.setSpan(clickableSpan, i, builder.length, 33)
    }

}