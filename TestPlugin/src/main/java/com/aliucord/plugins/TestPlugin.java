package com.aliucord.plugins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;
import com.aliucord.Constants;
import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.api.CommandsAPI;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.widgets.LinearLayout;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.views.Divider;
import com.discord.api.channel.Channel;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.api.commands.CommandChoice;
import com.discord.app.AppBottomSheet;
import com.discord.models.commands.ApplicationCommandOption;
import com.discord.models.member.GuildMember;
import com.discord.models.user.User;
import com.discord.models.user.CoreUser;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.user.UserUtils;
import com.discord.utilities.icon.IconUtils;
import com.discord.views.CheckedSetting;
import com.discord.views.RadioManager;
import com.lytefast.flexinput.*;
import java.util.*;

@SuppressWarnings({ "unchecked", "unused" })
public class TestPlugin extends Plugin {
  private Drawable pluginIcon;

    public static class PluginSettings extends SettingsPage {
        private final SettingsAPI settings;
        public PluginSettings(SettingsAPI settings) {
            this.settings = settings;
        }

        @Override
        @SuppressWarnings("ResultOfMethodCallIgnored")
        public void onViewBound(View view) {
            super.onViewBound(view);
            setActionBarTitle("Test Plugin");

            var context = view.getContext();
            
            var expHeader = new TextView(context, null, 0, R$h.UiKit_Settings_Item_Header);
            expHeader.setTypeface(ResourcesCompat.getFont(context, Constants.Fonts.whitney_semibold));
            expHeader.setText("Experiments");
            layout.addView(appearanceHeader);

            addView(createSwitch(context, settings, "allBots", "Mark everyone as bots", null, false));
            addView(new Divider(context));
        }
        
        private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
            CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
            cs.setChecked(sets.getBool(key, defaultValue));
            cs.setOnCheckedListener(c -> sets.setBool(key, c));
            return cs;
        }
    }

    public TestPlugin() {
        settingsTab = new SettingsTab(PluginSettings.class).withArgs(settings);
        needsResources = true;
    }
  

  @NonNull
  @Override
  public Manifest getManifest() {
    var manifest = new Manifest();
    manifest.authors =
      new Manifest.Author[] {
        new Manifest.Author("Wing", 298295889720770563L),
      };
    manifest.description = "Used for testing";
    manifest.version = "1.0.0";
    manifest.updateUrl =
      "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    return manifest;
  }

  @Override
  @SuppressWarnings({ "unchecked", "ConstantConditions" })
  public void start(Context context) {
    patcher.patch(
      User.class,
      "isBot",
      new Class<?>[] {},
      new PinePatchFn(
        callFrame -> {
          boolean allbots = settings.getBool("allBots", false);
          if(allbots) {
            callFrame.setResult(true);
          }
          callFrame.setResult(false);
        }
      )
    );

    pluginIcon = ResourcesCompat.getDrawable(
      resources,
      resources.getIdentifier("ic_editfriend", "drawable", "com.aliucord.plugins"),
      null
    );

  }

  @Override
  public void stop(Context context) {
    patcher.unpatchAll();
    commands.unregisterAll();
  }
}
