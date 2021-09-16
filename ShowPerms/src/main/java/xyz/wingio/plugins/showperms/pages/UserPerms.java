package xyz.wingio.plugins.showperms.pages;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.aliucord.Utils;
import com.aliucord.fragments.SettingsPage;

import com.discord.api.role.GuildRole;
import com.discord.models.user.User;
import com.discord.utilities.color.ColorCompat;

import com.lytefast.flexinput.R;

import java.util.*;

public class UserPerms extends SettingsPage {
    public List<GuildRole> roles;
    public User user;
    public String guildName;

    public UserPerms(List<GuildRole> roles, User user) {
        this.roles = roles;
        this.user = user;
    }

    public UserPerms(List<GuildRole> roles, User user, String guildName) {
        this.roles = roles;
        this.user = user;
        this.guildName = guildName;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        Context ctx = view.getContext();
        int p = Utils.dpToPx(16);
        setActionBarTitle("Permissions for " + user.getUsername());
        if(guildName != null) {
            setActionBarSubtitle(guildName);
        }
        setPadding(0);

        TextView header = new TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Header);
        header.setText("Roles");
        header.setPadding(p, p, p, p);
        addView(header);

        for (GuildRole role : roles) {
            TextView roleView = new TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Icon);
            roleView.setText(role.g());
            var clr = Color.parseColor("#" + String.format("%06x", role.b()));
            Drawable icon = ContextCompat.getDrawable(ctx, R.d.ic_shieldstar_24dp);
            icon.setTint(role.b() == 0 ? ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal) : clr);
            roleView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            roleView.setTextColor(role.b() == 0 ? ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal) : clr);
            roleView.setOnClickListener(v -> {
                Utils.openPageWithProxy(ctx, new PermissionViewer(role));
            });
            addView(roleView);
        }
    }
}