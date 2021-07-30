package com.aliucord.plugins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.aliucord.*;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.utils.ReflectUtils;
import com.aliucord.views.Divider;
import com.aliucord.fragments.SettingsPage;

import com.aliucord.plugins.achievements.*;

import com.discord.app.AppBottomSheet;
import com.discord.databinding.WidgetSettingsBinding;
import com.discord.utilities.color.ColorCompat;
import com.discord.widgets.settings.WidgetSettings;
import com.lytefast.flexinput.*;

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
  public void start(Context context) throws Throwable{
    basicAchs.put("babysteps", new Achievement(context, "Baby Steps", "Open achievement list for the first time!", "babysteps"));
    Logger achLogger = new Logger("Achievements");
    achLogger.tag = "[Achievements]";
    Achievement thrAch = new Achievement(context, "Threading the Needle", "Participate in a thread", "usethread");
    Achievement testAch = new Achievement(context, "Test Achievement", "This is a description", "test");
    // RxUtils.subscribe(RxUtils.onBackpressureBuffer(StoreStream.getGatewaySocket().getMessageCreate()), RxUtils.createActionSubscriber(message -> {
		// 	if (message == null) return;
		// 	Message modelMessage = new Message(message);
    //  MeUser currentUser = StoreStream.getUsers().getMe();
		// 	CoreUser coreUser = new CoreUser(modelMessage.getAuthor());
		// 	if (modelMessage.getEditedTimestamp() == null && coreUser.getId() == currentUser.getId() && StoreStream.getChannelsSelected().getId() == modelMessage.getChannelId()) {
    //     String content = modelMessage.getContent();
		// 		achLogger.debug("[AMS] [" + currentUser.getUsername() + "] -> " + content);
    //     if(content.contains("triggerach")) {
    //       testAch.unlock();
    //     }
		// 	}
		// }));

    pluginIcon = ResourcesCompat.getDrawable(
      resources,
      resources.getIdentifier("ic_editfriend", "drawable", "com.aliucord.plugins"),
      null
    );



    final var getBinding = WidgetSettings.class.getDeclaredMethod("getBinding");
    getBinding.setAccessible(true);
    var achId = View.generateViewId();

    patcher.patch(WidgetSettings.class.getDeclaredMethod("configureUI", WidgetSettings.Model.class), new PinePatchFn(callFrame -> {
      var widgetSettings = (WidgetSettings) callFrame.thisObject;
      WidgetSettingsBinding binding;
      try {
          binding = (WidgetSettingsBinding) getBinding.invoke(widgetSettings);
          if (binding == null) return;
      } catch (Throwable th) { return; }
      var ctx = widgetSettings.requireContext();
      var layout = (LinearLayoutCompat) ((NestedScrollView) ((CoordinatorLayout) binding.getRoot()).getChildAt(1)).getChildAt(0);
      if(layout.findViewById(achId) == null) {
        var font = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium);
        
        var expview = new TextView(ctx, null, 0, R$h.UiKit_Settings_Item_Icon);
        expview.setId(achId);
        expview.setText("Achievements");
        expview.setTypeface(font);

        var icon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_editfriend", "drawable", "com.aliucord.plugins"), null);
        icon = icon.mutate();
        icon.setTint(ColorCompat.getThemedColor(context, R$b.colorInteractiveNormal));
        expview.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

        expview.setOnClickListener(e -> {
          var p = PluginManager.plugins.get("Achievements");
          if (p.settingsTab.type == Plugin.SettingsTab.Type.PAGE && p.settingsTab.page != null) {
                Fragment page = p.settingsTab.args != null
                        ? ReflectUtils.invokeConstructorWithArgs(p.settingsTab.page, p.settingsTab.args)
                        : p.settingsTab.page.newInstance();
                Utils.openPageWithProxy(ctx, page);
            }
        });

        layout.addView(expview, 4);
      }
    }));
  }

  @Override
  public void stop(Context context) {
    patcher.unpatchAll();
    commands.unregisterAll();
  }
}
