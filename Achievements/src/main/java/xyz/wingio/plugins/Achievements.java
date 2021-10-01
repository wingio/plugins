package xyz.wingio.plugins;

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

import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.Constants;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.utils.ReflectUtils;
import com.aliucord.views.Divider;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.annotations.AliucordPlugin;

import xyz.wingio.plugins.achievements.*;

import com.discord.app.AppBottomSheet;
import com.discord.databinding.WidgetSettingsBinding;
import com.discord.utilities.color.ColorCompat;
import com.discord.widgets.settings.WidgetSettings;
import com.lytefast.flexinput.R;

import java.util.*;

@AliucordPlugin
@SuppressWarnings({ "unchecked", "unused" })
public class Achievements extends Plugin {
  private Drawable pluginIcon;

  public Achievements() {
      settingsTab = new SettingsTab(PluginSettings.class).withArgs(settings, this);
      needsResources = true;
  }

  public static Logger logger = new Logger("Achievements");
  
  public List<Achievement> basics = new LinkedList<>() {{
    add(new Achievement("Baby Steps", "Open achievement list for the first time!", "babysteps"));
    add(new Achievement("Threading the Needle", "Participate in a thread", "usethread"));
    add(new Achievement("Showing Appreciation", "React with a star to a message", "addstar"));
  }};
    
  public static final Map<String, Achievement> pluginAchs = new HashMap<>();

  public Achievement createAchievement(String name, String description, String id) {
    Achievement achievement = new Achievement(name, description, id);
    pluginAchs.put(id, achievement);
    return achievement;
  }

  @Override
  @SuppressWarnings({ "unchecked", "ConstantConditions" })
  public void start(Context context) throws Throwable{
    
    logger.tag = "[Achievements]";

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
      resources.getIdentifier("ic_trophy", "drawable", "com.aliucord.plugins"),
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
        
        var expview = new TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Icon);
        expview.setId(achId);
        expview.setText("Achievements");
        expview.setTypeface(font);

        var icon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_trophy", "drawable", "com.aliucord.plugins"), null);
        icon = icon.mutate();
        icon.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        expview.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

        expview.setOnClickListener(e -> {
          try {
            var p = PluginManager.plugins.get("Achievements");
            if (p.settingsTab.type == Plugin.SettingsTab.Type.PAGE && p.settingsTab.page != null) {
                  Fragment page = p.settingsTab.args != null
                          ? ReflectUtils.invokeConstructorWithArgs(p.settingsTab.page, p.settingsTab.args)
                          : p.settingsTab.page.newInstance();
                  Utils.openPageWithProxy(ctx, page);
            }
          } catch (Throwable th){
            PluginManager.logger.error(ctx, "Failed to open achievements list", th);
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
