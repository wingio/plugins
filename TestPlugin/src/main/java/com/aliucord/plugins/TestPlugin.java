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

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.plugins.testplugin.*;
import com.discord.utilities.color.ColorCompat;
import com.discord.api.premium.PremiumTier;
import com.discord.databinding.WidgetChatOverlayBinding;
import com.discord.stores.StoreStream;
import com.discord.widgets.chat.*;
import com.discord.widgets.chat.input.*;
import com.discord.widgets.chat.overlay.WidgetChatOverlay$binding$2;
import com.discord.widgets.changelog.WidgetChangeLog;
import com.lytefast.flexinput.*;

import java.util.*;

public class TestPlugin extends Plugin {

    public TestPlugin() {
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
    manifest.description = "Used for testing: Changelog";
    manifest.version = "1.0.0";
    manifest.updateUrl =
      "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    return manifest;
  }

    @Override
    public void start(Context context) throws Throwable {
        pluginIcon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_editfriend", "drawable", "com.aliucord.plugins"), null );
        var id = View.generateViewId();
        
        patcher.patch(WidgetUrlActions.class, "onViewCreated", new Class<?>[] { View.class, Bundle.class }, new PinePatchFn(callFrame -> {
            LinearLayout view = (LinearLayout) callFrame.args[0];
            var ctx = view.getContext();
            var option = new TextView(view.getContext(), null, 0, R$h.UiKit_Settings_Item_Icon);
            option.setText("Open in External Browser");
            option.setId(id);
            if (pluginIcon != null) pluginIcon.setTint(ColorCompat.getThemedColor(view.getContext(), R$b.colorInteractiveNormal));
            option.setCompoundDrawablesRelativeWithIntrinsicBounds(pluginIcon,null,null,null);
            option.setOnClickListener(e -> {
                String body = view.getContext().getString(2131887249);
                Utils.log("Body: " + body);
                Utils.log("Last Changed: " + ctx.getString(2131887250));
                Utils.log("Revision: " + ctx.getString(2131887252));
                Utils.log("Video: " + ctx.getString(2131887253));
                String video = "https://cdn.discordapp.com/attachments/719794226673614879/872727881552396308/7_59_P.M_720P_HD.mp4";
                body = "New Features {added marginTop}\n======================\n\n* **Added Custom Changelogs** Just testing stuff out ;)\n* **Links are cool too** [See](https://aperii.com)";
                WidgetChangeLog.Companion.launch(ctx, ctx.getString(2131887250), ctx.getString(2131887252), video, body);
            });
           
            view.addView(option, 4);
        }));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
