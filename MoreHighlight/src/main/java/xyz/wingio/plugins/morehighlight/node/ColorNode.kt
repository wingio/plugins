package xyz.wingio.plugins.morehighlight.node

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.graphics.ColorUtils
import com.discord.simpleast.core.node.Node

class ColorNode<MessageRenderContext>(
    private var content: String,
    private var color: Int
): Node<MessageRenderContext>() {

    override fun render(builder: SpannableStringBuilder, renderContext: MessageRenderContext) {
        val length = builder.length
        builder.append(content)

        builder.setSpan(ForegroundColorSpan(color), length, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(BackgroundColorSpan(ColorUtils.setAlphaComponent(color, 25)), length, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(StyleSpan(Typeface.BOLD), length, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

}