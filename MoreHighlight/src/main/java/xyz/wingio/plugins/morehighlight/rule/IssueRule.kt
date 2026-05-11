package xyz.wingio.plugins.morehighlight.rule

import android.content.Context
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.discord.simpleast.core.parser.ParseSpec
import com.discord.simpleast.core.parser.Parser
import com.discord.simpleast.core.parser.Rule
import com.discord.utilities.textprocessing.MessageParseState
import com.discord.utilities.textprocessing.MessageRenderContext
import xyz.wingio.plugins.morehighlight.MoreHighlight
import xyz.wingio.plugins.morehighlight.node.ClickableNode
import java.util.regex.Matcher
import java.util.regex.Pattern

class IssueRule(
    private val context: Context
): Rule<MessageRenderContext, ClickableNode<MessageRenderContext?>, MessageParseState>(
    Pattern.compile("^<([A-Za-z0-9-]{1,39})/([A-Za-z0-9-]{1,39})#([0-9]+)>")
) {

    override fun parse(
        matcher: Matcher,
        parser: Parser<MessageRenderContext, in ClickableNode<MessageRenderContext?>?, MessageParseState>,
        s: MessageParseState
    ): ParseSpec<MessageRenderContext, MessageParseState> {

        val showRepo = PluginManager.plugins["MoreHighlight"]!!.settings.getBool(MoreHighlight.PREF_SHOW_REPO_NAME, false)
        val url = "https://github.com/%s/%s/issues/%s".format(matcher.group(1), matcher.group(2), matcher.group(3))

        val clickableNode = ClickableNode<MessageRenderContext>((if (showRepo) matcher.group(2)!! + "#" else "#") + matcher.group(3), context, { Utils.launchUrl(url) }, {})
        return ParseSpec(clickableNode, s)
    }

}