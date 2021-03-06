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
    public PatcherAPI patcher;
    public static Logger logger = new Logger("TwemojiEverywhere");
    public static SettingsAPI settings = PluginManager.plugins.get("TwemojiEverywhere").settings;

    private static boolean isSHCEnabled(){
        return PluginManager.plugins.get("ShowHiddenChannels") != null && PluginManager.isPluginEnabled("ShowHiddenChannels");
    }

    public Patches(PatcherAPI patcher) {
        this.patcher = patcher;
    }

    public void patchAll(){
        MemberListPatches memberListPatches = new MemberListPatches(patcher);
        ProfileSheetPatches profileSheetPatches = new ProfileSheetPatches(patcher);
        if(Settings.inMemberList()) memberListPatches.patchMemberList();
        if(Settings.inProfileSheet()) profileSheetPatches.patchUserSheet();
        if(Settings.inChannelList()) patchChannelList();
    }

    public void patchChannelList(){
        ChannelListPatches channelListPatches = new ChannelListPatches(patcher);
        channelListPatches.patchTextChannel();
        channelListPatches.patchCatChannel();
        channelListPatches.patchVoiceChannel();
        channelListPatches.patchPrivChannel();
        channelListPatches.patchStageChannel();
        channelListPatches.patchThreadChannel();
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