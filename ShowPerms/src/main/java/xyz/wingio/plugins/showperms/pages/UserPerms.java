package xyz.wingio.plugins.showperms.pages;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import xyz.wingio.plugins.showperms.util.PermUtils;

import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.wrappers.*;
import com.aliucord.views.Divider;

import com.discord.api.role.GuildRole;
import com.discord.api.channel.Channel;
import com.discord.api.permission.PermissionOverwrite;
import com.discord.models.user.User;
import com.discord.models.member.GuildMember;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.stores.*;

import xyz.wingio.plugins.showperms.widgets.WidgetUserOverwrite;

import com.lytefast.flexinput.R;

import java.util.*;

public class UserPerms extends SettingsPage {
    public List<GuildRole> roles;
    public User user;
    public String guildName;
    public String name;
    public ChannelWrapper channel;
    public List<PermissionOverwrite> overwrites;

    public UserPerms(Channel channel) {
        this.channel = new ChannelWrapper(channel);
        this.name = "channel";
        this.guildName = "#" + this.channel.getName();
        this.overwrites = this.channel.getPermissionOverwrites();
        Collections.reverse(this.overwrites);
    }

    public UserPerms(List<GuildRole> roles, User user) {
        this.roles = roles;
        this.user = user;
        this.name = user.getUsername();
    }

    public UserPerms(List<GuildRole> roles, User user, String guildName) {
        this.roles = roles;
        this.user = user;
        this.guildName = guildName;
        this.name = user.getUsername();
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        Context ctx = view.getContext();
        int p = DimenUtils.dpToPx(16);
        setActionBarTitle("Permissions for " + name);
        if(guildName != null) {
            setActionBarSubtitle(guildName);
        }
        setPadding(0);

        if(channel != null) {
            TextView minRoleR = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header);
            String roleReqR = "Minimum role to view: %s";
            minRoleR.setText(String.format(roleReqR, "@everyone"));
            minRoleR.setPadding(p, p, p, p);

            TextView minRoleW = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header);
            String roleReqW = "Minimum role to chat: %s";
            minRoleW.setText(String.format(roleReqW, "@everyone"));
            minRoleW.setPadding(p, p, p, p);

            // addView(minRoleR);
            // addView(minRoleW);

            TextView header = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header);
            header.setText("Roles");
            header.setPadding(p, p, p, p);
            header.setVisibility(View.GONE);
            
            addView(header);

            for(PermissionOverwrite overwrite : overwrites){
                var ow = new PermissionOverwriteWrapper(overwrite);
                if(ow.getType() == PermissionOverwrite.Type.ROLE){
                    header.setVisibility(View.VISIBLE);
                    Map<Long, GuildRole> roleMap = StoreStream.getGuilds().getRoles().get(channel.getGuildId());
                    GuildRole role = roleMap == null ? null : roleMap.get(ow.getId());
                    if(role != null){
                        TextView roleView = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon);
                        roleView.setText(role.g());
                        var clr = Color.parseColor("#" + String.format("%06x", role.b()));
                        Drawable icon = ContextCompat.getDrawable(ctx, R.e.ic_shieldstar_24dp);
                        icon.setTint(role.b() == 0 ? ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal) : clr);
                        roleView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                        roleView.setTextColor(role.b() == 0 ? ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal) : clr);
                        roleView.setOnClickListener(v -> {
                            Utils.openPageWithProxy(ctx, new OverwriteViewer(overwrite, role, channel.getName(), channel.getGuildId()));
                        });
                        addView(roleView);
                    }
                }
            }


            Divider div = new Divider(ctx);
            div.setVisibility(View.GONE);
            TextView userHeader = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header);
            userHeader.setText("Members");
            userHeader.setPadding(p, p, p, p);
            userHeader.setVisibility(View.GONE);
            
            addView(div);
            addView(userHeader);

            for(PermissionOverwrite overwrite : overwrites){
                var ow = new PermissionOverwriteWrapper(overwrite);
                if(ow.getType() == PermissionOverwrite.Type.MEMBER){
                    User usr = StoreStream.getUsers().getUsers().get(ow.getId());
                    if(usr != null){
                        userHeader.setVisibility(View.VISIBLE);
                        div.setVisibility(View.VISIBLE);
                        WidgetUserOverwrite owView = new WidgetUserOverwrite(ctx).setUser(usr, channel.getGuildId());
                        owView.setOnClickListener(v -> {
                            Utils.openPageWithProxy(ctx, new OverwriteViewer(overwrite, usr, channel.getName(), channel.getGuildId()));
                        });
                        addView(owView);
                    }
                }
            }

        } else {
            TextView header = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header);
            header.setText("Roles");
            header.setPadding(p, p, p, p);
            
            addView(header);

            for (GuildRole role : roles) {
                TextView roleView = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon);
                roleView.setText(role.g());
                var clr = Color.parseColor("#" + String.format("%06x", role.b()));
                Drawable icon = ContextCompat.getDrawable(ctx, R.e.ic_shieldstar_24dp);
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

    public Map<Long, PermissionOverwrite> listToMap(List<PermissionOverwrite> list){
        Map<Long, PermissionOverwrite> map = new HashMap<>();
        for(PermissionOverwrite overwrite : list){
            map.put(overwrite.e(), overwrite);
        }
        return map;
    }
}