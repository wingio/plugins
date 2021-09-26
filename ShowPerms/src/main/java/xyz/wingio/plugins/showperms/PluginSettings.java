package xyz.wingio.plugins.showperms;

import android.content.Context;
import android.widget.*;
import android.view.*;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliucord.Utils;
import com.aliucord.api.SettingsAPI;
import com.discord.views.CheckedSetting;
import com.discord.views.RadioManager;
import com.discord.app.AppBottomSheet;
import com.discord.utilities.color.ColorCompat;

import com.lytefast.flexinput.R;

import java.util.*;

public class PluginSettings extends AppBottomSheet {
    public int getContentViewResId() { return 0; }
    private final SettingsAPI settings;
    public PluginSettings(SettingsAPI settings) {
        this.settings = settings;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = inflater.getContext();
        int p = Utils.dpToPx(16);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(ColorCompat.getThemedColor(context, R.b.colorBackgroundPrimary));

        layout.addView(createSwitch(context, settings, "showDot", "Show Chip Dot", "Show the colored dot used to denote which role the permission came from.", true));
        layout.addView(createSwitch(context, settings, "showRoleCount", "Show Role Count", "Show the number of roles a user has by the server name", true));
        TextView header = new TextView(context, null, 0, R.h.UiKit_Settings_Item_Header);
        header.setText("List Behavior");
        header.setPadding(p, p, p, p);
        layout.addView(header);
        var radios = Arrays.asList(
            Utils.createCheckedSetting(context, CheckedSetting.ViewType.RADIO, "Default", null),
            Utils.createCheckedSetting(context, CheckedSetting.ViewType.RADIO, "Show All Permissions for Admin", "Show every permission if at least one of the roles has admin permissions."),
            Utils.createCheckedSetting(context, CheckedSetting.ViewType.RADIO, "Show Only Admin", "If the user is admin only show the Administrator permission, overrides \"Show All Permissions for Admin\".")
        );

        var radioManager = new RadioManager(radios);
        int format = settings.getInt("format", 0);

        int j = radios.size();
        for (int i = 0; i < j; i++) {
            int k = i;
            var radio = radios.get(k);
            radio.e(e -> {
                settings.setInt("format", k);
                radioManager.a(radio);
            });
            layout.addView(radio);
            if (k == format) radioManager.a(radio);
        }
        return layout;
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}