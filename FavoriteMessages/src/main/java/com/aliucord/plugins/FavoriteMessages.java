package com.aliucord.plugins;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;
import android.os.*;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;

import com.aliucord.*;
import com.aliucord.Utils;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.api.CommandsAPI;
import com.aliucord.plugins.favoritemessages.*;
import com.discord.utilities.color.ColorCompat;
import com.discord.databinding.WidgetChatOverlayBinding;
import com.discord.stores.StoreStream;
import com.discord.widgets.chat.*;
import com.discord.widgets.chat.input.*;
import com.discord.widgets.chat.list.actions.WidgetChatListActions;
import com.discord.models.message.Message;
import com.lytefast.flexinput.*;

import java.util.*;

public class FavoriteMessages extends Plugin {

    public FavoriteMessages() {
        settingsTab = new SettingsTab(PluginSettings.class).withArgs(settings);
        needsResources = true;
    }
    
    private Drawable pluginIcon;
    public RelativeLayout overlay;

    @NonNull
  @Override
  public Manifest getManifest() {
    var manifest = new Manifest();
    manifest.authors =
      new Manifest.Author[] {
        new Manifest.Author("Wing", 298295889720770563L),
      };
    manifest.description = "Organize your favorite messages";
    manifest.version = "1.0.0";
    manifest.changelog = "Initial release";
    manifest.updateUrl =
      "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    return manifest;
  }

    @Override
    public void start(Context context) throws Throwable {
      pluginIcon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_editfriend", "drawable", "com.aliucord.plugins"), null );
      var id = View.generateViewId();
      Drawable icon = pluginIcon;
      
      patcher.patch(WidgetChatListActions.class, "configureUI", new Class<?>[]{ WidgetChatListActions.Model.class }, new PinePatchFn(callFrame -> {
        var _this = (WidgetChatListActions) callFrame.thisObject;
        var rootView = (NestedScrollView) _this.getView();
        if(rootView == null) return;
        var layout = (LinearLayout) rootView.getChildAt(0);
        if (layout == null || layout.findViewById(id) != null) return;
        var ctx = layout.getContext();
        var msg = ((WidgetChatListActions.Model) callFrame.args[0]).getMessage();
        Map<Long, StoredMessage> favorites = (Map<Long, StoredMessage>) settings.getObject("favorites", new Map<Long, StoredMessage>());
        var view = new TextView(ctx, null, 0, R$h.UiKit_Settings_Item_Icon);
        view.setId(id);
        if (icon != null) icon.setTint(
          ColorCompat.getThemedColor(ctx, R$b.colorInteractiveNormal)
        );
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(icon,null,null,null);
        Utils.log(String.valueOf(favorites));
        StoredMessage sm = (StoredMessage) favorites.get(msg.getId());
        if (sm == null) {
          view.setText("Favorite Message");
          view.setOnClickListener(e -> {
            favorites.put(msg.getId(), new StoredMessage(msg));
            settings.setObject("favorites", favorites);
            Utils.showToast(context, "Favorited Message");
            _this.dismiss();
          });
          layout.addView(view);
        } else {
          view.setText("Unfavorite Message");
          view.setOnClickListener(e -> {
            favorites.remove(msg.getId());
            settings.setObject("favorites", favorites);
            Utils.showToast(context, "Unfavorited Message");
            _this.dismiss();
          });
          layout.addView(view);
        }
        
        
      }));

      commands.registerCommand(
            "clearfavorites",
            "Lists installed plugins",
            Collections.emptyList(),
            ctx -> {
                settings.setObject("favorites", null);
                return new CommandsAPI.CommandResult("Cleared favorite messages", null, false);
            }
        );
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
