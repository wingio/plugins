package xyz.wingio.plugins.discovery.recycler;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;

import androidx.recyclerview.widget.RecyclerView;

import xyz.wingio.plugins.discovery.views.*;
import xyz.wingio.plugins.discovery.api.*;
import xyz.wingio.plugins.discovery.util.*;

import com.aliucord.Http;
import com.aliucord.Utils;
import com.aliucord.utils.RxUtils;
import com.aliucord.utils.*;
import com.aliucord.Logger;
import com.aliucord.fragments.ConfirmDialog;
import com.aliucord.fragments.SettingsPage;

import com.discord.stores.*;
import com.discord.widgets.guilds.invite.WidgetGuildInvite;
import com.discord.utilities.rest.RestAPI;
import com.discord.utilities.analytics.AnalyticSuperProperties;

import com.lytefast.flexinput.R;

import java.util.*;
import java.io.*;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        private final Adapter adapter;
        public final DiscoveryItem item;

        public ViewHolder(Adapter adapter, DiscoveryItem item) {
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
        return new ViewHolder(this, new DiscoveryItem(ctx));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DiscoveryGuild guild = mData.get(position);
        DiscoveryItem item = holder.item;

        item.name.setText(guild.name);
        if(guild.features.contains("VERIFIED")){
            item.name.setCompoundDrawablesWithIntrinsicBounds(R.d.ic_verified_badge_banner, 0, 0, 0);
        } else if (guild.features.contains("PARTNERED")) {
            item.name.setCompoundDrawablesWithIntrinsicBounds(R.d.ic_partnered_badge_banner, 0, 0, 0);
        }

        if(guild.icon != null) {
            var iconUrl = String.format("https://cdn.discordapp.com/icons/%s/%s.png?size=256", guild.id, guild.icon);
            item.icon.setImageURI(iconUrl);
        }

        if(guild.splash != null) {
            item.banner.setVisibility(View.VISIBLE);
            var splashUrl = String.format("https://cdn.discordapp.com/discovery-splashes/%s/%s.jpg?size=512", guild.id, guild.splash);
            item.banner.setImageURI(splashUrl);
        } else if (guild.banner != null) {
            item.banner.setVisibility(View.VISIBLE);
            var bannerUrl = String.format("https://cdn.discordapp.com/banners/%s/%s.jpg?size=512", guild.id, guild.banner);
            item.banner.setImageURI(bannerUrl);
        }

        if(guild.description != null) {
            item.description.setVisibility(View.VISIBLE);
            item.description.setText(guild.description);
        }

        if(guild.approximate_presence_count != 0) {
            item.onlineCount.setText(String.format("%s Online", guild.approximate_presence_count));
        }

        if(guild.approximate_member_count != 0) {
            item.memberCount.setText(String.format("%s Members", guild.approximate_member_count));
        }

        if(guild.vanity_url_code != null) {
            item.joinBtn.setOnClickListener(v -> {
                WidgetGuildInvite.Companion.launch(item.joinBtn.getContext(), new StoreInviteSettings.InviteCode(guild.vanity_url_code, "", null));
            });
        } else {
            item.joinBtn.setText("Can't join this server");
            item.joinBtn.setEnabled(false);
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