package xyz.wingio.plugins.guildprofiles.pages;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.aliucord.*;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.utils.RxUtils;
import com.discord.api.permission.Permission;
import com.discord.api.role.GuildRole;
import com.discord.models.guild.Guild;
import com.discord.models.member.GuildMember;
import com.discord.restapi.RestAPIParams;
import com.discord.stores.*;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.utilities.rest.RestAPI;
import com.discord.utilities.color.ColorCompat;

import com.lytefast.flexinput.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ServerRolesPage extends SettingsPage {

    private final List<GuildRole> roles;
    private final String name;

    public ServerRolesPage(List<GuildRole> roles, String name) {
        this.roles = roles;
        this.name = name;
    }

    @Override
    public void onViewBound(View view) {
        super.onViewBound(view);
        setActionBarTitle(roles.size() + " Roles");
        setActionBarSubtitle(name);
        setPadding(0);

        var ctx = view.getContext();

        for (GuildRole role : roles) {
            TextView roleView = new TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Icon);
            roleView.setText(role.g());
            var clr = Color.parseColor("#" + String.format("%06x", role.b()));
            Drawable icon = ContextCompat.getDrawable(ctx, R.d.ic_shieldstar_24dp);
            icon.setTint(role.b() == 0 ? ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal) : clr);
            roleView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            roleView.setTextColor(role.b() == 0 ? ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal) : clr);
            addView(roleView);
        }
    }
}