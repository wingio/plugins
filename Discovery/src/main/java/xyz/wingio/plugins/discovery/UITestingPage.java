package xyz.wingio.plugins.discovery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.*;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.*;

import xyz.wingio.plugins.Discovery;
import xyz.wingio.plugins.discovery.api.*;
import xyz.wingio.plugins.discovery.recycler.Adapter;
import xyz.wingio.plugins.discovery.views.SearchEditText;
import xyz.wingio.plugins.discovery.widgets.WidgetDiscoveryItem;

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
import com.aliucord.views.TextInput;
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
public final class UITestingPage extends SettingsPage {
    private SettingsAPI settings;
    private Discovery plugin;
    private Logger logger = new Logger("Discovery");
    private int p = DimenUtils.dpToPx(16);
    
    public UITestingPage(Discovery plugin) {
        this.plugin = plugin;
        this.settings = plugin.settings;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        setActionBarTitle("Discovery");
        setActionBarSubtitle("UI Testing Page");
        setPadding(p);
        Context ctx = view.getContext();
        LinearLayout layout = getLinearLayout();

        LinearLayout.LayoutParams marginBottomParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        marginBottomParams.setMargins(0, 0, 0, p);
        
        SearchEditText search = new SearchEditText(ctx);
        search.setLayoutParams(marginBottomParams);

        WidgetDiscoveryItem discoveryItem = new WidgetDiscoveryItem(ctx);
        search.setLayoutParams(marginBottomParams);

        layout.addView(search);
        layout.addView(discoveryItem);
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}
