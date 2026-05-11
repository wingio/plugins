@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS")

package xyz.wingio.plugins.morehighlight.node

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.graphics.ColorUtils
import com.discord.simpleast.core.node.Node
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.utilities.spans.ClickableSpan
import com.discord.utilities.textprocessing.MessageRenderContext
import com.lytefast.flexinput.R

class SlashCommandNode(
    private val content: String
): Node<MessageRenderContext>() {

    override fun render(builder: SpannableStringBuilder, renderContext: MessageRenderContext) {
        val color = ColorCompat.getThemedColor(renderContext.context, R.b.colorTextLink)
        val length = builder.length

        builder.append("/$content")

        val clickableSpan = ClickableSpan(color, false, {}) {
            StoreStream.getChat().replaceChatText("/$content")
        }

        builder.setSpan(clickableSpan, length, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(BackgroundColorSpan(ColorUtils.setAlphaComponent(color, 25)), length, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(StyleSpan(Typeface.BOLD), length, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

}