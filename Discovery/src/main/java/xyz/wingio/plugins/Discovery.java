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
import androidx.recyclerview.widget.LinearLayoutManager;

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
  private boolean hasAddedBtn = false;

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

    patchHubAction(patcher);

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
      TextView dev = (TextView) v.findViewById(Utils.getResId("developer_options", "id"));
      if(dev != null) dev.setOnLongClickListener(tv -> {
        Utils.openPageWithProxy(ctx, new UITestingPage(this));
        return true;
      });
    }));
  }

  public void patchHubAction(PatcherAPI patcher) {
    patcher.patch(WidgetGuildsList.class, "configureUI", new Class<?>[] {WidgetGuildsListViewModel.ViewState.class}, new Hook(callFrame -> {
      WidgetGuildsList _this = (WidgetGuildsList) callFrame.thisObject;
      WidgetGuildsListViewModel.ViewState viewState = (WidgetGuildsListViewModel.ViewState) callFrame.args[0];
      if (viewState instanceof WidgetGuildsListViewModel.ViewState.Loaded) {
        try{
          WidgetGuildListAdapter widgetGuildListAdapter = (WidgetGuildListAdapter) ReflectUtils.getField(_this, "adapter");
          WidgetGuildsListViewModel.ViewState.Loaded loaded = (WidgetGuildsListViewModel.ViewState.Loaded) viewState;
          List<GuildListItem> items = loaded.getItems();
          boolean useHubAction = settings.getBool("useHubAction", false);
          if((items.get(items.size() - 2) instanceof GuildListItem.HubItem && items.get(items.size() - 3) instanceof GuildListItem.HubItem) == false) {
            if(!useHubAction) items.add(items.size() - 2, new GuildListItem.HubItem(false));
          }
          widgetGuildListAdapter.setItems(items, false);
        } catch(Throwable e) {logger.error("Couldn't add discovery icon", e);}
      }
    }));

    patcher.patch(WidgetGuildListAdapter.class, "onBindViewHolder", new Class<?>[]{GuildListViewHolder.class, int.class}, new Hook(callFrame -> {
      try {
        WidgetGuildListAdapter _this = (WidgetGuildListAdapter) callFrame.thisObject;
        GuildListViewHolder holder = (GuildListViewHolder) callFrame.args[0];
        int pos = (int) callFrame.args[1];
        List<GuildListItem> items = (List<GuildListItem>) ReflectUtils.getField(_this, "items");
        GuildListItem guildListItem = (GuildListItem) items.get(pos);
        if(pos == items.size() - 2) {
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
        } else if(pos == items.size() - 3) {
          if(guildListItem instanceof GuildListItem.HubItem){ FrameLayout layout = (FrameLayout) holder.itemView; ImageView icon = (ImageView) layout.getChildAt(1);icon.setImageResource(R.d.ic_hub_24dp); holder.itemView.setOnClickListener(new WidgetGuildListAdapter$onBindViewHolder$2(_this, holder, guildListItem)); }
        }
      } catch (Throwable e) {
        logger.error("Couldnt change hub icon", e);
      }
    }));
  }

  @Override
  public void stop(Context context) { patcher.unpatchAll(); }
}

