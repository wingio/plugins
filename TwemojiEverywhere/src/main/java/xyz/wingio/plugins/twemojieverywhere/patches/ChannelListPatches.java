package xyz.wingio.plugins.twemojieverywhere.patches;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;

import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.*;
import com.aliucord.patcher.*;
import com.aliucord.utils.*;
import com.aliucord.wrappers.*;

import xyz.wingio.plugins.twemojieverywhere.*;

import com.discord.databinding.*;
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheet;
import com.discord.simpleast.core.parser.*;
import com.discord.utilities.textprocessing.*;
import com.discord.utilities.color.*;
import com.discord.utilities.user.*;
import com.discord.utilities.textprocessing.node.ZeroSpaceWidthNode;
import com.discord.simpleast.core.node.Node;
import com.discord.stores.*;
import com.discord.widgets.channels.list.*;
import com.discord.widgets.channels.list.items.*;
import com.discord.widgets.user.profile.UserProfileHeaderViewModel;
import com.lytefast.flexinput.R;

import de.robv.android.xposed.XposedBridge;

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;
import kotlin.jvm.functions.Function0;

public class ChannelListPatches extends Patches {
    public ChannelListPatches(PatcherAPI patcher) {
        super(patcher);
    }
    public int channelTextName = Utils.getResId("channels_item_channel_name", "id");
    public int channelCatName = Utils.getResId("channels_item_category_name", "id");
    public int channelVoiceName = Utils.getResId("channels_item_voice_channel_name", "id");
    public int channelPrivName = Utils.getResId("channels_list_item_private_name", "id");
    public int channelStageName = Utils.getResId("stage_channel_item_voice_channel_name", "id");
    public int channelThreadName = Utils.getResId("channels_item_thread_name", "id");

    public void patchTextChannel() {
        patcher.patch(WidgetChannelsListAdapter.ItemChannelText.class, "onConfigure", new Class<?>[] {int.class, ChannelListItem.class}, new Hook(callFrame -> {
            if(!Settings.inChannelList()) return;
            WidgetChannelsListAdapter.ItemChannelText _this = (WidgetChannelsListAdapter.ItemChannelText) callFrame.thisObject;
            TextView channelName = (TextView) _this.itemView.findViewById(channelTextName);
            channelName.setText(renderTwemoji(channelName.getContext(), channelName.getText()));
        }));
    }

    public void patchCatChannel() {
        patcher.patch(WidgetChannelsListAdapter.ItemChannelCategory.class, "onConfigure", new Class<?>[] {int.class, ChannelListItem.class}, new Hook(callFrame -> {
            if(!Settings.inChannelList()) return;
            WidgetChannelsListAdapter.ItemChannelCategory _this = (WidgetChannelsListAdapter.ItemChannelCategory) callFrame.thisObject;
            TextView channelName = (TextView) _this.itemView.findViewById(channelCatName);
            channelName.setText(renderTwemoji(channelName.getContext(), channelName.getText()));
        }));
    }

    public void patchVoiceChannel() {
        patcher.patch(WidgetChannelsListAdapter.ItemChannelVoice.class, "onConfigure", new Class<?>[] {int.class, ChannelListItem.class}, new Hook(callFrame -> {
            if(!Settings.inChannelList()) return;
            WidgetChannelsListAdapter.ItemChannelVoice _this = (WidgetChannelsListAdapter.ItemChannelVoice) callFrame.thisObject;
            TextView channelName = (TextView) _this.itemView.findViewById(channelVoiceName);
            channelName.setText(renderTwemoji(channelName.getContext(), channelName.getText()));
        }));
    }

    public void patchPrivChannel() {
        patcher.patch(WidgetChannelsListAdapter.ItemChannelPrivate.class, "onConfigure", new Class<?>[] {int.class, ChannelListItem.class}, new Hook(callFrame -> {
            if(!Settings.inChannelList()) return;
            WidgetChannelsListAdapter.ItemChannelPrivate _this = (WidgetChannelsListAdapter.ItemChannelPrivate) callFrame.thisObject;
            TextView channelName = (TextView) _this.itemView.findViewById(channelPrivName);
            channelName.setText(renderTwemoji(channelName.getContext(), channelName.getText()));
        }));
    }

    public void patchStageChannel() {
        patcher.patch(WidgetChannelsListAdapter.ItemChannelStageVoice.class, "onConfigure", new Class<?>[] {int.class, ChannelListItem.class}, new Hook(callFrame -> {
            if(!Settings.inChannelList()) return;
            WidgetChannelsListAdapter.ItemChannelStageVoice _this = (WidgetChannelsListAdapter.ItemChannelStageVoice) callFrame.thisObject;
            TextView channelName = (TextView) _this.itemView.findViewById(channelStageName);
            channelName.setText(renderTwemoji(channelName.getContext(), channelName.getText()));
        }));
    }

    public void patchThreadChannel() {
        patcher.patch(WidgetChannelsListAdapter.ItemChannelThread.class, "onConfigure", new Class<?>[] {int.class, ChannelListItem.class}, new Hook(callFrame -> {
            if(!Settings.inChannelList()) return;
            WidgetChannelsListAdapter.ItemChannelThread _this = (WidgetChannelsListAdapter.ItemChannelThread) callFrame.thisObject;
            TextView channelName = (TextView) _this.itemView.findViewById(channelThreadName);
            channelName.setText(renderTwemoji(channelName.getContext(), channelName.getText()));
        }));
    }
}