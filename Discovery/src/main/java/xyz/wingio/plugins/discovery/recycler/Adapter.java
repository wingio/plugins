package xyz.wingio.plugins.discovery.recycler;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import xyz.wingio.plugins.discovery.widgets.*;
import xyz.wingio.plugins.discovery.api.*;

import com.aliucord.Http;
import com.aliucord.Utils;
import com.aliucord.PluginManager;
import com.aliucord.utils.RxUtils;
import com.aliucord.utils.*;
import com.aliucord.Logger;
import com.aliucord.fragments.ConfirmDialog;
import com.aliucord.fragments.SettingsPage;

import com.discord.stores.*;
import com.discord.widgets.guilds.invite.WidgetGuildInvite;
import com.discord.utilities.rest.RestAPI;
import com.discord.utilities.analytics.AnalyticSuperProperties;
import com.discord.gateway.GatewaySocket;
import com.discord.models.guild.Guild;

import com.lytefast.flexinput.R;

import java.util.*;
import java.io.*;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        private final Adapter adapter;
        public final ViewGroup item;

        public ViewHolder(Adapter adapter, ViewGroup item) {
            super(item);
            this.adapter = adapter;
            this.item = item;
        }
    }

    private List<DiscoveryGuild> mData;
    private Context ctx;
    private SettingsPage page;

    public Adapter(List<DiscoveryGuild> data, SettingsPage page) {
        mData = data;
        this.page = page;
        this.ctx = page.getContext();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(this, new WidgetDiscoveryItem(ctx));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DiscoveryGuild guild = mData.get(position);
        WidgetDiscoveryItem item = (WidgetDiscoveryItem) holder.item;

        item.name.setText(guild.name);
        if(guild.features.contains("VERIFIED")){
            item.name.setCompoundDrawablesWithIntrinsicBounds(R.e.ic_verified_badge_banner, 0, 0, 0);
        } else if (guild.features.contains("PARTNERED")) {
            item.name.setCompoundDrawablesWithIntrinsicBounds(R.e.ic_partnered_badge_banner, 0, 0, 0);
        } else {
            item.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        if(guild.icon != null) {
            var iconUrl = String.format("https://cdn.discordapp.com/icons/%s/%s.png?size=256", guild.id, guild.icon);
            item.icon.setImageURI(iconUrl);
        }
        item.icon.setContentDescription(guild.name != null ? guild.name : "Discovery Server");

        if(guild.splash != null) {
            item.banner.setVisibility(View.VISIBLE);
            var splashUrl = String.format("https://cdn.discordapp.com/discovery-splashes/%s/%s.jpg?size=512", guild.id, guild.splash);
            item.banner.setImageURI(splashUrl);
        } else if (guild.banner != null) {
            item.banner.setVisibility(View.VISIBLE);
            var bannerUrl = String.format("https://cdn.discordapp.com/banners/%s/%s.jpg?size=512", guild.id, guild.banner);
            item.banner.setImageURI(bannerUrl);
        } else {
            item.banner.setVisibility(View.GONE);
        }

        if(guild.description != null) {
            item.description.setVisibility(View.VISIBLE);
            item.description.setText(guild.description);
        } else if(guild.description == null || guild.description.isEmpty()) {
            item.description.setVisibility(View.GONE);
        }

        if(guild.approximate_presence_count != 0) {
            item.onlineCount.setText(String.format("%s Online", guild.approximate_presence_count));
        }

        if(guild.approximate_member_count != 0) {
            item.memberCount.setText(String.format("%s Members", guild.approximate_member_count));
        }

        if(StoreStream.getGuilds().getGuilds().containsKey(guild.id)) {
            item.joinBtn.setText("Joined");
            item.joinBtn.setEnabled(false);
        } else {
            if(guild.vanity_url_code != null) {
                item.joinBtn.setText("Join");
                item.joinBtn.setEnabled(true);
                item.joinBtn.setOnClickListener(v -> {
                    WidgetGuildInvite.Companion.launch(item.joinBtn.getContext(), new StoreInviteSettings.InviteCode(guild.vanity_url_code, "", null));
                });
            } else {
                item.joinBtn.setText("Can't join this server");
                item.joinBtn.setEnabled(false);
                if(PluginManager.plugins.get("Discovery").settings.getBool("dangerJoin", false)) {
                    item.joinBtn.setText("Join");
                    item.joinBtn.setEnabled(true);
                    
                    item.joinBtn.setOnClickListener(v -> {
                        joinGuild(guild.id);
                    });
                }
            }
        }
        item.setOnClickListener(v -> {});
        item.setOnLongClickListener(v -> {
            WidgetDiscoverySheet sheet = new WidgetDiscoverySheet(page);
            sheet.setGuild(guild);
            sheet.show(page.getFragmentManager(), "DiscoverySheet");
            return true;
        });
        
    }

    private void joinGuild(Long id) {
        Logger log = new Logger("Discovery");
        try {
            GatewaySocket socket = (GatewaySocket) ReflectUtils.getField(StoreStream.getGatewaySocket(), "socket");
            String sessionId = (String) ReflectUtils.getField(socket, "sessionId");
            log.debug(sessionId);
            Utils.threadPool.execute(() -> {
                try {
                    var req = Http.Request.newDiscordRequest("/guilds/" + id + "/members/@me?session_id=" + sessionId + "&location=Guild%20Discovery", "PUT")
                        .setHeader("x-content-properties", "e30=")
                        .execute();
                    StoreStream.getGuildSelected().set(id);
                    Utils.mainThread.post(() -> { page.getActivity().onBackPressed(); });
                } catch (Throwable e) {
                    log.error("Error joining guild", e);
                }
            }); 
        } catch (Throwable e) {
            log.error("Error getting Session Id", e);
        }
    }

    public void setData(List<DiscoveryGuild> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void addData(List<DiscoveryGuild> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public List<DiscoveryGuild> getData() {
        return mData;
    }

}