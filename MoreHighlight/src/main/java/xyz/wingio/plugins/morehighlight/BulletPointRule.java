package xyz.wingio.plugins.morehighlight;

import android.content.Context;

import com.discord.simpleast.core.parser.ParseSpec;
import com.discord.simpleast.core.parser.Parser;
import com.discord.simpleast.core.node.Node;
import com.discord.simpleast.core.parser.Rule;
import com.discord.utilities.textprocessing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BulletPointRule extends Rule.BlockRule<MessageRenderContext, BulletPointNode<MessageRenderContext>, MessageParseState> {
    public BulletPointRule() {

        super(Pattern.compile("^\\s*([*-])\\s+(.+)(?=\\n|$)"));
    }

    @Override
    public ParseSpec<MessageRenderContext, MessageParseState> parse(Matcher matcher, Parser<MessageRenderContext, ? super BulletPointNode<MessageRenderContext>, MessageParseState> parser, MessageParseState s) {
        String content = matcher.group(2);
        
        BulletPointNode bulletPointNode = new BulletPointNode(content);
        return new ParseSpec<>(bulletPointNode, s);
    }
}