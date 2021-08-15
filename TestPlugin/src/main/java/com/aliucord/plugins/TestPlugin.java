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
import com.aliucord.PluginManager;
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
import com.discord.widgets.guilds.profile.*;
import com.discord.utilities.icon.*;
import com.discord.models.member.GuildMember;
import com.discord.models.user.User;
import com.lytefast.flexinput.R;

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
    manifest.description = "Used for testing: avatar patch";
    manifest.version = "1.1.0";
    manifest.updateUrl =
      "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    manifest.changelog = "New Features {updated marginTop}\n======================\n\n* **Rebranded!** We are now XintoCord";
    return manifest;
  }

    @Override
    public void start(Context context) throws Throwable {
        pluginIcon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_editfriend", "drawable", "com.aliucord.plugins"), null );
        var id = View.generateViewId();
        
        patcher.patch(WidgetUrlActions.class, "onViewCreated", new Class<?>[] { View.class, Bundle.class }, new PinePatchFn(callFrame -> {
            LinearLayout view = (LinearLayout) callFrame.args[0];
            var ctx = view.getContext();
            var option = new TextView(view.getContext(), null, 0, R.h.UiKit_Settings_Item_Icon);
            option.setText("Open in External Browser");
            option.setId(id);
            if (pluginIcon != null) pluginIcon.setTint(ColorCompat.getThemedColor(view.getContext(), R.b.colorInteractiveNormal));
            option.setCompoundDrawablesRelativeWithIntrinsicBounds(pluginIcon,null,null,null);
            option.setOnClickListener(e -> {
                String body = view.getContext().getString(2131887249);
                Utils.log("Body: " + body);
                Utils.log("Last Changed: " + ctx.getString(2131887250));
                Utils.log("Revision: " + ctx.getString(2131887252));
                Utils.log("Video: " + ctx.getString(2131887253));
                String video = "https://cdn.discordapp.com/attachments/719794226673614879/872727881552396308/7_59_P.M_720P_HD.mp4";
                body = "New Features {modified marginTop}\n======================\n\n* **Rebranded** We are now XintoCord!";
                WidgetChangeLog.Companion.launch(ctx, "2021-08-05", "1", "https://cdn.discordapp.com/banners/169256939211980800/eda024c8f40a45c88265a176f0926bea.jpg?size=2048", body);
            });
           
            view.addView(option, 4);
        }));

        final int sheetId = Utils.getResId("guild_profile_sheet_actions", "id");
        
        patcher.patch(WidgetGuildProfileSheet.class, "onViewCreated", new Class<?>[]{ View.class, Bundle.class }, new PinePatchFn(callFrame -> {
            WidgetGuildProfileSheet _this = (WidgetGuildProfileSheet) callFrame.thisObject;
            //WidgetGuildProfileSheetBinding binding = _this.getBinding();
            View view = (View) callFrame.args[0];
            LinearLayout layout = (LinearLayout) view.findViewById(sheetId);
            Utils.log("sheet_id: " + sheetId);
            Utils.log("View ID: " + layout.getId());
            Context ctx = view.getContext();

            TextView textView = new TextView(ctx, null, 0, R.h.UserProfile_Section_Header);
            textView.setText("Test");
            textView.setId(View.generateViewId());
            textView.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_semibold));
            textView.setPadding(Utils.dpToPx(16), 0, 0, 0);
            layout.addView(textView, 0);
        }));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
