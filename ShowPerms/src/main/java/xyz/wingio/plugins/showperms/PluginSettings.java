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
import com.discord.app.AppBottomSheet;
import com.discord.utilities.color.ColorCompat;

import com.lytefast.flexinput.R;

public class PluginSettings extends AppBottomSheet {
    public int getContentViewResId() { return 0; }
    private final SettingsAPI settings;
    public PluginSettings(SettingsAPI settings) {
        this.settings = settings;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = inflater.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(ColorCompat.getThemedColor(context, R.b.colorBackgroundPrimary));

        layout.addView(createSwitch(context, settings, "showDot", "Show Chip Dot", "Show the colored dot used to denote which role the permission came from.", true));
        layout.addView(createSwitch(context, settings, "showFullAdmin", "Show All Permissions for Admin", "Show every permission if at least one of the roles has admin permissions.", false));
        return layout;
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}