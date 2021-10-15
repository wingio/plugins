package xyz.wingio.plugins.discovery;

import android.content.Context;
import android.widget.*;
import android.view.*;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliucord.Utils;
import com.aliucord.utils.*;
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
        int p = DimenUtils.dpToPx(16);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(ColorCompat.getThemedColor(context, R.b.colorBackgroundPrimary));

        layout.addView(createSwitch(context, settings, "replaceHubAction", "Replace Hub Button", "Make hub button open discovery rather than the hub email page\n\nNote: Requires restart", true));
        return layout;
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}