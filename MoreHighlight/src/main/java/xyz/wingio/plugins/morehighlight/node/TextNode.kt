package xyz.wingio.plugins.morehighlight.node

import android.text.SpannableStringBuilder
import com.discord.simpleast.core.node.Node

class TextNode<MessageRenderContext>(
    private val content: String
): Node<MessageRenderContext>() {

    override fun render(builder: SpannableStringBuilder, renderContext: MessageRenderContext) {
        builder.append(content)
    }

}