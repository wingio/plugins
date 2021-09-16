package xyz.wingio.plugins;

import com.google.android.material.chip.ChipGroup;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import android.os.*;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import xyz.wingio.plugins.showperms.*;
import xyz.wingio.plugins.showperms.util.*;
import xyz.wingio.plugins.showperms.pages.*;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.wrappers.*;
import com.aliucord.utils.ReflectUtils;

import com.discord.api.channel.Channel;
import com.discord.api.role.GuildRole;
import com.discord.api.permission.*;
import com.discord.databinding.WidgetUserSheetBinding;
import com.discord.models.guild.Guild;
import com.discord.models.member.GuildMember;
import com.discord.stores.*;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.utilities.auditlogs.AuditLogChangeUtils;
import com.discord.widgets.user.usersheet.*;

import com.lytefast.flexinput.R;

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;

@AliucordPlugin
public class ShowPerms extends Plugin {

  public ShowPerms() {
    settingsTab = new SettingsTab(PluginSettings.class, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings);
    needsResources = true;
  }
  
  public Logger logger = new Logger("ShowPerms");
  private int p = Utils.dpToPx(16);

  @NonNull
  @Override
  public Manifest getManifest() {
    var manifest = new Manifest();
    manifest.authors =
    new Manifest.Author[] {
    new Manifest.Author("Wing", 298295889720770563L),
    };
    manifest.description = "Shows user permissions in the profile sheet";
    manifest.version = "1.1.0";
    manifest.updateUrl =
    "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    manifest.changelog = "Added {added marginTop}\n======================\n\n* **View individual role permissions!** Long press the permissions section to see all the roles a user has and their permissions.\n\nImproved {improved marginTop}\n======================\n\n* **Show all admin perms.** You can now enable a setting to show all permissions when they have the Administrator permission.";
    return manifest;
  }

  @Override
  public void start(Context context) throws Throwable {
    int sectionId = View.generateViewId();
    patcher.patch(WidgetUserSheet.class, "configureGuildSection", new Class<?>[]{WidgetUserSheetViewModel.ViewState.Loaded.class}, new PinePatchFn(callFrame -> {
      try {
        WidgetUserSheetViewModel.ViewState.Loaded loaded = (WidgetUserSheetViewModel.ViewState.Loaded) callFrame.args[0];
        WidgetUserSheet _this = (WidgetUserSheet) callFrame.thisObject;

        WidgetUserSheetBinding binding = (WidgetUserSheetBinding) ReflectUtils.invokeMethod(WidgetUserSheet.class, _this, "getBinding");
        LinearLayout content = (LinearLayout) binding.getRoot().findViewById(Utils.getResId("user_sheet_content", "id"));
        Context ctx = content.getContext();
        int connectionsHeaderIndex = content.indexOfChild(content.findViewById(Utils.getResId("user_sheet_connections_header", "id")));

        LinearLayout section = new LinearLayout(ctx); section.setId(sectionId); section.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); params.setMargins(p, 0, p, 0);
        section.setLayoutParams(params);
        section.setOnLongClickListener(v -> {
          Utils.openPageWithProxy(ctx, new UserPerms(loaded.getRoleItems(), loaded.getUser(), loaded.getGuildName()));
          return true;
        });

        TextView permHeader = new TextView(ctx, null, 0, R.h.UserProfile_Section_Header); permHeader.setText("Permissions"); 
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); headerParams.setMargins(0, 0, 0, p / 2);
        permHeader.setLayoutParams(headerParams);

        section.addView(permHeader);

        Map<String, PermData> perms = PermUtils.getPermissions(loaded.getRoleItems());
        ChipGroup permView = new ChipGroup(ctx); permView.setChipSpacingVertical(p / 4); permView.setChipSpacingHorizontal(p / 4);
        for(String perm : perms.keySet()){
          PermChip chip = new PermChip(ctx, perm, perms.get(perm));
          permView.addView(chip);
        }
        section.addView(permView);

        if(content.findViewById(sectionId) == null && perms.size() > 0) content.addView(section, connectionsHeaderIndex);
      } catch (Throwable e) {logger.error("Error showing permissions", e);}
    }));
  }

  @Override
  public void stop(Context context) { patcher.unpatchAll(); }
}