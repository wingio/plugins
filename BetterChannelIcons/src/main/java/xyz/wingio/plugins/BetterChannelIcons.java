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

import com.aliucord.utils.*;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.NotificationsAPI;
import com.aliucord.entities.NotificationData;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.Hook;
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
  private Drawable pluginIcon;

  @Override
  public void start(Context context) throws Throwable {
    pluginIcon = ContextCompat.getDrawable(context, R.e.ic_channel_text_white_a60_24dp);
    
    boolean hasConverted = settings.getBool("hasConverted", false);
    if(!hasConverted){
      PluginManager.disablePlugin("BetterChannelIcons");
      Map<String, Integer> oldIcons = settings.getObject("icons", new HashMap<>(), Utils.oldIconStoreType);
      settings.setObject("icons", Utils.convertToNewFormat(oldIcons));
      settings.setBool("hasConverted", true);
      logger.debug("Converted old icons to new format");
      PluginManager.enablePlugin("BetterChannelIcons");
      return;
    }

    Patches.addChannelAction();
    Patches.setTextIcon(resources);
    Patches.setVoiceIcon(resources);
    Patches.setToolbarIcon(resources);
  }
  
  @Override
  public void stop(Context context) { patcher.unpatchAll(); }
}