package xyz.wingio.plugins;

import com.google.android.material.chip.ChipGroup;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import android.os.*;

import androidx.annotation.NonNull;
import androidx.annotation.DrawableRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.NotificationsAPI;
import com.aliucord.entities.NotificationData;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.wrappers.*;
import com.aliucord.utils.ReflectUtils;

import com.discord.api.channel.Channel;
import com.discord.databinding.*;
import com.discord.models.guild.Guild;
import com.discord.stores.*;
import com.discord.widgets.channels.list.WidgetChannelsListAdapter;
import com.discord.widgets.channels.list.items.*;
import com.discord.widgets.home.*;
import com.discord.utilities.color.ColorCompat;

import com.google.gson.reflect.TypeToken;

import com.lytefast.flexinput.R;

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;
import kotlin.Unit;

import xyz.wingio.plugins.betterchannelicons.*;

@AliucordPlugin
public class BetterChannelIcons extends Plugin {

  public BetterChannelIcons() {
    settingsTab = new SettingsTab(PluginSettings.class).withArgs(settings);
    needsResources = true;
  }
  
  public Logger logger = new Logger("BetterChannelIcons");
  public static final Type iconStoreType = TypeToken.getParameterized(HashMap.class, String.class, String.class).getType();
  public static final Type oldIconStoreType = TypeToken.getParameterized(HashMap.class, String.class, Integer.class).getType();
  private Drawable pluginIcon;

