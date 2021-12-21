package xyz.wingio.plugins;

import android.content.Context;
import android.net.Uri;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.*;
import android.text.Editable;
import android.view.*;
import android.widget.*;

import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.*;
import com.aliucord.utils.*;
import com.aliucord.wrappers.*;

import com.discord.models.member.GuildMember;
import com.discord.models.guild.Guild;
import com.discord.api.role.GuildRole;
import com.discord.api.permission.Permission;
import com.discord.stores.*;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.widgets.home.*;
import com.discord.widgets.servers.*;

import com.google.gson.reflect.TypeToken;
import com.google.android.material.snackbar.Snackbar;

import com.lytefast.flexinput.R;

import java.lang.reflect.*;
import java.util.*;

import kotlin.Unit;

@SuppressWarnings("unused")
@AliucordPlugin
public class ViewServerAsRole extends Plugin {

  public ViewServerAsRole(){
    // settingsTab = new SettingsTab(PluginSettings.class).withArgs(this);
  }

  public static Logger logger = new Logger("ViewServerAsRole");
  private Snackbar bar;
  private Map<Long, List<Long>> roles = new HashMap<>();

  @Override
  public void start(Context context) throws Throwable {

    patcher.patch(WidgetHome.class, "configureUI", new Class<?>[] {WidgetHomeModel.class}, new Hook(cf -> {
      Long guildId = ChannelWrapper.getGuildId(StoreStream.getChannelsSelected().getSelectedChannel());
      WidgetHome _this = (WidgetHome) cf.thisObject;
      var resources = context.getResources();
      var id = resources.getIdentifier("status_bar_height", "dimen", "android");
      var statusBarHeight = id > 0 ? resources.getDimensionPixelSize(id) : 0;
      var activity = _this.getAppActivity();
      if(bar != null) bar.dismiss();
      List<Long> roleIds = roles.get(guildId);
      var size = roleIds == null ? 0 : roleIds.size();
      bar = Snackbar.make(activity.u, String.format("You are currently viewing this server as %s role%s", size, size > 1 || size == 0 ? "s" : ""), Snackbar.LENGTH_INDEFINITE);
      FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bar.getView().getLayoutParams();
      if(params != null){
        params.topMargin = statusBarHeight + DimenUtils.dpToPx(56);
        params.gravity = Gravity.TOP;
        bar.getView().setLayoutParams(params);
      }
      bar.setAction("Exit", v-> {
        roles.remove(guildId);
        bar.dismiss();
      });
      bar.setBackgroundTint(0xFF3C45A5);
      bar.setActionTextColor(0xFFFFFFFF);
      if(roles.containsKey(guildId)) bar.show();
    }));

    patcher.patch(Guild.class, "getOwnerId", new Class<?>[] {}, new Hook(cf -> {
      Long ownerId = (Long) cf.getResult();
      if(ownerId == StoreStream.getUsers().getMe().getId() && roles.get(((Guild) cf.thisObject).getId()) != null) cf.setResult(0L);
    }));

    patcher.patch(GuildMember.class, "getRoles", new Class<?>[] {}, new Hook(cf -> {
      GuildMember member = (GuildMember) cf.thisObject;
      List<Long> roleIds = roles.get(member.getGuildId());
      if(roleIds != null && isAdmin(member)) cf.setResult(roleIds);
    }));

    patcher.patch(WidgetServerSettingsEditRole.class, "setupActionBar", new Class<?>[] {WidgetServerSettingsEditRole.Model.class}, new Hook(cf -> {
      WidgetServerSettingsEditRole _this = (WidgetServerSettingsEditRole) cf.thisObject;
      WidgetServerSettingsEditRole.Model model = (WidgetServerSettingsEditRole.Model) cf.args[0];
      var activity = _this.getAppActivity();
      
      if(canViewAsRole(StoreStream.getGuilds().getMembers().get(model.getGuildId()).get(StoreStream.getUsers().getMe().getId()), model.getRole())) activity.u.getMenu().add(0, 0, 0, "View Server as Role").setOnMenuItemClickListener(item -> {
        if(roles.get(model.getGuildId()) == null){
          List<Long> roleIds = new ArrayList<>();
          roleIds.add(new GuildRoleWrapper(model.getRole()).getId());
          roles.put(model.getGuildId(), roleIds);
        } else {
          List<Long> roleIds = roles.get(model.getGuildId());
          if(!roleIds.contains(new GuildRoleWrapper(model.getRole()).getId())) roleIds.add(new GuildRoleWrapper(model.getRole()).getId());
          roles.put(model.getGuildId(), roleIds);
        }

        activity.onBackPressed();
        return true;
      });
    }));

  }

  private boolean isAdmin(GuildMember member) {
    var guildRoles = StoreStream.getGuilds().getRoles().get(member.getGuildId());
    long perms = applyRoles(member, guildRoles, GuildRoleWrapper.getPermissions(guildRoles.get(member.getGuildId())));
    return PermissionUtils.can(Permission.ADMINISTRATOR, perms) || StoreStream.getGuilds().getGuilds().get(member.getGuildId()).getOwnerId() == member.getUserId();
  }

  private final long applyRoles(GuildMember guildMember, Map<Long, GuildRole> map, long j) {
    try {
      if (guildMember != null) {
        for (Long l : (List<Long>) ReflectUtils.getField(guildMember, "roles")) {
          GuildRole guildRole = map != null ? map.get(Long.valueOf(l.longValue())) : null;
          if (guildRole != null) {
            j |= GuildRoleWrapper.getPermissions(guildRole);
          }
        }
      }
    } catch (Throwable ignored) {}
    return j;
  }

  private boolean canViewAsRole(GuildMember guildMember, GuildRole guildRole) {
    if(new GuildRoleWrapper(guildRole).getId() == guildMember.getGuildId()) return false;
    try {
      var roleIds = (List<Long>) ReflectUtils.getField(guildMember, "roles");
      Map<Long, GuildRole> roles = StoreStream.getGuilds().getRoles().get(guildMember.getGuildId());
      List<GuildRoleWrapper> list = new ArrayList<>();
      for (Long l : roleIds) {
        GuildRole guildRole1 = roles.get(Long.valueOf(l.longValue()));
        if (guildRole1 != null) {
          list.add(new GuildRoleWrapper(guildRole1));
        }
      }
      list.sort(Comparator.comparingInt(GuildRoleWrapper::getPosition));
      Collections.reverse(list);
      if(GuildRoleWrapper.getPosition(guildRole) > list.get(0).getPosition()) return false;
    } catch (Throwable ignored) {return false;}
    return isAdmin(guildMember);
  }

  @Override
  public void stop(Context context) {
      patcher.unpatchAll();
      commands.unregisterAll();
  }
}