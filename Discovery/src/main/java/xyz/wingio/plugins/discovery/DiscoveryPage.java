package xyz.wingio.plugins.discovery;

import android.annotation.SuppressLint;
import android.view.*;
import android.widget.*;
import android.content.Context;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.*;

import xyz.wingio.plugins.Discovery;
import xyz.wingio.plugins.discovery.api.*;
import xyz.wingio.plugins.discovery.recycler.Adapter;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.Http;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.SettingsAPI;
import com.aliucord.api.NotificationsAPI;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.views.Divider;
import com.aliucord.views.Button;
import com.aliucord.entities.NotificationData;

import com.discord.views.CheckedSetting;
import com.discord.views.RadioManager;
import com.discord.views.RadioManager;
import com.discord.widgets.user.profile.UserProfileHeaderView;
import com.discord.stores.*;
import com.discord.models.user.User;
import com.discord.utilities.rest.RestAPI;
import com.discord.utilities.analytics.AnalyticSuperProperties;
import com.lytefast.flexinput.R;

import kotlin.Unit;
import java.util.*;

@SuppressLint("SetTextI18n")
public final class DiscoveryPage extends SettingsPage {
    private List<DiscoveryGuild> cache;
    private SettingsAPI settings;
    private Discovery plugin;
    
    public DiscoveryPage(Discovery plugin) {
        this.plugin = plugin;
        this.settings = plugin.settings;
        this.cache = plugin.cache;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        setActionBarTitle("Discovery");
        setActionBarSubtitle(null);
        setPadding(DimenUtils.dpToPx(16));
        var context = view.getContext();
        Adapter adapter = new Adapter(new ArrayList<>(), this);
        Button loadMore = new Button(context);
        TextView info = new TextView(context, null, 0, R.h.UiKit_Settings_Item_SubText);
        info.setText("Loading...");
        info.setGravity(Gravity.CENTER);
        loadMore.setText("Load More");
        loadMore.setOnClickListener(v -> {
            loadMore.setEnabled(false);
            Utils.threadPool.execute(() -> {
                try {
                    final DiscoveryResult loaded = loadMore(adapter.getData(), 40);
                    Utils.mainThread.post(() -> {
                        adapter.addData(loaded.guilds);
                        plugin.updateCache(loaded);
                        loadMore.setEnabled(true);
                    });
                } catch (Throwable e) {}
            });
        });

        if(cache.size() == 0) {
            loadMore.setVisibility(View.GONE);
            Utils.threadPool.execute(() -> {
                try {
                    final DiscoveryResult res = loadMore(cache, 48);
                    
                    Utils.mainThread.post(() -> {
                        this.setActionBarSubtitle(res.total + " servers");
                        adapter.setData(res.guilds);
                        plugin.updateCache(res);
                        plugin.setTotalDiscoveryServers(res.total);
                        loadMore.setVisibility(View.VISIBLE);
                        info.setVisibility(View.GONE);
                    });
                } catch (Throwable e) {
                    new Logger("TestTube").error("Failed to get discovery", e);
                    Utils.mainThread.post(() -> {
                        this.setActionBarSubtitle("Failed to get discovery");
                        info.setText("Failed to get discovery");
                    });
                }
            });
        } else {
            this.setActionBarSubtitle(plugin.totalDiscoveryServers + " servers");
            adapter.setData(cache);
            info.setVisibility(View.GONE);
        }

        var layout = getLinearLayout();

        RecyclerView recycler = new RecyclerView(context);
        recycler.setLayoutManager(new LinearLayoutManager(context));
        recycler.setAdapter(adapter);
        recycler.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        
        
        layout.addView(info);
        layout.addView(recycler);
        layout.addView(loadMore);
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }

    public DiscoveryResult loadMore(List<DiscoveryGuild> current, int limit) throws Throwable {
        DiscoveryResult res = (DiscoveryResult) new Http.Request(String.format("https://discord.com/api/v9/discoverable-guilds?limit=%s&offset=%s", limit, current.size()), "GET")
            .setHeader("Authorization", (String) ReflectUtils.getField(StoreStream.getAuthentication(), "authToken"))
            .setHeader("User-Agent", RestAPI.AppHeadersProvider.INSTANCE.getUserAgent())
            .setHeader("X-Super-Properties", AnalyticSuperProperties.INSTANCE.getSuperPropertiesStringBase64())
            .setHeader("Referer", "https://discord.com/guild-discovery")
            .setHeader("Accept", "*/*")
            .execute()
            .json(DiscoveryResult.class);
        return res;
    }
}
