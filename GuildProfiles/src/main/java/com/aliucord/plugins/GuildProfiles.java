package com.aliucord.plugins;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;
import android.os.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.aliucord.plugins.guildprofiles.*;
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
import com.discord.widgets.user.usersheet.WidgetUserSheet;
import com.discord.widgets.guilds.profile.*;
import com.discord.utilities.icon.*;
import com.discord.models.member.GuildMember;
import com.discord.models.guild.Guild;
import com.discord.models.user.User;
import com.lytefast.flexinput.R;

import java.util.*;
import java.lang.reflect.*;

public class GuildProfiles extends Plugin {

    public GuildProfiles() {
        settingsTab = new SettingsTab(PluginSettings.class).withArgs(settings);
        needsResources = true;
    }
    
    public RelativeLayout overlay;

    @NonNull
  @Override
  public Manifest getManifest() {
    var manifest = new Manifest();
    manifest.authors =
      new Manifest.Author[] {
        new Manifest.Author("Wing", 298295889720770563L),
      };
    manifest.description = "Adds more server information to the server profile sheet";
    manifest.version = "1.0.0";
    manifest.updateUrl =
      "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    return manifest;
  }

    @Override
    public void start(Context context) throws Throwable {
        final int sheetId = Utils.getResId("guild_profile_sheet_actions", "id");
        final int infoId = View.generateViewId();
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
              info.setId(infoId);
              info.setOrientation(LinearLayout.VERTICAL);
              info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
              info.setBackgroundColor(Color.TRANSPARENT);
              info.setPadding(0, 0, 0, 0);
              if(layout.findViewById(infoId) == null) {
                boolean showCreatedAt = settings.getBool("createdAt", true);
                boolean showJoinedAt = settings.getBool("joinedAt", true);
                boolean showVanity = settings.getBool("vanityUrl", true);
                boolean showOwner = settings.getBool("owner", true);
                boolean showLocale = settings.getBool("locale", true);

                boolean hasVanity = guild.canHaveVanityURL();
                User owner = StoreStream.getUsers().getUsers().get(guild.getOwnerId());

                if(showCreatedAt) {
                  addInfo(ctx, info, "Created At", String.valueOf(TimeUtils.toReadableTimeString(context, SnowflakeUtils.toTimestamp(state.component1()), clock)), null);
                }

                if(showJoinedAt) {
                  addInfo(ctx, info, "Joined At", String.valueOf(TimeUtils.getReadableTimeString(context, guild.getJoinedAt())), null);
                }

                if(showVanity && hasVanity && guild.getVanityUrlCode() != null) {
                  addInfo(ctx, info, "Vanity URL", "discord.gg/" + guild.getVanityUrlCode(), null);
                }

                if(showOwner && owner != null) {
                  String discrim = String.valueOf(owner.getDiscriminator());
                  while(discrim.length() < 4){
                    discrim = "0" + discrim;
                  }
                  addInfo(ctx, info, "Owner", owner.getUsername() + "#" + discrim, e -> {
                      var fm = context.supportFragmentManager;
                      WidgetUserSheet.Companion.show(owner.getId(), fm);
                      return true;
                  });
                }
                
                if(showLocale && guild.getPreferredLocale() != null) {
                    addInfo(ctx, info, "Language", guild.getPreferredLocale(), null);
                }
                
                layout.addView(info, 0);
              }

              
            } catch (Throwable e) {
              Logger logger = new Logger("GuildProfiles");
              logger.error("Error adding guild info", e);
            }
        }));
    }

    public void addInfo(Context c, LinearLayout layout, String name, String value, @Nullable View.OnLongClickListener listener) {
      LinearLayout section = new LinearLayout(c);
      section.setOrientation(LinearLayout.VERTICAL);
      section.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
      section.setBackgroundColor(Color.TRANSPARENT);
      section.setPadding(0, Utils.dpToPx(8), 0, 0);

      if(listener != null) {
        section.setOnLongClickListener(listener);
      } else {
        section.setOnLongClickListener(e -> {
          Utils.setClipboard(name, value);
          Utils.showToast(c, "Copied to clipboard");
          return true;
        });
      }

      

      TextView header = new TextView(c, null, 0, R.h.UserProfile_Section_Header);
      header.setText(name);
      header.setTypeface(ResourcesCompat.getFont(c, Constants.Fonts.whitney_bold));
      section.addView(header);

      TextView info = new TextView(c, null, 0, R.h.UserProfile_Section_Header);
      info.setText(value);
      info.setTypeface(ResourcesCompat.getFont(c, Constants.Fonts.whitney_semibold));
      section.addView(info);
      layout.addView(section);
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
