package xyz.wingio.plugins;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import android.os.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;

import xyz.wingio.plugins.discovery.*;
import xyz.wingio.plugins.discovery.api.*;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.CommandsAPI;
import com.aliucord.api.PatcherAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.wrappers.*;
import com.aliucord.utils.*;

import com.discord.widgets.settings.WidgetSettings;
import com.discord.widgets.guilds.list.*;
import com.discord.utilities.color.ColorCompat;

import com.lytefast.flexinput.R;

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;

@AliucordPlugin
public class Discovery extends Plugin {

  public Discovery() {
    settingsTab = new SettingsTab(PluginSettings.class, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings);
    needsResources = true;
  }
  
  public List<DiscoveryGuild> cache = new ArrayList<DiscoveryGuild>();
  public int totalDiscoveryServers = 0;
  private Drawable pluginIcon;

  public List<DiscoveryGuild> updateCache(DiscoveryResult prev) {
    cache.addAll(prev.guilds);
    return cache;
  }

  public void setTotalDiscoveryServers(int total) {
    totalDiscoveryServers = total;
  }

  public Logger logger = new Logger("Discovery");
  private int btnID = View.generateViewId();

  @Override
  public void start(Context context) throws Throwable {
    pluginIcon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_discovery_24dp", "drawable", "com.aliucord.plugins"), null);

    boolean replaceHubAction = settings.getBool("replaceHubAction", true);

    if(replaceHubAction) replaceHubAction(patcher);

    patcher.patch(WidgetSettings.class, "onViewBound", new Class<?>[]{ View.class }, new Hook(callFrame -> {
      CoordinatorLayout view = (CoordinatorLayout) callFrame.args[0];
      LinearLayoutCompat v = (LinearLayoutCompat) ((NestedScrollView) view.getChildAt(1)).getChildAt(0);
      Context ctx = v.getContext();
      int baseIndex = v.indexOfChild(v.findViewById(Utils.getResId("qr_scanner", "id")));
      TextView option = new TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Icon);
      option.setText("Discovery");
      Drawable icon = pluginIcon.mutate();
      option.setCompoundDrawablesWithIntrinsicBounds(Utils.tintToTheme(icon), null, null, null);
      option.setOnClickListener(w -> {
        Utils.openPageWithProxy(ctx, new DiscoveryPage(this));
      });

      v.addView(option, baseIndex + 1);
    }));
  }

  public void replaceHubAction(PatcherAPI patcher) {
    patcher.patch(WidgetGuildListAdapter.class, "onBindViewHolder", new Class<?>[]{GuildListViewHolder.class, int.class}, new Hook(callFrame -> {
      try {
        WidgetGuildListAdapter _this = (WidgetGuildListAdapter) callFrame.thisObject;
        GuildListViewHolder holder = (GuildListViewHolder) callFrame.args[0];
        List<GuildListItem> items = (List<GuildListItem>) ReflectUtils.getField(_this, "items");
        GuildListItem guildListItem = (GuildListItem) items.get((int) callFrame.args[1]);
        if(guildListItem instanceof GuildListItem.HubItem){
          FrameLayout layout = (FrameLayout) holder.itemView;
          ImageView icon = (ImageView) layout.getChildAt(1);
          Drawable compass = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_discovery_24dp", "drawable", "com.aliucord.plugins"), null);
          compass.mutate();
          icon.setImageDrawable(compass);
          
          holder.itemView.setOnClickListener(v -> {
            Utils.openPageWithProxy(v.getContext(), new DiscoveryPage(this));
          });
        }
      } catch (Throwable e) {
        logger.error("Couldnt change hub icon", e);
      }
    }));
  }

  @Override
  public void stop(Context context) { patcher.unpatchAll(); }
}

