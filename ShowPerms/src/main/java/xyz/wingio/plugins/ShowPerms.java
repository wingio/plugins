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
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import xyz.wingio.plugins.showperms.*;
import xyz.wingio.plugins.showperms.util.*;
import xyz.wingio.plugins.showperms.pages.*;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.wrappers.*;
import com.aliucord.utils.ReflectUtils;

import com.discord.api.channel.Channel;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.api.role.GuildRole;
import com.discord.api.permission.*;
import com.discord.databinding.WidgetUserSheetBinding;
import com.discord.models.guild.Guild;
import com.discord.models.member.GuildMember;
import com.discord.models.user.User;
import com.discord.stores.*;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.utilities.auditlogs.AuditLogChangeUtils;
import com.discord.widgets.user.usersheet.*;
import com.discord.widgets.roles.RolesListView;

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
  private int p = DimenUtils.dpToPx(16);
  private Drawable pluginIcon;
  private Drawable pluginIconDark;
  private static String ownerRoleSchema = "{\n\"color\": 0,\n\"hoist\": true,\n\"icon\": null,\n\"managed\": true,\n\"mentionable\": false,\n\"name\": \"Server Owner\",\n\"permissions\": 8,\n\"position\": 0,\n\"tags\": null,\n\"unicodeEmoji\": \"\"\n}";

  @Override
  public void start(Context context) throws Throwable {
    int sectionId = View.generateViewId();

    pluginIcon = ContextCompat.getDrawable(context, R.d.ic_shieldstar_24dp);

    patcher.patch(WidgetUserSheet.class, "configureGuildSection", new Class<?>[]{WidgetUserSheetViewModel.ViewState.Loaded.class}, new PinePatchFn(callFrame -> {
      try {
        int format = settings.getInt("format", 0);
        boolean showFullAdmin = (format == 1);
        boolean showMinAdmin = (format == 2);
        boolean showRoleCount = settings.getBool("showRoleCount", true);
        boolean invertOrder = settings.getBool("invertOrder", false);
        WidgetUserSheetViewModel.ViewState.Loaded loaded = (WidgetUserSheetViewModel.ViewState.Loaded) callFrame.args[0];
        WidgetUserSheet _this = (WidgetUserSheet) callFrame.thisObject;

        WidgetUserSheetBinding binding = (WidgetUserSheetBinding) ReflectUtils.invokeMethod(WidgetUserSheet.class, _this, "getBinding");
        LinearLayout content = (LinearLayout) binding.getRoot().findViewById(Utils.getResId("user_sheet_content", "id"));
        Context ctx = content.getContext();
        TextView guildName = (TextView) content.findViewById(Utils.getResId("user_sheet_guild_header", "id"));
        int connectionsHeaderIndex = content.indexOfChild(content.findViewById(Utils.getResId("user_sheet_connections_header", "id")));

        List<GuildRole> userRoles = new ArrayList<>(loaded.getRoleItems());
        Long guildId = loaded.getGuildId();
        Guild guild = StoreStream.getGuilds().getGuilds().get(guildId);
        Map<Long, GuildRole> guildRoles = StoreStream.getGuilds().getRoles().get(guildId);
        if(guildRoles.containsKey(guildId)) userRoles.add(guildRoles.get(guildId));

        LinearLayout section = new LinearLayout(ctx); section.setId(sectionId); section.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); params.setMargins(p, 0, p, 0);
        section.setLayoutParams(params);
        section.setOnLongClickListener(v -> {
          Utils.openPageWithProxy(ctx, new UserPerms(userRoles, loaded.getUser(), loaded.getGuildName()));
          return true;
        });

        TextView permHeader = new TextView(ctx, null, 0, R.h.UserProfile_Section_Header); permHeader.setText("Permissions"); 
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); headerParams.setMargins(0, 0, 0, p / 2);
        permHeader.setLayoutParams(headerParams);

        section.addView(permHeader);
        if(invertOrder) Collections.reverse(userRoles);
        Map<String, PermData> perms = PermUtils.getPermissions(userRoles);
        ChipGroup permView = new ChipGroup(ctx); permView.setChipSpacingVertical(p / 4); permView.setChipSpacingHorizontal(p / 4);

        if((guild.getOwnerId() == loaded.getUser().getId())){
          GuildRole ownerRole = (GuildRole) GsonUtils.fromJson(ownerRoleSchema, GuildRole.class);
          PermChip chip = new PermChip(ctx, "Server Owner", new PermData("Server Owner", ownerRole));
          if(showMinAdmin) perms.put("Owner", new PermData("Owner", ownerRole));
          permView.addView(chip);
        }

        for(String perm : perms.keySet()){
          PermChip chip = new PermChip(ctx, perm, perms.get(perm));
          if(!perms.containsKey("Owner")) {
            if(showMinAdmin && perm.equals("Administrator")){permView.addView(chip);} else if(showMinAdmin && !perms.containsKey("Administrator")) {permView.addView(chip);} else if (!showMinAdmin){permView.addView(chip);}
          }
        }
        section.addView(permView);

        if(content.findViewById(sectionId) == null && perms.size() > 0) content.addView(section, connectionsHeaderIndex);

        if(loaded.getRoleItems().size() > 0 && showRoleCount) {
          guildName.setText(guildName.getText() + " • " + loaded.getRoleItems().size() + " roles");
        }
      } catch (Throwable e) {logger.error("Error showing permissions", e);}
    }));

    patcher.patch(RolesListView.class, "updateView", new Class<?>[]{List.class, int.class, long.class}, new PinePatchFn(callFrame -> {
      List<GuildRole> roles = (List<GuildRole>) callFrame.args[0];
      RolesListView _this = (RolesListView) callFrame.thisObject;
      for(int i = 0; i < roles.size(); i++){
        GuildRole role = roles.get(i);
        View view = _this.getChildAt(i);
        view.setOnLongClickListener(v -> {
          Utils.openPageWithProxy(view.getContext(), new PermissionViewer(role));
          return true;
        });
      }
    }));

    // patcher.patch(Class.forName("xyz.wingio.plugins.guildprofiles.pages.ServerRolesPage.RolesAdapter"), "onRoleClicked", new Class<?>[] { GuildRole.class }, new Hook(callFrame -> {
    //   GuildRole role = (GuildRole) callFrame.args[0];
    //   Utils.openPageWithProxy(context, new PermissionViewer(role));
    // }));

    var roleOption = Utils.createCommandOption(ApplicationCommandType.ROLE, "role", "Any role", null, true, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false);
    commands.registerCommand("perms", "View permissions for a role", Arrays.asList(roleOption), ctx -> {
      final Long roleId = ctx.getLong("role");
      ChannelWrapper channel = ctx.getChannel();
      if(!channel.isGuild()) return new CommandsAPI.CommandResult("You can only run this in a server", null, false);
      Map<Long, GuildRole> guildRoles = StoreStream.getGuilds().getRoles().get(channel.getGuildId());
      GuildRole role = guildRoles.get(roleId);
      if(role == null) return new CommandsAPI.CommandResult("Unable to find that role", null, false);
      Utils.openPageWithProxy(ctx.getContext(), new PermissionViewer(role));
      return new CommandsAPI.CommandResult();
    });

  }

  public void openPermViewer(GuildRole role, com.discord.app.AppActivity ctx){
    Utils.openPageWithProxy(ctx, new PermissionViewer(role));
  }

  @Override
  public void stop(Context context) { patcher.unpatchAll(); }
}