package xyz.wingio.plugins.morehighlight.node

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.aliucord.PluginManager
import com.discord.simpleast.core.node.Node
import xyz.wingio.plugins.morehighlight.MoreHighlight

class HeaderNode<MessageRenderContext>(
    private val headerSize: Int
): Node.a<MessageRenderContext>() {

    override fun render(builder: SpannableStringBuilder, renderContext: MessageRenderContext) {
        val length = builder.length
        super.render(builder, renderContext)

        val scaleFactor = PluginManager.plugins["MoreHighlight"]!!.settings.getFloat(MoreHighlight.PREF_HEADER_SIZE, 1.0f)
        val proportion = when (headerSize) {
            1 -> 2.0f * scaleFactor
            2 -> 1.5f * scaleFactor
            3 -> 1.25f * scaleFactor
            else -> 1f
        }

        builder.setSpan(RelativeSizeSpan(proportion), length, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(StyleSpan(Typeface.BOLD), length, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

}
