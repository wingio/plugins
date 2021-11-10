package xyz.wingio.plugins.twemojieverywhere;

import android.annotation.SuppressLint;
import android.view.*;
import android.widget.*;
import android.content.Context;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.*;

import xyz.wingio.plugins.TwemojiEverywhere;

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
import com.aliucord.entities.NotificationData;

import com.discord.views.CheckedSetting;
import com.discord.views.RadioManager;
import com.discord.widgets.user.profile.UserProfileHeaderView;
import com.discord.stores.*;
import com.discord.models.user.User;
import com.discord.panels.*;
import com.discord.utilities.rest.RestAPI;
import com.discord.utilities.analytics.AnalyticSuperProperties;
import com.lytefast.flexinput.R;

import kotlin.Unit;
import java.util.*;

@SuppressLint("SetTextI18n")
public final class PluginSettings extends SettingsPage {
    private SettingsAPI settings;
    private TwemojiEverywhere plugin;
    private int p = DimenUtils.dpToPx(16);
    private int i = 0;
    
    public PluginSettings(TwemojiEverywhere plugin) {
        this.plugin = plugin;
        this.settings = plugin.settings;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        setActionBarTitle("TwemojiEverywhere");
        setPadding(0);

        var ctx = view.getContext();
        var layout = getLinearLayout();

        layout.addView(createSwitch(ctx, settings, "chat_names", "Show in Chat Names", null, true));
        layout.addView(createSwitch(ctx, settings, "in_profile_sheet", "Show in Profile Sheet", null, true));
        layout.addView(createSwitch(ctx, settings, "show_in_member_list", "Show in Member List", null, true));
        layout.setOnClickListener(v -> {i++; if(i == 10) {layout.addView(createSwitch(ctx, settings, "in_server_popout_name", "Custom Emotes", null, false));layout.addView(createHuskSwitch(ctx));}});
        layout.addView(createSwitch(ctx, settings, "in_channel_list", "Show in Channel List", null, true));
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, CharSequence label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> {sets.setBool(key, c); restart();});
        return cs;
    }

    private CheckedSetting createHuskSwitch(Context context) {
        return createSwitch(context, settings, "true_twemoji", "Husk Mode", "WARNING: This option may cause huge performance issues and may not work in all places", false);
    }

    private void restart() {
        try {
            plugin.stop(Utils.getAppContext());
            plugin.start(Utils.getAppContext());
        } catch (Throwable e) {}
    }
}
