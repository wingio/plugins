package xyz.wingio.plugins.morehighlight.node

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import com.discord.simpleast.core.node.Node
import com.discord.utilities.color.ColorCompat
import com.lytefast.flexinput.R

class SubtextNode<MessageRenderContext>(
    private val context: Context
): Node.a<MessageRenderContext>() {

    override fun render(builder: SpannableStringBuilder, renderContext: MessageRenderContext) {
        val length = builder.length
        super.render(builder, renderContext)

        val mutedColor = ColorCompat.getThemedColor(context, R.b.colorTextMuted)
        builder.setSpan(RelativeSizeSpan(0.85f), length, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(ForegroundColorSpan(mutedColor), length, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

}
