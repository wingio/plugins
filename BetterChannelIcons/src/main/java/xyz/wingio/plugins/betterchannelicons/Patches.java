package xyz.wingio.plugins.betterchannelicons;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;

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
import com.lytefast.flexinput.R;

import java.util.*;

public class Patches {
    private static PatcherAPI patcher = new PatcherAPI();
    private static Logger logger = new Logger("BetterChannelIcons");
    private static SettingsAPI settings = PluginManager.plugins.get("BetterChannelIcons").settings;

    private static boolean isSHCEnabled(){
        return PluginManager.plugins.get("ShowHiddenChannels") != null && PluginManager.isPluginEnabled("ShowHiddenChannels");
    }

    public static void addChannelAction(){
        patcher.patch(WidgetChannelsListItemChannelActions.class, "configureUI", new Class<?>[] { WidgetChannelsListItemChannelActions.Model.class }, new Hook(callFrame -> {
            WidgetChannelsListItemChannelActions.Model model = (WidgetChannelsListItemChannelActions.Model) callFrame.args[0];
            WidgetChannelsListItemChannelActions _this = (WidgetChannelsListItemChannelActions) callFrame.thisObject;
            try {
                WidgetChannelsListItemActionsBinding binding = (WidgetChannelsListItemActionsBinding) ReflectUtils.invokeMethod(_this, "getBinding");
                ViewGroup root = (ViewGroup) ((ViewGroup) binding.getRoot()).getChildAt(0);
                TextView edit = new TextView(root.getContext(), null, 0, R.i.UiKit_Settings_Item_Icon);
                Drawable editIcn = ContextCompat.getDrawable(root.getContext(), R.e.ic_edit_24dp).mutate();
                editIcn.setTint(ColorCompat.getThemedColor(root.getContext(), R.b.colorInteractiveNormal));
                edit.setText("Change Icon");
                edit.setCompoundDrawablesWithIntrinsicBounds(editIcn, null, null, null);
                edit.setOnClickListener(v -> {
                    new AddChannelSheet(ChannelWrapper.getId(model.getChannel()), settings).show(_this.getParentFragmentManager(), "Edit Channel Icon");
                });
                if(isSHCEnabled() && !PermissionUtils.INSTANCE.hasAccess(model.getChannel(), model.getPermissions())) { root.addView(edit); } else { root.addView(edit, 2); }
            } catch (Throwable e) {logger.error("Error configuring channel actions", e);}
        }));
    }

    public static void setToolbarIcon(Resources resources) {
        patcher.patch(WidgetHomeHeaderManager.class, "configure", new Class<?>[]{ WidgetHome.class, WidgetHomeModel.class, WidgetHomeBinding.class }, new Hook(callFrame -> {
        try {
            if(!settings.getBool("setToolbarIcon", true)) return;
            WidgetHomeBinding binding = (WidgetHomeBinding) callFrame.args[2]; WidgetHomeModel model = (WidgetHomeModel) callFrame.args[1]; WidgetHome widget = (WidgetHome) callFrame.args[0]; ChannelWrapper channel = new ChannelWrapper(model.getChannel()); var root = widget.getActionBarTitleLayout().j.getRoot();
            ImageView channelIcon = (ImageView) root.findViewById(com.aliucord.Utils.getResId("toolbar_icon", "id"));
            if(channel.isGuild()) {Guild guild = StoreStream.getGuilds().getGuilds().get(channel.getGuildId());if(guild.getRulesChannelId() != null){if(guild.getRulesChannelId() == channel.getId()) { channelIcon.setImageDrawable(Utils.themeDrawable(widget.getContext(), ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_rules_24dp", "drawable", "xyz.wingio.plugins"), null)) ); };}}
            if(channel.getId() == 811275162715553823L || channel.getId() == 845784407846813696L){ channelIcon.setImageDrawable(Utils.themeDrawable(widget.getContext(), ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_plugin_24dp", "drawable", "xyz.wingio.plugins"), null))); }

            var icon = Utils.getChannelIcon(channel);
            if (icon != null) channelIcon.setImageDrawable(Utils.themeDrawable(widget.getContext(), ContextCompat.getDrawable(widget.getContext(), icon)));
        } catch (Throwable e) {logger.error("Error setting channel icon in title bar", e);}
        }));
    }

    public static void setVoiceIcon(Resources resources) {
        patcher.patch(WidgetChannelsListAdapter.ItemChannelVoice.class, "onConfigure", new Class<?>[]{int.class, ChannelListItem.class}, new Hook(callFrame -> {
        try {
            WidgetChannelsListAdapter.ItemChannelVoice _this = (WidgetChannelsListAdapter.ItemChannelVoice) callFrame.thisObject;
            ChannelListItem channelListItem = (ChannelListItem) callFrame.args[1];
            ChannelListItemVoiceChannel channelItem = (ChannelListItemVoiceChannel) channelListItem;
            Channel apiChannel = channelItem.getChannel();
            ChannelWrapper channel = new ChannelWrapper(apiChannel);
            var icon = Utils.getChannelIcon(channel);
            if(icon != null) {
            WidgetChannelsListItemChannelVoiceBinding binding = (WidgetChannelsListItemChannelVoiceBinding) ReflectUtils.getField(_this, "binding");
            ((ImageView) binding.getRoot().findViewById(com.aliucord.Utils.getResId("channels_item_voice_channel_speaker", "id"))).setImageResource(icon);
            }
        } catch (Throwable e) {logger.error("Error setting channel icon", e);}
        }));
    }

    public static void setTextIcon(Resources resources) {
        patcher.patch(WidgetChannelsListAdapter.ItemChannelText.class, "onConfigure", new Class<?>[]{int.class, ChannelListItem.class}, new Hook(callFrame -> {
        try {
            WidgetChannelsListAdapter.ItemChannelText _this = (WidgetChannelsListAdapter.ItemChannelText) callFrame.thisObject; ChannelListItem channelListItem = (ChannelListItem) callFrame.args[1]; ChannelListItemTextChannel channelItem = (ChannelListItemTextChannel) channelListItem; Channel apiChannel = channelItem.getChannel(); ChannelWrapper channel = new ChannelWrapper(apiChannel);
            WidgetChannelsListItemChannelBinding binding = (WidgetChannelsListItemChannelBinding) ReflectUtils.getField(_this, "binding");
            ImageView channelIcon = (ImageView) binding.getRoot().findViewById(com.aliucord.Utils.getResId("channels_item_channel_hash", "id"));
            if(channel.isGuild()) {Guild guild = StoreStream.getGuilds().getGuilds().get(channel.getGuildId());if(guild.getRulesChannelId() != null){if(guild.getRulesChannelId() == channel.getId()) {channelIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_rules_24dp", "drawable", "xyz.wingio.plugins"), null));};}}
            if(channel.getId() == 811275162715553823L || channel.getId() == 845784407846813696L){channelIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_plugin_24dp", "drawable", "xyz.wingio.plugins"), null));}

            if(Utils.getChannelIcon(channel) != null) channelIcon.setImageResource(Utils.getChannelIcon(channel));
        } catch (Throwable e) {logger.error("Error setting channel icon", e);}
        }));
    }
}