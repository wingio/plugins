package xyz.wingio.plugins;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import android.os.*;
import android.text.*;
import android.text.style.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import xyz.wingio.plugins.twemojieverywhere.*;
import xyz.wingio.plugins.twemojieverywhere.patches.*;

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

import com.discord.databinding.*;
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheet;
import com.discord.simpleast.core.parser.*;
import com.discord.utilities.textprocessing.*;
import com.discord.utilities.color.*;
import com.discord.utilities.user.*;
import com.discord.utilities.textprocessing.node.ZeroSpaceWidthNode;
import com.discord.simpleast.core.node.Node;
import com.discord.stores.*;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;
import com.discord.widgets.chat.list.entries.ChatListEntry;
import com.discord.widgets.user.profile.UserProfileHeaderView;
import com.discord.widgets.channels.memberlist.adapter.*;
import com.discord.widgets.user.profile.UserProfileHeaderViewModel;

import com.facebook.drawee.span.DraweeSpanStringBuilder;

import de.robv.android.xposed.XposedBridge;
import com.lytefast.flexinput.R;

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;
import kotlin.jvm.functions.Function0;

@AliucordPlugin
public class TwemojiEverywhere extends Plugin {

  public TwemojiEverywhere() {
    settingsTab = new SettingsTab(PluginSettings.class).withArgs(this);
    needsResources = true;
  }

  public static Logger logger = new Logger("TwemojiEverywhere");
  private Drawable pluginIcon;

  @Override
  public void start(Context context) throws Throwable {
    Patches.patchAll();
    pluginIcon = ContextCompat.getDrawable(context, R.e.ic_emoji_picker_category_people);

    patcher.patch(WidgetChatListAdapterItemMessage.class, "onConfigure", new Class<?>[]{ int.class, ChatListEntry.class }, new Hook(callFrame -> {
      if(!Settings.inChatNames()) return;
      WidgetChatListAdapterItemMessage _this = (WidgetChatListAdapterItemMessage) callFrame.thisObject;
      try {
        TextView author = (TextView) _this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_name", "id"));
        TextView reply = (TextView) _this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_decorator_reply_name", "id"));
        author.setText(renderTwemoji(author.getContext(), author.getText()));
        reply.setText(renderTwemoji(reply.getContext(), reply.getText()));
      } catch(Throwable e){}
    }));
      
    if(Settings.enabledEverywhere()) patcher.patch(TextView.class, "setText", new Class<?>[]{ CharSequence.class }, new Hook(callFrame -> {
      TextView _this = (TextView) callFrame.thisObject;
      try {
        CharSequence text = (CharSequence) callFrame.args[0];
        _this.setText(renderTwemoji(_this.getContext(), text), TextView.BufferType.SPANNABLE);
      } catch(Throwable e){}
    }));
  }

  public static DraweeSpanStringBuilder renderTwemoji(Context context, CharSequence text) {
    return renderTwemoji(context, text, Settings.inServerPopoutName());
  }

  public static DraweeSpanStringBuilder renderTwemoji(Context context, CharSequence text, boolean useCustomEmoji) {
    Parser parser = new Parser();
    Rules rules = Rules.INSTANCE;
    if(useCustomEmoji) parser.addRule(rules.createCustomEmojiRule());
    parser.addRule(rules.createUnicodeEmojiRule());
    parser.addRule(new TextRule());
    var nodes = parser.parse(text, MessageParseState.Companion.getInitialState());
    nodes.add(new ZeroSpaceWidthNode());
    Long meId = StoreStream.getUsers().getMe().getId();
    return (DraweeSpanStringBuilder) AstRenderer.render(nodes, new MessageRenderContext(context, meId, true));
  }

  @Override
  public void stop(Context context) { patcher.unpatchAll(); }
}