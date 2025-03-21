package xyz.wingio.plugins.morehighlight;

import com.discord.simpleast.core.parser.ParseSpec;
import com.discord.simpleast.core.parser.Parser;
import com.discord.simpleast.core.parser.Rule;
import com.discord.utilities.textprocessing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HeaderRule extends Rule<MessageRenderContext, HeaderNode<MessageRenderContext>, MessageParseState> {

    public HeaderRule() {
        // Matches headers: # Header, ## Header, ### Header
        super(Pattern.compile("^(#{1,3}) (.+)$"));
    }

    @Override
    public ParseSpec<MessageRenderContext, MessageParseState> parse(Matcher matcher, Parser<MessageRenderContext, ? super HeaderNode<MessageRenderContext>, MessageParseState> parser, MessageParseState s) {
        // Determine header level from number of # symbols
        int level = matcher.group(1).length();
        // Extract header content
        String content = matcher.group(2);
        
        HeaderNode headerNode = new HeaderNode(content, level);
        return new ParseSpec<>(headerNode, s);
    }
}
