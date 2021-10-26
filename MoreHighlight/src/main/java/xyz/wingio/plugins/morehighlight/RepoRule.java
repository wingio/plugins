package xyz.wingio.plugins.morehighlight;

import com.aliucord.PluginManager;
import com.discord.simpleast.core.parser.ParseSpec;
import com.discord.simpleast.core.parser.Parser;
import com.discord.simpleast.core.node.Node;
import com.discord.simpleast.core.parser.Rule;
import com.discord.utilities.textprocessing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public final class RepoRule extends Rule<MessageRenderContext, LinkNode<MessageRenderContext>,MessageParseState> {
    public RepoRule(Pattern pattern) {
        super(pattern);
    }

    @Override
    public ParseSpec<MessageRenderContext, MessageParseState> parse(Matcher matcher, Parser<MessageRenderContext, ? super LinkNode<MessageRenderContext>, MessageParseState> parser, MessageParseState s) {
        LinkNode textNode = new LinkNode(matcher.group(1) + "/" + matcher.group(2), String.format("https://github.com/%s/%s", matcher.group(1), matcher.group(2)));
        return new ParseSpec<>(textNode, s);
    }
}