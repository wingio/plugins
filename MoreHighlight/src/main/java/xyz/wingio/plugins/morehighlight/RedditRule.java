package xyz.wingio.plugins.morehighlight;

import com.discord.simpleast.core.parser.ParseSpec;
import com.discord.simpleast.core.parser.Parser;
import com.discord.simpleast.core.node.Node;
import com.discord.simpleast.core.parser.Rule;
import com.discord.utilities.textprocessing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public final class RedditRule extends Rule<MessageRenderContext, LinkNode<MessageRenderContext>,MessageParseState> {
    public RedditRule(Pattern pattern) {
        super(pattern);
    }

    @Override
    public Matcher match(CharSequence charSequence, String str, MessageParseState s2) {
        return super.match(charSequence, str, s2);
    }

    @Override
    public ParseSpec<MessageRenderContext, MessageParseState> parse(Matcher matcher, Parser<MessageRenderContext, ? super LinkNode<MessageRenderContext>, MessageParseState> parser, MessageParseState s) {
        LinkNode textNode = new LinkNode(matcher.group(1) + "/" + matcher.group(2), String.format("https://reddit.com/%s/%s", matcher.group(1), matcher.group(2)));
        return new ParseSpec<>(textNode, s);
    }
}