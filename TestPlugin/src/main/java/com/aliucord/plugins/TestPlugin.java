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
import com.aliucord.utils.RxUtils;
import com.discord.stores.StoreStream;
import com.discord.api.channel.Channel;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.api.commands.CommandChoice;
import com.discord.app.AppBottomSheet;
import com.discord.models.commands.ApplicationCommandOption;
import com.discord.models.member.GuildMember;
import com.discord.models.user.User;
import com.discord.models.user.MeUser;
import com.discord.models.user.CoreUser;
import com.discord.models.message.Message;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.user.UserUtils;
import com.discord.utilities.icon.IconUtils;
import com.discord.views.CheckedSetting;
import com.discord.views.RadioManager;
import com.lytefast.flexinput.*;
import rx.Subscription;

import com.aliucord.plugins.testplugin.*;

import java.util.*;

@SuppressWarnings({ "unchecked", "unused" })
public class TestPlugin extends Plugin {
  private Drawable pluginIcon;

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
    var ach = PluginManager.plugins.get("Achievements");
    var testAch;
    if(ach != null) {
      testAch = ach.getClass().getMethod("addAchievement").invoke(context, "Test", "Description", "tp_test");
    }
    RxUtils.subscribe(RxUtils.onBackpressureBuffer(StoreStream.getGatewaySocket().getMessageCreate()), RxUtils.createActionSubscriber(message -> {
			if (message == null) return;
			Message modelMessage = new Message(message);
      MeUser currentUser = StoreStream.getUsers().getMe();
			CoreUser coreUser = new CoreUser(modelMessage.getAuthor());
			if (modelMessage.getEditedTimestamp() == null && coreUser.getId() == currentUser.getId() && StoreStream.getChannelsSelected().getId() == modelMessage.getChannelId()) {
				Utils.log("[" + currentUser.getUsername() + "] " + modelMessage.getContent());
        if(modelMessage.getContent().contains("tp_trigger")) {
          if(testAch != null) {
            testAch.unlock();
          }
        }
			}
		}));

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
