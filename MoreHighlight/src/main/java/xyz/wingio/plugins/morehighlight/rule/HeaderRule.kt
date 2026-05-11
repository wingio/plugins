package xyz.wingio.plugins.morehighlight.rule

import com.discord.simpleast.core.parser.ParseSpec
import com.discord.simpleast.core.parser.Parser
import com.discord.simpleast.core.parser.Rule.BlockRule
import com.discord.utilities.textprocessing.MessageParseState
import com.discord.utilities.textprocessing.MessageRenderContext
import xyz.wingio.plugins.morehighlight.node.HeaderNode
import java.util.regex.Matcher
import java.util.regex.Pattern

class HeaderRule: BlockRule<MessageRenderContext, HeaderNode<MessageRenderContext?>, MessageParseState>(
    Pattern.compile("^\\s*(##?#?)\\s+(.+)(?=\\n|$)")
) {

    override fun parse(
        matcher: Matcher,
        parser: Parser<MessageRenderContext?, in HeaderNode<MessageRenderContext?>?, MessageParseState?>,
        s: MessageParseState?
    ): ParseSpec<MessageRenderContext?, MessageParseState?> {
        return ParseSpec(HeaderNode(matcher.group(1)!!.length), s, matcher.start(2), matcher.end(2))
    }

}
