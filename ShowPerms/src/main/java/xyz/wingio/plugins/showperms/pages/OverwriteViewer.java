package xyz.wingio.plugins.showperms.pages;

import android.content.Context;
import android.widget.*;
import android.view.*;
import android.os.*;
import android.graphics.drawable.Drawable;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.utils.*;
import com.aliucord.api.SettingsAPI;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.wrappers.*;
import com.aliucord.views.Divider;
import com.discord.api.role.GuildRole;
import com.discord.api.permission.PermissionOverwrite;
import com.discord.views.CheckedSetting;
import com.discord.app.AppBottomSheet;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.models.user.User;
import com.discord.models.member.*;
import com.discord.stores.*;

import xyz.wingio.plugins.showperms.util.PermUtils;
import xyz.wingio.plugins.showperms.widgets.WidgetUserOverwrite;

import com.lytefast.flexinput.R;

import java.util.*;

public class OverwriteViewer extends SettingsPage {
    public int getContentViewResId() { return 0; }
    private Long allowed;
    private Long denied;
    private PermissionOverwrite ow;
    private GuildRole role;
    private User user;
    private String channelName;
    private Long guildId;

    public OverwriteViewer(PermissionOverwrite ow, User user, String channelName, Long guildId) {
        this(ow, user, null, channelName, guildId);
    }

    public OverwriteViewer(PermissionOverwrite ow, GuildRole role, String channelName, Long guildId) {
        this(ow, null, role, channelName, guildId);
    }

    public OverwriteViewer(PermissionOverwrite ow, User user, GuildRole role, String channelName, Long guildId) {
        this.ow = ow;
        this.allowed = ow.c();
        this.denied = ow.d();
        this.role = role;
        this.user = user;
        this.channelName = channelName;
        this.guildId = guildId;
    }

    
    @Override
    public void onViewBound(View view) {
        super.onViewBound(view);
        Context context = view.getContext();
        int p = DimenUtils.dpToPx(16);
        setPadding(0);
        setActionBarTitle("Permission Overwrites");
        setActionBarSubtitle(null);
        LinearLayout item = new LinearLayout(context);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, 0, p, 0);
        item.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        item.setVerticalGravity(Gravity.CENTER);

        if(role != null){
            TextView name = new TextView(context, null, 0, R.i.UiKit_TextView_Semibold);
            name.setText(role.g());
            var clr = Color.parseColor("#" + String.format("%06x", role.b()));
            name.setTextColor(role.b() == 0 ? ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal) : clr);
            name.setPadding(p,p,p,p);
            item.addView(name);
        }

        if(user != null){
            WidgetUserOverwrite wuo = new WidgetUserOverwrite(context).setUser(user, guildId);
            wuo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            item.addView(wuo);
        }

        TextView ch = new TextView(context, null, 0, R.i.UiKit_TextView_Semibold);
        ch.setTextColor(ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal));
        ch.setText("#" + channelName);
        ch.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ch.setGravity(Gravity.END);
        item.addView(ch);
        
        TextView header = new TextView(context, null, 0, R.i.UiKit_Settings_Item_Header);
        header.setText("Permissions");
        header.setPadding(p, p, p, p);
        
        addView(item);
        addView(new Divider(context));
        addView(header);

        try {
            List<String> allPerms = PermUtils.getPermissions();
            List<String> allowedPerms = PermUtils.getPermissions(allowed);
            List<String> deniedPerms = PermUtils.getPermissions(denied);

            for(String perm : allPerms) {
                TextView permView = new TextView(context, null, 0, R.i.UiKit_Settings_Item_Icon);
                permView.setText(perm);
                Drawable icon = ContextCompat.getDrawable(context, R.e.ic_slash_command_24dp).mutate();
                icon.setTint(ColorUtils.setAlphaComponent(ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal), 120));
                if(allowedPerms.contains(perm)){
                    icon = ContextCompat.getDrawable(context, R.e.ic_check_green_24dp);
                }
                if(deniedPerms.contains(perm)) {
                    icon = ContextCompat.getDrawable(context, R.e.ic_close_grey_24dp).mutate();
                    icon.setTint(ColorCompat.getThemedColor(context, R.b.colorStatusDangerBackground));
                }
                permView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                addView(permView);
            }
        } catch (Throwable e) {
            new Logger("ShowPerms").error("Couldn't show overwrites", e);
        }
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}