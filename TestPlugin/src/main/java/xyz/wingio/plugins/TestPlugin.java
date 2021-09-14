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

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.wrappers.*;
import com.aliucord.utils.ReflectUtils;
import xyz.wingio.plugins.testplugin.*;

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
public class TestPlugin extends Plugin {

  public TestPlugin() {
    needsResources = true;
  }
  
  public Logger logger = new Logger("TestPlugin");
  public Field[] fields = Permission.class.getDeclaredFields();

  @NonNull
  @Override
  public Manifest getManifest() {
    var manifest = new Manifest();
    manifest.authors =
    new Manifest.Author[] {
    new Manifest.Author("Wing", 298295889720770563L),
    };
    manifest.description = "Used for testing: permission viewer";
    manifest.version = "1.1.0";
    manifest.updateUrl =
    "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    manifest.changelog = "New Features {updated marginTop}\n======================\n\n* **Rebranded!** We are now XintoCord";
    return manifest;
  }

  @Override
  public void start(Context context) throws Throwable {
    int sectionId = View.generateViewId();
    patcher.patch(WidgetUserSheet.class, "configureGuildSection", new Class<?>[]{WidgetUserSheetViewModel.ViewState.Loaded.class}, new PinePatchFn(callFrame -> {
      try {
        WidgetUserSheetViewModel.ViewState.Loaded loaded = (WidgetUserSheetViewModel.ViewState.Loaded) callFrame.args[0];
        WidgetUserSheet _this = (WidgetUserSheet) callFrame.thisObject;
        Channel apiChannel = loaded.getChannel(); ChannelWrapper channel = new ChannelWrapper(apiChannel);
        StoreGuilds guildStore = StoreStream.getGuilds();
        Guild guild = guildStore.getGuild(channel.getGuildId());
        GuildMember member = guildStore.getMember(guild.getId(), loaded.getUser().getId());
        //Utils.log("member roles: " + loaded.getRoleItems());
        if(guild != null && member != null) {
          WidgetUserSheetBinding binding = (WidgetUserSheetBinding) ReflectUtils.invokeMethod(WidgetUserSheet.class, _this, "getBinding");
          NestedScrollView sheet = (NestedScrollView) binding.getRoot();
          LinearLayout content = (LinearLayout) sheet.findViewById(Utils.getResId("user_sheet_content", "id"));
          Context ctx = content.getContext();
          TextView connHeader = (TextView) content.findViewById(Utils.getResId("user_sheet_connections_header", "id"));
          int connectionsHeaderIndex = content.indexOfChild(connHeader);

          LinearLayout section = new LinearLayout(ctx);
          section.setId(sectionId);
          section.setOrientation(LinearLayout.VERTICAL);
          LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
          int p = Utils.dpToPx(16);
          params.setMargins(p, 0, p, 0);
          section.setLayoutParams(params);

          TextView guildName = new TextView(ctx, null, 0, R.h.UserProfile_Section_Header);
          guildName.setText("Permissions");
          LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
          headerParams.setMargins(0, 0, 0, Utils.dpToPx(8));
          guildName.setLayoutParams(headerParams);

          section.addView(guildName);

          Map<String, Integer> perms = getPermissions(loaded.getRoleItems());
          ChipGroup permView = new ChipGroup(ctx);
          permView.setChipSpacingVertical(Utils.dpToPx(4));
          permView.setChipSpacingHorizontal(Utils.dpToPx(4));
          for(String perm : perms.keySet()){
            PermChip chip = new PermChip(ctx, perm, perms.get(perm));
            permView.addView(chip);
          }
          section.addView(permView);

          if(content.findViewById(sectionId) == null && perms.size() > 0) {
            content.addView(section, connectionsHeaderIndex);
          }
        }
      } catch (Throwable e) {logger.error("Error showing permissions", e);}
    }));
  }

  public Map<String, Integer> getPermissions(List<GuildRole> guildRoles) throws Throwable {
    List<String> permissions = new ArrayList<>();
    Map<String, Integer> colors = new HashMap<>();
    Long totalPerms = 0L;
    for(GuildRole role : guildRoles) {
      if(role != null) {
        Long perms = (Long) ReflectUtils.getField(GuildRole.class, role, "permissions");
        int color = (int) ReflectUtils.getField(GuildRole.class, role, "color");
        List<String> permnames = getPermissions(perms);
        for (String permName : permnames) {
          if(!colors.containsKey(permName)){
            colors.put(permName, color);
          }
        }
        totalPerms = totalPerms | perms;
      }
    }

    return colors;
  }

  public List<String> getPermissions(Long bits) throws Throwable {
    List<String> permissions = new ArrayList<>();
    for(Field field : fields){
      List<String> ignoredFieldsList = Arrays.asList("INSTANCE", "DEFAULT", "ALL", "NONE");
      if(!ignoredFieldsList.contains(field.getName())){
        Long permBit = (Long) field.get(Permission.INSTANCE);
        if(PermissionUtils.can(permBit, bits)){
          permissions.add(capitalizeString(field.getName().replaceAll("_", " ").toLowerCase()));
        }
      }
    }
    return permissions;
  }

  public class PermData {
    public String name;
    public int color;

    public PermData(String name, int color) {
      this.name = name;
      this.color = color;
    }

    public PermData(String name) {
      this.name = name;
    }

    public PermData setColor(int newColor){
      this.color = newColor;
      return this;
    }

    public PermData setName(String newName){
      this.name = newName;
      return this;
    }
    
  }

  public static String capitalizeString(String string) {
    char[] chars = string.toLowerCase().toCharArray();
    boolean found = false;
    for (int i = 0; i < chars.length; i++) {
      if (!found && Character.isLetter(chars[i])) {
        chars[i] = Character.toUpperCase(chars[i]);
        found = true;
      } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
        found = false;
      }
    }
    return String.valueOf(chars);
  }

  @Override
  public void stop(Context context) { patcher.unpatchAll(); }
}

