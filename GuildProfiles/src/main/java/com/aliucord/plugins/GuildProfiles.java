package com.aliucord.plugins;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;
import android.os.*;

import com.google.android.material.button.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.fragments.*;
import com.aliucord.plugins.guildprofiles.*;
import com.aliucord.plugins.guildprofiles.pages.*;
import com.discord.utilities.color.ColorCompat;
import com.discord.api.premium.PremiumTier;
import com.discord.api.guild.GuildVerificationLevel;
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
import com.discord.app.AppBottomSheet;
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
    manifest.version = "1.0.1";
    manifest.updateUrl =
      "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    return manifest;
  }

    @Override
    public void start(Context context) throws Throwable {
        final int sheetId = Utils.getResId("guild_profile_sheet_actions", "id");
        final int infoId = View.generateViewId();
        final int tabId = View.generateViewId();
        final int blockedId = View.generateViewId();
        patcher.patch(WidgetGuildProfileSheet.class, "configureUI", new Class<?>[]{ WidgetGuildProfileSheetViewModel.ViewState.Loaded.class }, new PinePatchFn(callFrame -> {
            WidgetGuildProfileSheet _this = (WidgetGuildProfileSheet) callFrame.thisObject;
            WidgetGuildProfileSheetViewModel.ViewState.Loaded state = (WidgetGuildProfileSheetViewModel.ViewState.Loaded) callFrame.args[0];
            var guildStore = StoreStream.getGuilds();
            Guild guild = guildStore.getGuilds().get(state.component1());
            
            try {
              var iconField = _this.getClass().getDeclaredField("binding$delegate");
              iconField.setAccessible(true);
              FragmentViewBindingDelegate d = (FragmentViewBindingDelegate) iconField.get(_this);
              WidgetGuildProfileSheetBinding binding = (WidgetGuildProfileSheetBinding) d.getValue((Fragment) _this, _this.$$delegatedProperties[0]);
              NestedScrollView lo = (NestedScrollView) binding.getRoot();
              LinearLayout layout = (LinearLayout) lo.findViewById(sheetId);
              Context ctx = layout.getContext();
              var clock = ClockFactory.get();
              var p = Utils.dpToPx(16);
              boolean showFriendsAct = settings.getBool("friendsAct", true);
              boolean showBlockedAct = settings.getBool("blockedAct", true);

              LinearLayout actions = (LinearLayout) ((FrameLayout) lo.findViewById(Utils.getResId("guild_profile_sheet_secondary_actions", "id"))).getChildAt(0);
              TextView mutualBtn = new TextView(actions.getContext(), null, 0, Utils.getResId("GuildProfileSheet.Actions.Title", "style"));
              mutualBtn.setId(tabId);
              mutualBtn.setText("Friends");
              mutualBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.d.ic_chevron_right_grey_12dp, 0);
              mutualBtn.setTypeface(ResourcesCompat.getFont(actions.getContext(), Constants.Fonts.whitney_semibold));
              mutualBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
              mutualBtn.setPadding(p, p, p, p);
              mutualBtn.setOnClickListener(e -> {Utils.openPageWithProxy(actions.getContext(), new MutualFriendsPage(guildStore.getMembers().get(guild.getId()), guild.getName()));});
              if(actions.findViewById(tabId) == null && showFriendsAct) {
                  actions.addView(mutualBtn, 1);
              }

              TextView blockedBtn = new TextView(actions.getContext(), null, 0, Utils.getResId("GuildProfileSheet.Actions.Title", "style"));
              blockedBtn.setId(blockedId);
              blockedBtn.setText("Blocked Users");
              blockedBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.d.ic_chevron_right_grey_12dp, 0);
              blockedBtn.setTypeface(ResourcesCompat.getFont(actions.getContext(), Constants.Fonts.whitney_semibold));
              blockedBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
              blockedBtn.setPadding(p, p, p, p);
              blockedBtn.setOnClickListener(e -> {Utils.openPageWithProxy(actions.getContext(), new BlockedUsersPage(guildStore.getMembers().get(guild.getId()), guild.getName()));});
              if(actions.findViewById(blockedId) == null && showBlockedAct) {
                  actions.addView(blockedBtn, 2);
              }

              GridLayout info = new GridLayout(ctx);
              info.setColumnCount(2);
              info.setId(infoId);
              info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
              info.setBackgroundColor(Color.TRANSPARENT);
              info.setPadding(0, 0, 0, 0);
              if(layout.findViewById(infoId) == null) {
                boolean showCreatedAt = settings.getBool("createdAt", true);
                boolean showJoinedAt = settings.getBool("joinedAt", true);
                boolean showVanity = settings.getBool("vanityUrl", true);
                boolean showOwner = settings.getBool("owner", true);
                boolean showLocale = settings.getBool("locale", true);
                boolean showTier = settings.getBool("tier", true);
                boolean showVerificationLevel = settings.getBool("verificationLevel", true);
                boolean showContentFilter = settings.getBool("contentFilter", true);

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

                if(showOwner) {
                  if(owner == null) {
                    StoreStream.getUsers().fetchUsers(Arrays.asList(guild.getOwnerId()));
                    owner = StoreStream.getUsers().getUsers().get(guild.getOwnerId());
                  }
                  if(owner != null){
                    String discrim = String.valueOf(owner.getDiscriminator());
                    while(discrim.length() < 4){
                      discrim = "0" + discrim;
                    }
                    final User gOwner = owner;
                    addInfo(ctx, info, "Owner", owner.getUsername() + "#" + discrim, e -> {
                      WidgetUserSheet.Companion.show(gOwner.getId(), guild.getId(), _this.getParentFragmentManager(), guild.getId());
                      return true;
                    });
                  }
                }

                if(showLocale && guild.getPreferredLocale() != null) {
                    addInfo(ctx, info, "Language", guild.getPreferredLocale(), null);
                }

                if(showTier && guild.getPremiumSubscriptionCount() > 2) {
                    addInfo(ctx, info, "Boost Level","Tier " + guild.getPremiumTier(), null);
                }

                if(showVerificationLevel) {
                    String level = "";
                    switch (guild.getVerificationLevel()) {
                        case NONE: level = "None"; break;
                        case LOW: level = "Low"; break;
                        case MEDIUM: level = "Medium"; break;
                        case HIGH: level = "High"; break;
                        case HIGHEST: level = "Very High"; break;
                    }
                    addInfo(ctx, info, "Verification Level", level, null);
                }

                if(showContentFilter) {
                    String level = "";
                    switch (guild.getExplicitContentFilter()) {
                        case NONE: level = "Don't scan"; break;
                        case SOME: level = "Scan those without a role"; break;
                        case ALL: level = "Scan everyone"; break;
                    }
                    addInfo(ctx, info, "Content Filter", level, null);
                }
                
                layout.addView(info, 3);
              }

              
            } catch (Throwable e) {
              Logger logger = new Logger("GuildProfiles");
              logger.error("Error adding guild info", e);
            }
        }));
    }

    public void addInfo(Context c, GridLayout layout, String name, String value, @Nullable View.OnLongClickListener listener) {
      LinearLayout section = new LinearLayout(c);
      section.setOrientation(LinearLayout.VERTICAL);
      GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f), GridLayout.spec(GridLayout.UNDEFINED, 1f));
      section.setLayoutParams(params);
      section.setBackgroundColor(Color.TRANSPARENT);
      section.setPadding(Utils.dpToPx(2), Utils.dpToPx(8), 0, 0);

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
      header.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
      section.addView(header);

      TextView info = new TextView(c, null, 0, R.h.UserProfile_Section_Header);
      info.setText(value);
      info.setTypeface(ResourcesCompat.getFont(c, Constants.Fonts.whitney_semibold));
      info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
      section.addView(info);
      layout.addView(section);
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
