package com.aliucord.plugins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.drawable.Drawable;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.NestedScrollView;

import com.aliucord.*;
import com.aliucord.api.CommandsAPI;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.widgets.LinearLayout;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.views.Divider;
import com.aliucord.utils.RxUtils;
import com.aliucord.plugins.achievements.Achievement;

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
import com.discord.databinding.WidgetSettingsBinding;
import com.discord.widgets.settings.WidgetSettings;

import com.lytefast.flexinput.*;

import rx.Subscription;

import com.aliucord.plugins.achievements.*;

import java.util.*;

@SuppressWarnings({ "unchecked", "unused" })
public class Achievements extends Plugin {
  private Drawable pluginIcon;

  public Achievements() {
      settingsTab = new SettingsTab(PluginSettings.class).withArgs(settings);
      needsResources = true;
  }

  private static final Map<String, Achievement> basicAchs = new HashMap<>();
  private static final Map<String, Achievement> pluginAchs = new HashMap<>();

  public Achievement createAchievement(Context ctx, String name, String description, String id) {
    Achievement achievement = new Achievement(ctx, name, description, id);
    pluginAchs.put(id, achievement);
    return achievement;
  }

  @NonNull
  @Override
  public Manifest getManifest() {
    var manifest = new Manifest();
    manifest.authors =
      new Manifest.Author[] {
        new Manifest.Author("Wing", 298295889720770563L),
      };
    manifest.description = "Try to unlock as many achievements as you possibly can!";
    manifest.version = "1.0.0";
    manifest.updateUrl =
      "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    return manifest;
  }

  @Override
  @SuppressWarnings({ "unchecked", "ConstantConditions" })
  public void start(Context context) {
    basicAchs.put("babysteps", new Achievement(context, "Baby Steps", "Open achievement list for the first time!", "babysteps"));
    Logger achLogger = new Logger("Achievements");
    achLogger.tag = "[Achievements]";
    Achievement thrAch = new Achievement(context, "Threading the Needle", "Participate in a thread", "usethread");
    Achievement testAch = new Achievement(context, "Test Achievement", "This is a description", "test");
    RxUtils.subscribe(RxUtils.onBackpressureBuffer(StoreStream.getGatewaySocket().getMessageCreate()), RxUtils.createActionSubscriber(message -> {
			if (message == null) return;
			Message modelMessage = new Message(message);
      MeUser currentUser = StoreStream.getUsers().getMe();
			CoreUser coreUser = new CoreUser(modelMessage.getAuthor());
			if (modelMessage.getEditedTimestamp() == null && coreUser.getId() == currentUser.getId() && StoreStream.getChannelsSelected().getId() == modelMessage.getChannelId()) {
        String content = modelMessage.getContent();
				achLogger.debug("[AMS] [" + currentUser.getUsername() + "] -> " + content);
        if(content.contains("triggerach")) {
          testAch.unlock();
        }
			}
		}));

    pluginIcon = ResourcesCompat.getDrawable(
      resources,
      resources.getIdentifier("ic_editfriend", "drawable", "com.aliucord.plugins"),
      null
    );

    patcher.patch(WidgetSettings.class.getDeclaredMethod("configureUI", WidgetSettings.Model.class), new PinePatchFn(callFrame -> {
      var widgetSettings = (WidgetSettings) callFrame.thisObject;
      WidgetSettingsBinding binding;
      try {
          binding = (WidgetSettingsBinding) getBinding.invoke(widgetSettings);
          if (binding == null) return;
      } catch (Throwable th) { return; }

      var ctx = widgetSettings.requireContext();
      var layout = (LinearLayoutCompat) ((NestedScrollView) ((CoordinatorLayout) binding.getRoot()).getChildAt(1)).getChildAt(0);
      var expview = new TextView(ctx, null, 0, R$h.UiKit_Settings_Item_Icon);

      expview.setId(View.generateViewId());
      expview.setText("Achievements");
      expview.setTypeface(wm);

      var icon = ContextCompat.getDrawable(ctx, R$d.ic_slash_command_24dp);
      icon = icon.mutate();
      icon.setTint(ColorCompat.getThemedColor(ctx, R$b.colorInteractiveNormal));
      expview.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

      layout.addView(expview, 4);
    }));
  }

  @Override
  public void stop(Context context) {
    patcher.unpatchAll();
    commands.unregisterAll();
  }
}
