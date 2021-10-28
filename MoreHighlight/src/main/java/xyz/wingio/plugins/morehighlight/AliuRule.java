package xyz.wingio.plugins.morehighlight;

import android.content.Context;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;

import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.entities.Plugin;

import com.discord.app.*;
import com.discord.simpleast.core.parser.ParseSpec;
import com.discord.simpleast.core.parser.Parser;
import com.discord.simpleast.core.node.Node;
import com.discord.simpleast.core.parser.Rule;
import com.discord.utilities.textprocessing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.Unit;

public final class AliuRule extends Rule<MessageRenderContext, ClickableNode<MessageRenderContext>,MessageParseState> {
    private Context context;
    private static FragmentManager cachedFragment = Utils.appActivity.getSupportFragmentManager();

    public AliuRule(Pattern pattern, Context context) {
        super(pattern);
        this.context = context;
    }

    @Override
    public ParseSpec<MessageRenderContext, MessageParseState> parse(Matcher matcher, Parser<MessageRenderContext, ? super ClickableNode<MessageRenderContext>, MessageParseState> parser, MessageParseState s) {
        final Plugin p = PluginManager.plugins.get(matcher.group(1));
        Context ctx = context;
        var textNode = (p != null && p.settingsTab != null) ? new ClickableNode(matcher.group(1), "", context) : new TextNode(matcher.group(1));
        if(textNode instanceof ClickableNode) ((ClickableNode) textNode).setOnClickListener(v -> {
            try{
                if (p.settingsTab.type == Plugin.SettingsTab.Type.PAGE && p.settingsTab.page != null) {
                    Fragment page = p.settingsTab.args != null
                            ? ReflectUtils.invokeConstructorWithArgs(p.settingsTab.page, p.settingsTab.args)
                            : p.settingsTab.page.newInstance();
                    Utils.openPageWithProxy(ctx, page);
                } else if (p.settingsTab.type == Plugin.SettingsTab.Type.BOTTOM_SHEET && p.settingsTab.bottomSheet != null) {
                    AppBottomSheet sheet = p.settingsTab.args != null
                            ? ReflectUtils.invokeConstructorWithArgs(p.settingsTab.bottomSheet, p.settingsTab.args)
                            : p.settingsTab.bottomSheet.newInstance();

                    sheet.show(cachedFragment, p.getName() + "Settings");
                }
            } catch (Throwable e) {}
            return Unit.a;
        });
        return new ParseSpec<>(textNode, s);
    }
}