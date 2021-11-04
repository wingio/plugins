package xyz.wingio.plugins.twemojieverywhere;

import com.discord.simpleast.core.parser.ParseSpec;
import com.discord.simpleast.core.parser.Parser;
import com.discord.simpleast.core.node.Node;
import com.discord.simpleast.core.parser.Rule;
import com.discord.utilities.textprocessing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public final class TextRule extends Rule<MessageRenderContext, Node<MessageRenderContext>,MessageParseState> {
    public TextRule() {
        super(Pattern.compile("[\\d\\D]"));
    }

    @Override
    public ParseSpec<MessageRenderContext, MessageParseState> parse(Matcher matcher, Parser<MessageRenderContext, ? super Node<MessageRenderContext>, MessageParseState> parser, MessageParseState s) {
        TextNode textNode = new TextNode(matcher.group());
        return new ParseSpec<>(textNode, s);
    }
}