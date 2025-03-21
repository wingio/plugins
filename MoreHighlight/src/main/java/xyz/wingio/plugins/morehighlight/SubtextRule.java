package xyz.wingio.plugins.morehighlight;

import com.discord.simpleast.core.parser.ParseSpec;
import com.discord.simpleast.core.parser.Parser;
import com.discord.simpleast.core.parser.Rule;
import com.discord.utilities.textprocessing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SubtextRule extends Rule<MessageRenderContext, SubtextNode<MessageRenderContext>, MessageParseState> {

    public SubtextRule() {
        // Matches tiny text syntax: -# tiny greyed out text
        super(Pattern.compile("^-# (.+)$"));
    }

    @Override
    public ParseSpec<MessageRenderContext, MessageParseState> parse(Matcher matcher, Parser<MessageRenderContext, ? super SubtextNode<MessageRenderContext>, MessageParseState> parser, MessageParseState s) {
        // Extract text content
        String content = matcher.group(1);
        
        SubtextNode SubtextNode = new SubtextNode(content);
        return new ParseSpec<>(SubtextNode, s);
    }
}
