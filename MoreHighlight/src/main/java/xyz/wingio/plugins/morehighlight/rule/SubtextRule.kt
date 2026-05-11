package xyz.wingio.plugins.morehighlight.rule

import android.content.Context
import com.discord.simpleast.core.parser.ParseSpec
import com.discord.simpleast.core.parser.Parser
import com.discord.simpleast.core.parser.Rule.BlockRule
import com.discord.utilities.textprocessing.MessageParseState
import com.discord.utilities.textprocessing.MessageRenderContext
import xyz.wingio.plugins.morehighlight.node.BulletPointNode
import xyz.wingio.plugins.morehighlight.node.SubtextNode
import java.util.regex.Matcher
import java.util.regex.Pattern

class SubtextRule(
    private val context: Context
): BlockRule<MessageRenderContext, SubtextNode<MessageRenderContext?>, MessageParseState>(
    Pattern.compile("^\\s*(-#)\\s+(.+)(?=\\n|$)")
) {

    override fun parse(
        matcher: Matcher,
        parser: Parser<MessageRenderContext?, in SubtextNode<MessageRenderContext?>?, MessageParseState?>,
        s: MessageParseState
    ): ParseSpec<MessageRenderContext?, MessageParseState?> {
        return ParseSpec(SubtextNode(context), s, matcher.start(2), matcher.end(2))
    }

}
