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
import androidx.fragment.app.Fragment;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.plugins.testplugin.*;
import com.discord.utilities.color.ColorCompat;
import com.discord.api.premium.PremiumTier;
import com.discord.databinding.WidgetChatOverlayBinding;
import com.discord.databinding.WidgetGuildProfileSheetBinding;
import com.discord.utilities.viewbinding.FragmentViewBindingDelegate;
import com.discord.utilities.SnowflakeUtils;
import com.discord.utilities.time.ClockFactory;
import com.discord.utilities.time.TimeUtils;
import com.discord.stores.StoreStream;
import com.discord.widgets.chat.*;
import com.discord.widgets.chat.input.*;
import com.discord.widgets.chat.overlay.WidgetChatOverlay$binding$2;
import com.discord.widgets.changelog.WidgetChangeLog;
import com.discord.widgets.guilds.profile.*;
import com.discord.utilities.icon.*;
import com.discord.models.member.GuildMember;
import com.discord.models.guild.Guild;
import com.discord.models.user.User;
import com.lytefast.flexinput.R;

import java.util.*;
import java.lang.reflect.*;

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
        
        
        patcher.patch(WidgetGuildProfileSheet.class, "configureUI", new Class<?>[]{ WidgetGuildProfileSheetViewModel.ViewState.Loaded.class }, new PinePatchFn(callFrame -> {
            WidgetGuildProfileSheet _this = (WidgetGuildProfileSheet) callFrame.thisObject;
            WidgetGuildProfileSheetViewModel.ViewState.Loaded state = (WidgetGuildProfileSheetViewModel.ViewState.Loaded) callFrame.args[0];
            Guild guild = StoreStream.getGuilds().getGuilds().get(state.component1());
            try {
              var iconField = _this.getClass().getDeclaredField("binding$delegate");
              iconField.setAccessible(true);
              FragmentViewBindingDelegate d = (FragmentViewBindingDelegate) iconField.get(_this);
              WidgetGuildProfileSheetBinding binding = (WidgetGuildProfileSheetBinding) d.getValue((Fragment) _this, _this.$$delegatedProperties[0]);
              NestedScrollView lo = (NestedScrollView) binding.getRoot();
              LinearLayout layout = (LinearLayout) lo.findViewById(sheetId);
              Context ctx = layout.getContext();
              var clock = ClockFactory.get();

              LinearLayout info = new LinearLayout(ctx);
              int infoId = View.generateViewId();
              info.setId(infoId);
              info.setOrientation(LinearLayout.VERTICAL);
              info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
              info.setBackgroundColor(Color.TRANSPARENT);
              info.setPadding(0, 0, 0, 0);

              // TextView textView = new TextView(ctx, null, 0, R.h.UserProfile_Section_Header);
              // textView.setText("Created At");
              // textView.setTypeface(ResourcesCompat.getFont(context, Constants.Fonts.whitney_semibold));
              // info.addView(textView);

              // TextView joinDate = new TextView(ctx, null, 0, R.h.UserProfile_Section_Header);
              // joinDate.setText();
              // joinDate.setId(View.generateViewId());
              // joinDate.setTypeface(ResourcesCompat.getFont(context, Constants.Fonts.whitney_semibold));
              // info.addView(joinDate);
              
              addInfo(ctx, info, "Created At", String.valueOf(TimeUtils.toReadableTimeString(context, SnowflakeUtils.toTimestamp(state.component1()), clock)));

              layout.addView(info, 0);
            } catch (Throwable e) {
              Logger logger = new Logger("TestPlugin");
              logger.error("Error adding guild info", e);
            }
        }));
    }

    public void addInfo(Context c, LinearLayout layout, String name, String value) {
      TextView header = new TextView(c, null, 0, R.h.UserProfile_Section_Header);
      header.setText(name);
      header.setTypeface(ResourcesCompat.getFont(c, Constants.Fonts.whitney_semibold));
      layout.addView(header);

      TextView info = new TextView(c, null, 0, R.h.UserProfile_Section_Header);
      info.setText(value);
      info.setTypeface(ResourcesCompat.getFont(c, Constants.Fonts.whitney_semibold));
      layout.addView(info);
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
