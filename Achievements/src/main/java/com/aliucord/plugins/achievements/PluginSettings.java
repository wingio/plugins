package com.aliucord.plugins.achievements;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;
import android.content.Context;
import androidx.core.content.ContextCompat;

import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Constants;
import com.aliucord.*;
import com.aliucord.api.SettingsAPI;
import com.aliucord.api.NotificationsAPI;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.views.Divider;
import com.aliucord.entities.NotificationData;
import com.aliucord.plugins.achievements.Achievement;

import com.discord.views.CheckedSetting;
import com.discord.views.RadioManager;
import com.discord.utilities.color.ColorCompat;
import com.lytefast.flexinput.*;
import com.aliucord.plugins.Achievements;

import kotlin.Unit;
import java.util.*;

@SuppressLint("SetTextI18n")
public final class PluginSettings extends SettingsPage {
    private static final String plugin = "Achievements";

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
        var wm = ResourcesCompat.getFont(context, Constants.Fonts.whitney_medium);

        Map<String, Achievement> basics = Achievements.basics;
        Achievement openSetsAch = basics.get("babysteps");
        openSetsAch.unlock();

        var expHeader = new TextView(context, null, 0, R$h.UiKit_Settings_Item_Header);
        expHeader.setTypeface(ResourcesCompat.getFont(context, Constants.Fonts.whitney_semibold));
        expHeader.setText("Experiments");

        var achHeader = new TextView(context, null, 0, R$h.UiKit_Settings_Item_Header);
        achHeader.setTypeface(ResourcesCompat.getFont(context, Constants.Fonts.whitney_semibold));
        achHeader.setText("Basic Achievements");

        layout.addView(achHeader);

        for (var ach : basics.values()) {
            var expview = new TextView(context, null, 0, R$h.UiKit_Settings_Item_Icon);
            expview.setId(View.generateViewId());
            expview.setText(ach.getName());
            expview.setTypeface(wm);

            var icon = ContextCompat.getDrawable(context, R$d.ic_slash_command_24dp);
            icon = icon.mutate();
            icon.setTint(ColorCompat.getThemedColor(context, R$b.colorInteractiveNormal));
            expview.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            layout.addView(expview);
        }

        layout.addView(new Divider(context));
        layout.addView(expHeader);
        layout.addView(createSwitch(context, settings, "allBots", "Mark everyone as bots", null, false));
        layout.addView(new Divider(context));
    }

    public void reloadPlugin() {
        PluginManager.stopPlugin(plugin);
        PluginManager.startPlugin(plugin);
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}
