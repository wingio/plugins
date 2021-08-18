package com.aliucord.plugins.guildprofiles;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;
import android.content.Context;

import androidx.core.content.res.ResourcesCompat;

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
import com.lytefast.flexinput.R;

import kotlin.Unit;
import java.util.Arrays;
import java.lang.CharSequence;

@SuppressLint("SetTextI18n")
public final class PluginSettings extends SettingsPage {
    private static final String plugin = "GuildProfiles";

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

        var expHeader = new TextView(context, null, 0, R.h.UiKit_Settings_Item_Header);
        expHeader.setTypeface(ResourcesCompat.getFont(context, Constants.Fonts.whitney_semibold));
        expHeader.setText("Settings");
        layout.addView(expHeader);

        layout.addView(createSwitch(context, settings, "createdAt", "Display 'Created At'", null, true));
        layout.addView(createSwitch(context, settings, "joinedAt", "Display 'Joined At'", null, true));
        layout.addView(createSwitch(context, settings, "vanityUrl", "Display 'Vanity URL'", null, true));
        layout.addView(createSwitch(context, settings, "owner", "Display 'Owner'", Utils.renderMD("**Hint**: Long press on this field to open up their profile!"), true));
        layout.addView(createSwitch(context, settings, "locale", "Display 'Language'", null, true));
        layout.addView(new Divider(context));
    }

    public void reloadPlugin() {
        PluginManager.stopPlugin(plugin);
        PluginManager.startPlugin(plugin);
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, CharSequence subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}