  @Override
  public void start(Context context) throws Throwable {
    pluginIcon = ContextCompat.getDrawable(context, R.d.ic_channel_text_white_a60_24dp);
    
    boolean hasConverted = settings.getBool("hasConverted", false);
    if(!hasConverted){
      PluginManager.disablePlugin("BetterChannelIcons");
      Map<String, Integer> oldIcons = settings.getObject("icons", new HashMap<>(), oldIconStoreType);
      settings.setObject("icons", convertToNewFormat(oldIcons));
      settings.setBool("hasConverted", true);
      logger.debug("Converted old icons to new format");
      PluginManager.enablePlugin("BetterChannelIcons");
      return;
    }

    patcher.patch(WidgetChannelsListAdapter.ItemChannelText.class, "onConfigure", new Class<?>[]{int.class, ChannelListItem.class}, new PinePatchFn(callFrame -> {
      try {
        WidgetChannelsListAdapter.ItemChannelText _this = (WidgetChannelsListAdapter.ItemChannelText) callFrame.thisObject; ChannelListItem channelListItem = (ChannelListItem) callFrame.args[1]; ChannelListItemTextChannel channelItem = (ChannelListItemTextChannel) channelListItem; Channel apiChannel = channelItem.getChannel(); ChannelWrapper channel = new ChannelWrapper(apiChannel);
        WidgetChannelsListItemChannelBinding binding = (WidgetChannelsListItemChannelBinding) ReflectUtils.getField(_this, "binding");
        ImageView channelIcon = (ImageView) binding.getRoot().findViewById(Utils.getResId("channels_item_channel_hash", "id"));
        if(channel.isGuild()) {Guild guild = StoreStream.getGuilds().getGuilds().get(channel.getGuildId());if(guild.getRulesChannelId() != null){if(guild.getRulesChannelId() == channel.getId()) {channelIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_rules_24dp", "drawable", "xyz.wingio.plugins"), null));};}}
        if(channel.getId() == 811275162715553823L || channel.getId() == 845784407846813696L){channelIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_plugin_24dp", "drawable", "xyz.wingio.plugins"), null));}

        if(getChannelIcon(channel) != null) channelIcon.setImageResource(getChannelIcon(channel));
      } catch (Throwable e) {logger.error("Error setting channel icon", e);}
    }));

    patcher.patch(WidgetChannelsListAdapter.ItemChannelVoice.class, "onConfigure", new Class<?>[]{int.class, ChannelListItem.class}, new PinePatchFn(callFrame -> {
      try {
        WidgetChannelsListAdapter.ItemChannelVoice _this = (WidgetChannelsListAdapter.ItemChannelVoice) callFrame.thisObject;
        ChannelListItem channelListItem = (ChannelListItem) callFrame.args[1];
        ChannelListItemVoiceChannel channelItem = (ChannelListItemVoiceChannel) channelListItem;
        Channel apiChannel = channelItem.getChannel();
        ChannelWrapper channel = new ChannelWrapper(apiChannel);
        var icon = getChannelIcon(channel);
        if(icon != null) {
          WidgetChannelsListItemChannelVoiceBinding binding = (WidgetChannelsListItemChannelVoiceBinding) ReflectUtils.getField(_this, "binding");
          ((ImageView) binding.getRoot().findViewById(Utils.getResId("channels_item_voice_channel_speaker", "id"))).setImageResource(icon);
        }
      } catch (Throwable e) {logger.error("Error setting channel icon", e);}
    }));

    patcher.patch(WidgetHomeHeaderManager.class, "configure", new Class<?>[]{ WidgetHome.class, WidgetHomeModel.class, WidgetHomeBinding.class }, new PinePatchFn(callFrame -> {
      try {
        if(!settings.getBool("setToolbarIcon", true)) return;
        WidgetHomeBinding binding = (WidgetHomeBinding) callFrame.args[2]; WidgetHomeModel model = (WidgetHomeModel) callFrame.args[1]; WidgetHome widget = (WidgetHome) callFrame.args[0]; ChannelWrapper channel = new ChannelWrapper(model.getChannel()); var root = widget.getActionBarTitleLayout().i.getRoot();
        ImageView channelIcon = (ImageView) root.findViewById(Utils.getResId("toolbar_icon", "id"));
        if(channel.isGuild()) {Guild guild = StoreStream.getGuilds().getGuilds().get(channel.getGuildId());if(guild.getRulesChannelId() != null){if(guild.getRulesChannelId() == channel.getId()) { channelIcon.setImageDrawable(themeDrawable(widget.getContext(), ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_rules_24dp", "drawable", "xyz.wingio.plugins"), null)) ); };}}
        if(channel.getId() == 811275162715553823L || channel.getId() == 845784407846813696L){ channelIcon.setImageDrawable(themeDrawable(widget.getContext(), ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_plugin_24dp", "drawable", "xyz.wingio.plugins"), null))); }

        var icon = getChannelIcon(channel);
        if (icon != null) channelIcon.setImageDrawable(themeDrawable(widget.getContext(), ContextCompat.getDrawable(widget.getContext(), icon)));
      } catch (Throwable e) {logger.error("Error setting channel icon in title bar", e);}
    }));
    
  }

  private Drawable themeDrawable(Context ctx, Drawable drawable){
    drawable = drawable.mutate();
    drawable.setTint(ColorUtils.setAlphaComponent(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal), 153));
    return drawable;
  }

  private Integer getChannelIcon(ChannelWrapper channel) throws Throwable {
    if(channel == null) return null;
    var name = channel.getName().toLowerCase();
    Map<String, String> icons = settings.getObject("icons", new HashMap<>(), iconStoreType);
    if(icons.containsKey(name)) return Utils.getResId(icons.get(name), "drawable");
    if(name.endsWith("-logs") || name.endsWith("-log")) return R.d.ic_channels_24dp;
    if(name.endsWith("-support") || name.endsWith("-help")) return R.d.ic_help_24dp;
    if(channel.getId() == 824357609778708580L) return R.d.ic_theme_24dp;
    if(channel.getType() == Channel.GUILD_VOICE) {
      if(name.startsWith("discord.gg/") || name.startsWith(".gg/") || name.startsWith("gg/") || name.startsWith("dsc.gg/")) return R.d.ic_diag_link_24dp;
      if(name.startsWith("member count") || name.startsWith("members") || name.startsWith("member count")) return R.d.ic_people_white_24dp;
      return voiceChannelIcons.get(name);
    }
    return channelIcons.get(name);
  }

  private Map<String, Integer> channelIcons = new HashMap<String, Integer>() {{
    put("faq", R.d.ic_help_24dp);
    put("help", R.d.ic_help_24dp);
    put("support", R.d.ic_help_24dp);
    put("info", R.d.ic_info_24dp);
    put("roles", R.d.ic_shieldstar_24dp);
    put("role-info", R.d.ic_shieldstar_24dp);
    put("offtopic", R.d.ic_chat_message_white_24dp);
    put("off-topic", R.d.ic_chat_message_white_24dp);
    put("general", R.d.ic_chat_message_white_24dp);
    put("general-chat", R.d.ic_chat_message_white_24dp);
    put("general-talk", R.d.ic_chat_message_white_24dp);
    put("talk", R.d.ic_chat_message_white_24dp);
    put("chat", R.d.ic_chat_message_white_24dp);
    put("art", R.d.ic_theme_24dp);
    put("fanart", R.d.ic_theme_24dp);
    put("fan-art", R.d.ic_theme_24dp);
    put("bot", R.d.ic_slash_command_24dp);
    put("bots", R.d.ic_slash_command_24dp);
    put("bot-spam", R.d.ic_slash_command_24dp);
    put("bot-commands", R.d.ic_slash_command_24dp);
    put("commands", R.d.ic_slash_command_24dp);
    put("memes", R.d.ic_emoji_picker_category_people);
    put("meme", R.d.ic_emoji_picker_category_people);
    put("meme-chat", R.d.ic_emoji_picker_category_people);
    put("shitpost", R.d.ic_emoji_picker_category_people);
    put("introductions", R.d.ic_raised_hand_action_24dp);
    put("introduce-yourself", R.d.ic_raised_hand_action_24dp);
    put("welcome", R.d.ic_raised_hand_action_24dp);
    put("welcomes", R.d.ic_raised_hand_action_24dp);
    put("intros", R.d.ic_raised_hand_action_24dp);
    put("media", R.d.ic_flex_input_image_24dp_dark);
    put("changes", R.d.ic_history_white_24dp);
    put("changelog", R.d.ic_history_white_24dp);
    put("logs", R.d.ic_channels_24dp);
    put("modlogs", R.d.ic_channels_24dp);
    put("starboard", R.d.ic_star_24dp);
    put("resources", R.d.ic_diag_link_24dp);
    put("links", R.d.ic_diag_link_24dp);
    put("socials", R.d.ic_diag_link_24dp);
    put("vc", R.d.ic_mic_grey_24dp);
    put("muted", R.d.ic_mic_grey_24dp);
    put("vc-chat", R.d.ic_mic_grey_24dp);
    put("voice-chat", R.d.ic_mic_grey_24dp);
    put("no-mic", R.d.ic_mic_grey_24dp);
    put("music", R.d.ic_headset_24dp);
    put("github", R.d.ic_github_white);
    put("github-commits", R.d.ic_github_white);
    put("github-notifications", R.d.ic_github_white);
  }};

  private Map<String, Integer> voiceChannelIcons = new HashMap<String, Integer>() {{
    put("music", R.d.ic_headset_24dp);
  }};

  private Map<String, String> convertToNewFormat(Map<String, Integer> icons) throws Throwable{
    Map<String, String> newIcons = new HashMap<>();
    Map<Integer, String> iconNameMap = Constants.getIconNameMap();
    List<String> keys = new ArrayList<>(icons.keySet());
    for(String key : keys){
      Integer iconIndex = icons.get(key);
      Integer icon = Constants.getIcons().get(iconIndex);
      newIcons.put(key, iconNameMap.get(icon));
    }
    return newIcons;
  }


  @Override
  public void stop(Context context) { patcher.unpatchAll(); }
}

