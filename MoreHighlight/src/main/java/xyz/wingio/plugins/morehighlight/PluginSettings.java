package xyz.wingio.plugins.morehighlight;

import android.annotation.SuppressLint;
import android.view.*;
import android.widget.*;
import android.content.Context;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.*;

import xyz.wingio.plugins.MoreHighlight;

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
    private MoreHighlight plugin;
    private int p = DimenUtils.dpToPx(16);
    
    public PluginSettings(MoreHighlight plugin) {
        this.plugin = plugin;
        this.settings = plugin.settings;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        setActionBarTitle("Test Plugin");
        setPadding(p);

        var ctx = view.getContext();
        var layout = getLinearLayout();
        
        layout.addView(createSwitch(ctx, settings, "show_repo_name", "Show repo name in issue/pr link", null, false));
        TextView info = new TextView(ctx, null, 0, R.h.UiKit_TextView);
        info.setText(MDUtils.render("Currently supports:\n    - **Reddit** (<r/[Subreddit]>, <u/[User]>, *ex. <r/Aliucord>*)\n    - **Github** (<username/repo#issue>, *ex. <Aliucord/Aliucord#127>*)"));
        info.setPadding(0, p, 0, 0);
        layout.addView(new Divider(ctx));
        layout.addView(info);
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}
