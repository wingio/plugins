package xyz.wingio.plugins.achievements;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.*;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.*;

import androidx.core.content.res.ResourcesCompat;

import xyz.wingio.plugins.achievements.Achievement;
import xyz.wingio.plugins.achievements.recycler.*;
import xyz.wingio.plugins.Achievements;

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
import com.discord.utilities.color.ColorCompat;
import com.lytefast.flexinput.R;

import kotlin.Unit;
import java.util.*;

@SuppressLint("SetTextI18n")
public final class PluginSettings extends SettingsPage {
    private final String TAG = "Achievements";

    private final SettingsAPI settings;
    private final Achievements plugin;
    public PluginSettings(SettingsAPI settings, Achievements plugin) {
        this.settings = settings;
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        setActionBarTitle(plugin.getName());
        setPadding(0);

        var context = view.getContext();
        var layout = getLinearLayout();
        var wm = ResourcesCompat.getFont(context, Constants.Fonts.whitney_medium);

        Map<String, Achievement> basics = plugin.basics;
        Achievement openSetsAch = basics.get("babysteps");
        List<String> basicIds = new ArrayList<>(basics.keySet());
        openSetsAch.unlock();
        Utils.log(String.valueOf(basics));


        var achHeader = new TextView(context, null, 0, R.h.UiKit_Settings_Item_Header);
        achHeader.setTypeface(ResourcesCompat.getFont(context, Constants.Fonts.whitney_semibold));
        achHeader.setText("Basic Achievements");

        layout.addView(achHeader);

        Map<String, Achievement> advanced = new HashMap<>();
        advanced.put("test", new Achievement("Test", "test", "test"));
        advanced.put("test2", new Achievement("Test2", "test2", "test2"));
        
        RecyclerView basicAchView = new RecyclerView(context);
        basicAchView.setLayoutManager(new LinearLayoutManager(context));
        basicAchView.setAdapter(new AchListAdapter(this, advanced));

        layout.addView(basicAchView);
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}
