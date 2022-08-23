package xyz.wingio.plugins.morehighlight;

import com.discord.simpleast.core.parser.ParseSpec;
import com.discord.simpleast.core.parser.Parser;
import com.discord.simpleast.core.parser.Rule;
import com.discord.utilities.textprocessing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SlashCommandRule extends Rule<MessageRenderContext, ClickableNode<MessageRenderContext>,MessageParseState> {

    public SlashCommandRule() {
        super(Pattern.compile("^</(.+):(\\d+)>"));
    }

    @Override
    public ParseSpec<MessageRenderContext, MessageParseState> parse(Matcher matcher, Parser<MessageRenderContext, ? super ClickableNode<MessageRenderContext>, MessageParseState> parser, MessageParseState s) {
        SlashCommandNode scNode = new SlashCommandNode(matcher.group(1));
        return new ParseSpec<>(scNode, s);
    }
}