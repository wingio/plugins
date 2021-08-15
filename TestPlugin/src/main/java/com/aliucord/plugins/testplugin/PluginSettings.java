package com.aliucord.plugins.testplugin;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;
import android.content.Context;

import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.api.SettingsAPI;
import com.aliucord.api.NotificationsAPI;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.views.Divider;
import com.aliucord.entities.NotificationData;

import com.discord.views.CheckedSetting;
import com.discord.views.RadioManager;
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
        NotificationData notD = new NotificationData();
                notD.setTitle("Achievement Unlocked");
                notD.setBody(Utils.renderMD("**Baby Steps**: Open achievement list for the first time!"));
                notD.setAutoDismissPeriodSecs(5);
                notD.setIconUrl("https://media.discordapp.net/attachments/656712865344126997/869987570090655784/76018874.png");
                notD.setOnClick(v -> {
                    Utils.log("Achievement Unlocked");
                    return Unit.a;
                });
 
        NotificationsAPI.display(notD);

        var expHeader = new TextView(context, null, 0, R.h.UiKit_Settings_Item_Header);
        expHeader.setTypeface(ResourcesCompat.getFont(context, Constants.Fonts.whitney_semibold));
        expHeader.setText("Experiments");
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
