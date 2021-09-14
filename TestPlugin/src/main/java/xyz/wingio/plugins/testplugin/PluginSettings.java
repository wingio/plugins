package xyz.wingio.plugins.testplugin;

import android.annotation.SuppressLint;
import android.view.*;
import android.widget.*;
import android.content.Context;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.aliucord.Constants;
import com.aliucord.Utils;
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
import com.lytefast.flexinput.R;

import kotlin.Unit;
import java.util.Arrays;

@SuppressLint("SetTextI18n")
public final class PluginSettings extends SettingsPage {
    private static final String plugin = "Test Plugin";

    private final SettingsAPI settings;
    public PluginSettings(SettingsAPI settings) {
        this.settings = settings;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        setActionBarTitle(plugin);
        setPadding(0);

        var context = view.getContext();
        var layout = getLinearLayout();
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}
