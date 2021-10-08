package xyz.wingio.plugins.showperms.pages;

import android.content.Context;
import android.widget.*;
import android.view.*;
import android.os.Bundle;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.api.SettingsAPI;
import com.aliucord.fragments.SettingsPage;
import com.discord.api.role.GuildRole;
import com.discord.views.CheckedSetting;
import com.discord.app.AppBottomSheet;
import com.discord.utilities.color.ColorCompat;

import xyz.wingio.plugins.showperms.util.PermUtils;

import com.lytefast.flexinput.R;

import java.util.*;

public class PermissionViewer extends SettingsPage {
    public int getContentViewResId() { return 0; }
    private final Long permissions;
    private final GuildRole role;
    public PermissionViewer(GuildRole role) {
        this.permissions = role.h();
        this.role = role;
    }

    
    @Override
    public void onViewBound(View view) {
        super.onViewBound(view);
        Context context = view.getContext();
        int p = DimenUtils.dpToPx(8);
        setPadding(0);
        setActionBarTitle(role.g());
        try {
            List<String> allPerms = PermUtils.getPermissions();
            List<String> allowedPerms = PermUtils.getPermissions(permissions);

            for(String perm : allPerms) {
                TextView permView = new TextView(context, null, 0, R.h.UiKit_Settings_Item_Icon);
                permView.setText(perm);
                if(allowedPerms.contains(perm)){
                    Drawable icon = ContextCompat.getDrawable(context, R.d.ic_check_green_24dp);
                    permView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                } else {
                    Drawable icon = ContextCompat.getDrawable(context, R.d.ic_close_grey_24dp);
                    permView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                }
                addView(permView);
            }
        } catch (Throwable e) {
        }
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}