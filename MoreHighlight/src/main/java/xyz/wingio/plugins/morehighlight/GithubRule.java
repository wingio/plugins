package xyz.wingio.plugins.morehighlight;

import com.aliucord.PluginManager;
import com.discord.simpleast.core.parser.ParseSpec;
import com.discord.simpleast.core.parser.Parser;
import com.discord.simpleast.core.node.Node;
import com.discord.simpleast.core.parser.Rule;
import com.discord.utilities.textprocessing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public final class GithubRule extends Rule<MessageRenderContext, LinkNode<MessageRenderContext>,MessageParseState> {
    public GithubRule(Pattern pattern) {
        super(pattern);
    }

    @Override
    public Matcher match(CharSequence charSequence, String str, MessageParseState s2) {
        return super.match(charSequence, str, s2);
    }

    @Override
    public ParseSpec<MessageRenderContext, MessageParseState> parse(Matcher matcher, Parser<MessageRenderContext, ? super LinkNode<MessageRenderContext>, MessageParseState> parser, MessageParseState s) {
        boolean showRepo = PluginManager.plugins.get("MoreHighlight").settings.getBool("show_repo_name", false);
        LinkNode textNode = new LinkNode((showRepo ? matcher.group(2) + "#" : "#") + matcher.group(3), String.format("https://github.com/%s/%s/issues/%s", matcher.group(1), matcher.group(2), matcher.group(3)));
        return new ParseSpec<>(textNode, s);
    }
}