package xyz.wingio.plugins.morehighlight.rule

import com.discord.simpleast.core.parser.ParseSpec
import com.discord.simpleast.core.parser.Parser
import com.discord.simpleast.core.parser.Rule
import com.discord.utilities.textprocessing.MessageParseState
import com.discord.utilities.textprocessing.MessageRenderContext
import xyz.wingio.plugins.morehighlight.node.ClickableNode
import xyz.wingio.plugins.morehighlight.node.SlashCommandNode
import java.util.regex.Matcher
import java.util.regex.Pattern

class SlashCommandRule: Rule<MessageRenderContext, ClickableNode<MessageRenderContext?>, MessageParseState>(
    Pattern.compile("^</(.+):(\\d+)>")
) {

    override fun parse(
        matcher: Matcher,
        parser: Parser<MessageRenderContext, in ClickableNode<MessageRenderContext?>?, MessageParseState>,
        s: MessageParseState
    ): ParseSpec<MessageRenderContext, MessageParseState>
        = ParseSpec(SlashCommandNode(matcher.group(1)!!), s)

}