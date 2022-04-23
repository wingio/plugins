package xyz.wingio.plugins.morehighlight;

import android.content.Context;
import android.graphics.Color;

import com.aliucord.PluginManager;
import com.discord.simpleast.core.parser.ParseSpec;
import com.discord.simpleast.core.parser.Parser;
import com.discord.simpleast.core.node.Node;
import com.discord.simpleast.core.parser.Rule;
import com.discord.utilities.textprocessing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorRule extends Rule<MessageRenderContext, ColorNode<MessageRenderContext>,MessageParseState> {
    public ColorRule() {
        super(Pattern.compile("^#[0-9a-fA-F]{6,8}"));
    }

    @Override
    public ParseSpec<MessageRenderContext, MessageParseState> parse(Matcher matcher, Parser<MessageRenderContext, ? super ColorNode<MessageRenderContext>, MessageParseState> parser, MessageParseState s) {
        try {
            ColorNode textNode = new ColorNode(matcher.group(0), Color.parseColor(matcher.group(0)));
            return new ParseSpec<>(textNode, s);
        } catch (Exception e) {
            return new ParseSpec<>(new TextNode(matcher.group(0)), s);
        }
    }
}