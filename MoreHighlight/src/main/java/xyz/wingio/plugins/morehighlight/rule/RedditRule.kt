package xyz.wingio.plugins.morehighlight.rule

import android.content.Context
import com.aliucord.Utils
import com.discord.simpleast.core.parser.ParseSpec
import com.discord.simpleast.core.parser.Parser
import com.discord.simpleast.core.parser.Rule
import com.discord.utilities.textprocessing.MessageParseState
import com.discord.utilities.textprocessing.MessageRenderContext
import xyz.wingio.plugins.morehighlight.node.ClickableNode
import java.util.regex.Matcher
import java.util.regex.Pattern

class RedditRule(
    private val context: Context
): Rule<MessageRenderContext, ClickableNode<MessageRenderContext?>, MessageParseState>(
    Pattern.compile("^<([ur])/([a-zA-Z0-9_]{3,20})>")
) {

    override fun parse(
        matcher: Matcher,
        parser: Parser<MessageRenderContext, in ClickableNode<MessageRenderContext?>?, MessageParseState>,
        s: MessageParseState
    ): ParseSpec<MessageRenderContext, MessageParseState> {
        val url = "https://reddit.com/%s/%s".format(matcher.group(1), matcher.group(2))

        val clickableNode = ClickableNode<MessageRenderContext>(matcher.group(1)!! + "/" + matcher.group(2), context, { Utils.launchUrl(url) }, {})
        return ParseSpec(clickableNode, s)
    }

}