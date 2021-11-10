package xyz.wingio.plugins.twemojieverywhere.patches;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;

import xyz.wingio.plugins.twemojieverywhere.*;

import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.*;
import com.aliucord.patcher.*;
import com.aliucord.utils.*;
import com.aliucord.wrappers.*;

import com.discord.api.channel.Channel;
import com.discord.databinding.*;
import com.discord.models.guild.Guild;
import com.discord.stores.*;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.widgets.channels.list.*;
import com.discord.widgets.channels.list.items.*;
import com.discord.widgets.home.*;
import com.discord.utilities.textprocessing.node.ZeroSpaceWidthNode;
import com.discord.utilities.textprocessing.*;
import com.discord.simpleast.core.parser.*;
import com.discord.simpleast.core.node.Node;
import com.lytefast.flexinput.R;

import com.facebook.drawee.span.DraweeSpanStringBuilder;

import java.util.*;

public class Patches {
    public static PatcherAPI patcher = new PatcherAPI();
    public static Logger logger = new Logger("TwemojiEverywhere");
    public static SettingsAPI settings = PluginManager.plugins.get("TwemojiEverywhere").settings;

    private static boolean isSHCEnabled(){
        return PluginManager.plugins.get("ShowHiddenChannels") != null && PluginManager.isPluginEnabled("ShowHiddenChannels");
    }

    public static void patchAll(){
        if(Settings.inMemberList()) MemberListPatches.patchMemberList();
        if(Settings.inProfileSheet()) ProfileSheetPatches.patchUserSheet();
        if(Settings.inChannelList()) patchChannelList();
    }

    public static void patchChannelList(){
        ChannelListPatches.patchTextChannel();
        ChannelListPatches.patchCatChannel();
        ChannelListPatches.patchVoiceChannel();
        ChannelListPatches.patchPrivChannel();
        ChannelListPatches.patchStageChannel();
        ChannelListPatches.patchThreadChannel();
    }

    public static DraweeSpanStringBuilder renderTwemoji(Context context, CharSequence text) {
        Parser parser = new Parser();
        Rules rules = Rules.INSTANCE;
        if(Settings.inServerPopoutName()) parser.addRule(rules.createCustomEmojiRule());
        parser.addRule(rules.createUnicodeEmojiRule());
        parser.addRule(new TextRule());
        var nodes = parser.parse(text, MessageParseState.Companion.getInitialState());
        nodes.add(new ZeroSpaceWidthNode());
        Long meId = StoreStream.getUsers().getMe().getId();
        return (DraweeSpanStringBuilder) AstRenderer.render(nodes, new MessageRenderContext(context, meId, true));
    }
}