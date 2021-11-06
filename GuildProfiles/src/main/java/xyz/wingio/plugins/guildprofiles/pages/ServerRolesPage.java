package xyz.wingio.plugins.guildprofiles.pages;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.aliucord.*;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.utils.*;
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

    public class RolesAdapter extends RecyclerView.Adapter<RolesAdapter.RoleViewHolder> {

        private final List<GuildRole> roles;
        private final Context ctx;

        class RoleViewHolder extends RecyclerView.ViewHolder {

            private final TextView tv;

            RoleViewHolder(ViewGroup itemView) {
                super(itemView);
                tv = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon);
                
                itemView.addView(tv);
            }
        }

        public RolesAdapter(List<GuildRole> roles, Context context) {
            this.roles = roles;
            this.ctx = context;
        }

        @Override
        public RoleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout view = new LinearLayout(ctx);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new RoleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RoleViewHolder holder, int position) {
            GuildRole role = roles.get(position);
            TextView roleView = holder.tv;
            roleView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            roleView.setText(role.g());
            var clr = Color.parseColor("#" + String.format("%06x", role.b()));
            Drawable icon = ContextCompat.getDrawable(ctx, R.e.ic_shieldstar_24dp);
            icon.setTint(role.b() == 0 ? ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal) : clr);
            roleView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            roleView.setTextColor(role.b() == 0 ? ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal) : clr);
            roleView.setOnClickListener(v -> onRoleClicked(role));
        }

        @Override
        public int getItemCount() {
            return roles.size();
        }

        public void onRoleClicked(GuildRole role){
            try {
                var sp = PluginManager.plugins.get("ShowPerms");
                ReflectUtils.invokeMethod(sp.getClass(), sp, "openPermViewer", role, ctx);
            } catch (Throwable e){
                Utils.showToast(role.g(), false);
            }
        }
    }

    @Override
    public void onViewBound(View view) {
        super.onViewBound(view);
        setActionBarTitle(roles.size() + " Roles");
        setActionBarSubtitle(name);
        setPadding(0);

        var ctx = view.getContext();

        RecyclerView rolesView = new RecyclerView(ctx);
        rolesView.setLayoutManager(new LinearLayoutManager(ctx, RecyclerView.VERTICAL, false));
        rolesView.setAdapter(new RolesAdapter(roles, ctx));
        rolesView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addView(rolesView);

    }
}