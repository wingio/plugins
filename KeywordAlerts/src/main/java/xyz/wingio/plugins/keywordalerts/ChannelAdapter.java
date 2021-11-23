package xyz.wingio.plugins.keywordalerts;

import xyz.wingio.plugins.KeywordAlerts;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.Base64;
import android.app.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.DimenRes;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;

import com.aliucord.PluginManager;
import com.aliucord.api.*;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.wrappers.*;
import com.aliucord.Http;
import com.aliucord.Logger;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.fragments.InputDialog;

import com.discord.stores.*;
import com.discord.models.user.*;

import com.lytefast.flexinput.R;

import kotlin.jvm.functions.Function1;
import java.util.*;

import java.io.*;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelHolder> {

    public class ChannelHolder extends RecyclerView.ViewHolder {
        private final ChannelAdapter adapter;
        public final View item;

        public ChannelHolder(ChannelAdapter adapter, View item) {
            super(item);
            this.adapter = adapter;
            this.item = item;
        }
    }

    private final Context ctx;
    private final Keyword keyword;
    private final List<Long> channelIds;
    private final SettingsPage page;
    private final SettingsAPI settings = PluginManager.plugins.get("KeywordAlerts").settings;

    public ChannelAdapter(SettingsPage page, Keyword keyword) {
        this.channelIds = keyword.getWhitelist();
        this.page = page;
        this.keyword = keyword;
        ctx = page.getContext();
    }

    @Override
    public int getItemCount() {
        return channelIds.size();
    }

    @NonNull
    @Override
    public ChannelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChannelHolder(this, new ChannelItem(ctx));
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelHolder holder, int position) {
        Long channelId = channelIds.get(position);
        ChannelItem item = (ChannelItem) holder.item;

        var c = StoreStream.getChannels().getChannel(channelId);
        item.name.setText(c != null ? ChannelWrapper.getName(c) : channelId + "");
        if(c != null){
            ChannelWrapper channel = new ChannelWrapper(c);
            var g = StoreStream.getGuilds().getGuilds().get(ChannelWrapper.getGuildId(c));
            item.server.setVisibility(g != null ? View.VISIBLE : View.GONE);
            if(channel.isGuild() && g != null) {
                item.server.setText(g.getName());
            } else if (channel.raw().w().get(0) != null) {
                CoreUser recipient = new CoreUser(channel.raw().w().get(0));
                item.name.setText(recipient.getUsername());
            }
        }

        

        item.remove.setOnClickListener(v -> {
            channelIds.remove(position);
            ChannelPage cpage = (ChannelPage) page;
            var kws = cpage.plugin.getKeywords();
            keyword.setWhitelist(channelIds);
            kws.put(keyword.getId(), keyword);
            settings.setObject("keywords", kws);
            cpage.reRender();
        });
    }

    public void add(Long channelId) {
        channelIds.add(channelId);
        notifyItemInserted(channelIds.size() - 1);
    }
}
