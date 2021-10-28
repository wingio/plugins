package xyz.wingio.plugins;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import android.os.*;

import androidx.constraintlayout.widget.ConstraintLayout;

import xyz.wingio.plugins.morehighlight.*;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.wrappers.*;
import com.aliucord.utils.*;
import com.aliucord.views.Button;

import com.discord.api.commands.ApplicationCommandType;
import com.discord.api.commands.CommandChoice;
import com.discord.models.commands.ApplicationCommandOption;
import com.discord.stores.*;
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheet;
import com.discord.widgets.user.usersheet.WidgetUserSheet;
import com.discord.widgets.guilds.list.*;
import com.discord.widgets.home.*;
import com.discord.panels.*;
import com.discord.simpleast.core.parser.*;
import com.discord.utilities.textprocessing.*;
import com.discord.simpleast.core.node.Node;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;

import com.discord.utilities.textprocessing.node.EditedMessageNode;
import com.discord.utilities.textprocessing.node.ZeroSpaceWidthNode;


import com.lytefast.flexinput.R;

import c.a.t.b.b.e;

import java.util.regex.*;
import java.util.*;
import java.lang.reflect.*;
import java.lang.*;
import kotlin.NoWhenBranchMatchedException;

@AliucordPlugin
public class MoreHighlight extends Plugin {

  public MoreHighlight() {
    settingsTab = new SettingsTab(PluginSettings.class).withArgs(this);
    needsResources = true;
  }

  public Logger logger = new Logger("MoreHighlight");
  public static Pattern REDDIT_REGEX = Pattern.compile("^<([ur])\\/([a-zA-Z0-9_]{3,20})>");
  public static Pattern ISSUE_REGEX = Pattern.compile("^<([A-Za-z0-9-]{1,39})\\/([A-Za-z0-9-]{1,39})#([0-9]{1,})>");
  public static Pattern REPO_REGEX = Pattern.compile("^<gh:([A-Za-z0-9-]{1,39})/([A-Za-z0-9-]{1,39})>");
  public static Pattern ALIU_REGEX = Pattern.compile("^ac://([A-Za-z0-9$]+)");

  @Override
  public void start(Context context) throws Throwable {
    patcher.patch(DiscordParser.class, "parseChannelMessage", new Class<?>[] {Context.class, String.class, MessageRenderContext.class, MessagePreprocessor.class, DiscordParser.ParserOptions.class, boolean.class}, new PreHook(callFrame -> {
      try{
        Context ctx = (Context) callFrame.args[0];
        Parser<MessageRenderContext, Node<MessageRenderContext>, MessageParseState> parser = DiscordParser.createParser$default(true, true, false, 4, null);
        String str = (String) callFrame.args[1];
        ArrayList<Rule<MessageRenderContext, ? extends Node<MessageRenderContext>,MessageParseState>> rules = (ArrayList<Rule<MessageRenderContext, ? extends Node<MessageRenderContext>,MessageParseState>>) ReflectUtils.getField(parser, "rules");
        rules.add(0, new RedditRule(REDDIT_REGEX, ctx));
        rules.add(0, new IssueRule(ISSUE_REGEX, ctx));
        rules.add(0, new RepoRule(REPO_REGEX, ctx));
        rules.add(0, new AliuRule(ALIU_REGEX, ctx));
        ReflectUtils.setField(parser, "rules", rules);
        if (str == null) {
            str = "";
        }
        var parsed = Parser.parse$default(parser, str, MessageParseState.Companion.getInitialState(), null, 4, null);
        ((MessagePreprocessor) callFrame.args[3]).process(parsed);
        if((boolean) callFrame.args[5]){
          parsed.add(new EditedMessageNode((Context) callFrame.args[0]));
        }
        parsed.add(new ZeroSpaceWidthNode());
        callFrame.setResult(AstRenderer.render(parsed, (MessageRenderContext) callFrame.args[2]));
      } catch(Throwable e) {logger.error("Error patching parser", e);}
    }));
  }

  @Override
  public void stop(Context context) { patcher.unpatchAll(); }
}