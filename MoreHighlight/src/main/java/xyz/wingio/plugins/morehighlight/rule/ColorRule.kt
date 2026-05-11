package xyz.wingio.plugins.morehighlight.rule

import android.graphics.Color
import com.discord.simpleast.core.parser.ParseSpec
import com.discord.simpleast.core.parser.Parser
import com.discord.simpleast.core.parser.Rule
import com.discord.utilities.textprocessing.MessageParseState
import com.discord.utilities.textprocessing.MessageRenderContext
import xyz.wingio.plugins.morehighlight.node.ColorNode
import xyz.wingio.plugins.morehighlight.node.TextNode
import java.util.regex.Matcher
import java.util.regex.Pattern

class ColorRule: Rule<MessageRenderContext, ColorNode<MessageRenderContext?>, MessageParseState>(
    Pattern.compile("^#[0-9a-fA-F]{6,8}")
) {
    
    override fun parse(
        matcher: Matcher,
        parser: Parser<MessageRenderContext, in ColorNode<MessageRenderContext?>?, MessageParseState>,
        s: MessageParseState
    ): ParseSpec<MessageRenderContext, MessageParseState> {
        return try {
            ParseSpec(ColorNode(matcher.group(0)!!, Color.parseColor(matcher.group(0))), s)
        } catch (_: Exception) {
            ParseSpec(TextNode(matcher.group(0)!!), s)
        }
    }

}