package xyz.wingio.plugins.discovery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.*;
import android.util.AttributeSet;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.*;

import xyz.wingio.plugins.Discovery;
import xyz.wingio.plugins.discovery.api.*;
import xyz.wingio.plugins.discovery.recycler.Adapter;
import xyz.wingio.plugins.discovery.views.SearchEditText;

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
import java.net.URLEncoder;

@SuppressLint("SetTextI18n")
public final class DiscoveryPage extends SettingsPage {
    public List<DiscoveryGuild> cache;
    // public Map<String, List<DiscoveryGuild>> searchCache;
    public SettingsAPI settings;
    public Discovery plugin;
    public Logger logger = new Logger("Discovery");
    public int serverCount = 0;
    public String currentSearch = "";
    
    public DiscoveryPage(Discovery plugin) {
        this.plugin = plugin;
        this.settings = plugin.settings;
        this.cache = plugin.cache;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        setActionBarTitle("Server Discovery");
        setActionBarSubtitle(null);
        setPadding(DimenUtils.dpToPx(16));
        var context = view.getContext();
        Adapter adapter = new Adapter(new ArrayList<>(), this);
        Button loadMore = new Button(context);
        TextView info = new TextView(context, null, 0, R.i.UiKit_Settings_Item_SubText);
        info.setText("Loading...");
        info.setGravity(Gravity.CENTER);
        loadMore.setText("Load More");
        loadMore.setOnClickListener(v -> {
            loadMore.setEnabled(false);
            Utils.threadPool.execute(() -> {
                try {
                    final DiscoveryResult loaded = currentSearch.isEmpty() ? loadMore(adapter.getData(), 40) : loadMore(adapter.getData(), 40, URLEncoder.encode(currentSearch, "UTF-8"));
                    Utils.mainThread.post(() -> {
                        adapter.addData(loaded.guilds);
                        if(currentSearch.isEmpty()) plugin.updateCache(loaded);
                        serverCount = loaded.total;
                        loadMore.setEnabled(true);
                        if(loaded.guilds.size() >= loaded.total){ 
                            loadMore.setVisibility(View.GONE);
                        } else {loadMore.setVisibility(View.VISIBLE);}
                    });
                } catch (Throwable e) {logger.error("Failed to load more", e);}
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
                        serverCount = res.total;
                        loadMore.setVisibility(View.VISIBLE);
                        info.setVisibility(View.GONE);
                    });
                } catch (Throwable e) {
                    logger.error("Failed to get discovery", e);
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
        
        SearchEditText search = new SearchEditText(context);
        search.setHint(R.h.search);
        search.setThemedEndIcon(R.e.ic_search_white_24dp);
        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        searchParams.setMargins(0, 0, 0, DimenUtils.dpToPx(16));
        search.setLayoutParams(searchParams);
        search.getRoot().setEndIconVisible(false);
        var searchOnClick = new SearchOnClick(search, this, adapter, loadMore);
        search.getRoot().setEndIconOnClickListener(searchOnClick);

        var editText = search.getEditText();
        if (editText != null) {
            editText.setMaxLines(1);
            editText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    search.getRoot().setEndIconVisible(!s.toString().isEmpty());
                    search.setThemedEndIcon(R.e.ic_search_white_24dp);
                    search.getRoot().setEndIconOnClickListener(searchOnClick);
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });
        }

        layout.addView(search);
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
        DiscoveryResult res = (DiscoveryResult) Http.Request.newDiscordRequest(String.format("/discoverable-guilds?limit=%s&offset=%s", limit, current.size()), "GET")
            .setHeader("Referer", "https://discord.com/guild-discovery")
            .execute()
            .json(DiscoveryResult.class);
        return res;
    }

    public DiscoveryResult loadMore(List<DiscoveryGuild> current, int limit, String query) throws Throwable {
        DiscoveryResult res = (DiscoveryResult) Http.Request.newDiscordRequest(String.format("/discoverable-guilds?limit=%s&offset=%s&query=%s", limit, current.size(), query), "GET")
            .setHeader("Referer", "https://discord.com/guild-discovery")
            .execute()
            .json(DiscoveryResult.class);
        return res;
    }

    public void dismissKeyboard() {
        View view = Utils.appActivity.getCurrentFocus();
        if(view != null) {  
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public class SearchOnClick implements View.OnClickListener {
        private SearchEditText search;
        private DiscoveryPage page;
        private Adapter adapter;
        private Button loadMore;

        public SearchOnClick(SearchEditText search, DiscoveryPage page, Adapter adapter, Button loadMore){
            this.search = search;
            this.page = page;
            this.adapter = adapter;
            this.loadMore = loadMore;
        }

        @Override
        public void onClick(View view){
            String text = search.getEditText().getText().toString();
            if(!text.isEmpty()){
                page.dismissKeyboard();
                page.currentSearch = text;
                Utils.threadPool.execute(() -> {
                    try {
                        DiscoveryResult searchRes = page.loadMore(new ArrayList<DiscoveryGuild>(), 48, URLEncoder.encode(text, "UTF-8"));
                        Utils.mainThread.post(() -> {
                            adapter.setData(searchRes.guilds);
                            page.setActionBarSubtitle(searchRes.total + " servers");
                            page.serverCount = searchRes.total;
                            if(searchRes.guilds.size() >= searchRes.total){ 
                                loadMore.setVisibility(View.GONE);
                            } else {loadMore.setVisibility(View.VISIBLE);}
                        });
                    } catch (Throwable e) {}
                });
                search.setThemedEndIcon(R.e.ic_close_circle_nova_grey_24dp);
                search.getRoot().setEndIconOnClickListener(s -> {
                    adapter.setData(cache);
                    setActionBarSubtitle(page.plugin.totalDiscoveryServers + " servers");
                    page.serverCount = page.plugin.totalDiscoveryServers;
                    search.getEditText().setText("");
                    search.setThemedEndIcon(R.e.ic_search_white_24dp);
                    search.getRoot().setEndIconOnClickListener(this);
                    page.currentSearch = "";
                    loadMore.setVisibility(View.VISIBLE);
                });
            }
        }
    }
}