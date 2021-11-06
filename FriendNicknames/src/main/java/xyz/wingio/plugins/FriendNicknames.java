package com.aliucord.plugins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;
import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.api.CommandsAPI;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.Hook;
import com.aliucord.widgets.LinearLayout;
import com.aliucord.annotations.AliucordPlugin;
import com.discord.api.channel.Channel;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.api.commands.CommandChoice;
import com.discord.app.AppBottomSheet;
import com.discord.models.commands.ApplicationCommandOption;
import com.discord.models.member.GuildMember;
import com.discord.models.user.User;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.user.UserUtils;
import com.discord.utilities.icon.IconUtils;
import com.discord.views.CheckedSetting;
import com.discord.views.RadioManager;
import com.lytefast.flexinput.R;
import java.util.*;

@AliucordPlugin
@SuppressWarnings({ "unchecked", "unused" })
public class FriendNicknames extends Plugin {
  private Drawable pluginIcon;
  
  public static final class PluginSettings extends AppBottomSheet {
        public int getContentViewResId() { return 0; }
        private final SettingsAPI settings;
        public PluginSettings(SettingsAPI settings) {
            this.settings = settings;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            SettingsAPI sets = PluginManager.plugins.get("FriendNicknames").settings;
            Context context = inflater.getContext();
            LinearLayout layout = new LinearLayout(context);
            layout.setBackgroundColor(ColorCompat.getThemedColor(context, R.b.colorBackgroundPrimary));

            layout.addView(createSwitch(context, sets, "showUsername", "Show Username", "Adds the username in parenthesis after the nickname", false));
            return layout;
        }

        private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
            CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
            cs.setChecked(sets.getBool(key, defaultValue));
            cs.setOnCheckedListener(c -> sets.setBool(key, c));
            return cs;
        }
    }

    public FriendNicknames() {
        settingsTab = new SettingsTab(PluginSettings.class, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings);
        needsResources = true;
    }

  @Override
  @SuppressWarnings({ "unchecked", "ConstantConditions" })
  public void start(Context context) {
    patcher.patch(
      GuildMember.Companion.getClass(),
      "getNickOrUsername",
      new Class<?>[] {
        User.class,
        GuildMember.class,
        Channel.class,
        List.class,
      },
      new Hook(
        callFrame -> {
          var user = (User) callFrame.args[0];
          var userId = user.getId();
          var nickname = settings.getString(String.valueOf(userId), null);
          if (nickname == null) return;
          var showUsername = settings.getBool("showUsername", false);
          if(showUsername == true) nickname = nickname + " (" + user.getUsername() + ")";
          callFrame.setResult(nickname);
        }
      )
    );

    pluginIcon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_editfriend", "drawable", "com.aliucord.plugins"),null);

    // patcher.patch(
    //   IconUtils.class,
    //   "getForUser",
    //   new Class<?>[] {
    //     User.class
    //   },
    //   new Hook(
    //     callFrame -> {
    //       var user = (User) callFrame.args[0];
    //       Utils.log(String.valueOf(user.getId()));
    //       callFrame.setResult("https://aperii.com/logo_circle.png");
    //     }
    //   )
    // );

    var userOption = Utils.createCommandOption(ApplicationCommandType.USER, "user", "User you want to set a nickname to", null, true, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false);
    var nickOption = Utils.createCommandOption(ApplicationCommandType.STRING, "nickname", "The nickname", null, true, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false);
    var setOption = Utils.createCommandOption(ApplicationCommandType.SUBCOMMAND, "set", "Set a nickname", null, true, true, new ArrayList<>(), new ArrayList<>(), Arrays.asList(userOption, nickOption), false);
    var clearOption = Utils.createCommandOption(ApplicationCommandType.SUBCOMMAND, "clear", "Clear a nickname", null, true, true, new ArrayList<>(), new ArrayList<>(), Arrays.asList(userOption), false);

    commands.registerCommand(
      "nick",
      "Modify a nickname for a particular user",
      Arrays.asList(setOption, clearOption),
      ctx -> {
        if (ctx.containsArg("set")) {
          var setargs = ctx.getSubCommandArgs("set");
          var user = (String) setargs.get("user");
          var nickname = (String) setargs.get("nickname");
          if ( user == null || user.equals("") || nickname == null || nickname.equals("")) {
            return new CommandsAPI.CommandResult(
              "Missing arguments",
              null,
              false
            );
          }

          settings.setString(user, String.valueOf(nickname));

          return new CommandsAPI.CommandResult("Set nickname", null, false);
        }

        if (ctx.containsArg("clear")) {
          var setargs = ctx.getSubCommandArgs("clear");
          var user = (String) setargs.get("user");
          if (user == null || user.equals("")) {
            return new CommandsAPI.CommandResult(
              "Missing arguments",
              null,
              false
            );
          }

          settings.setString(user, null);

          return new CommandsAPI.CommandResult("Cleared nickname", null, false);
        }

        return new CommandsAPI.CommandResult();
      }
    );
  }

  @Override
  public void stop(Context context) {
    patcher.unpatchAll();
    commands.unregisterAll();
  }
}
