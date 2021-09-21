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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.wrappers.*;
import com.aliucord.utils.ReflectUtils;

import com.discord.api.channel.Channel;
import com.discord.databinding.WidgetChannelsListItemChannelVoiceBinding;
import com.discord.models.guild.Guild;
import com.discord.stores.*;
import com.discord.widgets.channels.list.WidgetChannelsListAdapter;
import com.discord.widgets.channels.list.items.*;

import com.lytefast.flexinput.R;

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;

@AliucordPlugin
public class BetterChannelIcons extends Plugin {

  public BetterChannelIcons() {
    needsResources = true;
  }
  
  public Logger logger = new Logger("BetterChannelIcons");

  @NonNull
  @Override
  public Manifest getManifest() {
    var manifest = new Manifest();
    manifest.authors =
    new Manifest.Author[] {
    new Manifest.Author("Wing", 298295889720770563L),
    };
    manifest.description = "Adds an array of new channel icons";
    manifest.version = "1.0.1";
    manifest.updateUrl =
    "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    return manifest;
  }

  @Override
  public void start(Context context) throws Throwable {
    int sectionId = View.generateViewId();
    patcher.patch(WidgetChannelsListAdapter.ItemChannelText.class, "getHashIcon", new Class<?>[]{ChannelListItemTextChannel.class}, new PinePatchFn(callFrame -> {
      try {
        ChannelListItemTextChannel channelItem = (ChannelListItemTextChannel) callFrame.args[0];
        Channel apiChannel = channelItem.getChannel();
        ChannelWrapper channel = new ChannelWrapper(apiChannel);
        var icon = getChannelIcon(channel);
        if(icon != null) callFrame.setResult(icon);
      } catch (Throwable e) {logger.error("Error setting channel icon", e);}
    }));

    patcher.patch(WidgetChannelsListAdapter.ItemChannelText.class, "getAnnouncementsIcon", new Class<?>[]{ChannelListItemTextChannel.class}, new PinePatchFn(callFrame -> {
      try {
        ChannelListItemTextChannel channelItem = (ChannelListItemTextChannel) callFrame.args[0];
        Channel apiChannel = channelItem.getChannel();
        ChannelWrapper channel = new ChannelWrapper(apiChannel);
        var icon = getChannelIcon(channel);
        if(icon != null) callFrame.setResult(icon);
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
  }

  private Integer getChannelIcon(ChannelWrapper channel) {
    var name = channel.getName().toLowerCase();
    if(channel.isGuild()) {
      Guild guild = StoreStream.getGuilds().getGuilds().get(channel.getGuildId());
      if(guild.getRulesChannelId() != null){if(guild.getRulesChannelId() == channel.getId()) return R.d.ic_info_24dp;}
    }
    if(name.endsWith("-logs") || name.endsWith("-log")) return R.d.ic_channels_24dp;
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
    put("music", R.d.ic_headset_24dp);
  }};

  private Map<String, Integer> voiceChannelIcons = new HashMap<String, Integer>() {{
    put("music", R.d.ic_headset_24dp);
  }};


  @Override
  public void stop(Context context) { patcher.unpatchAll(); }
}

