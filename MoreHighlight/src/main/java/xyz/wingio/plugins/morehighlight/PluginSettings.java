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
        setActionBarTitle("MoreHighlight Settings");
        setPadding(p);

        var ctx = view.getContext();
        var layout = getLinearLayout();
        
        layout.addView(createSwitch(ctx, settings, "show_repo_name", "Show repo name in issue/pr link", null, false));
        
        TextView headerSizeLabel = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Label);
        headerSizeLabel.setText("Header Size Scale");
        headerSizeLabel.setPadding(0, p, 0, 0);
        layout.addView(headerSizeLabel);
        
        TextView headerSizeDescription = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_SubText);
        headerSizeDescription.setText("Adjust the size of headers (100% is default)");
        headerSizeDescription.setPadding(p, 0, p, DimenUtils.dpToPx(8));
        layout.addView(headerSizeDescription);

        SeekBar headerSizeSlider = new SeekBar(ctx);
        headerSizeSlider.setMax(200);
        int currentScale = (int)(settings.getFloat("header_size_scale", 1.0f) * 100);
        headerSizeSlider.setProgress(currentScale);
        headerSizeSlider.setPadding(p, DimenUtils.dpToPx(8), p, 0);

        TextView headerSizeValue = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        headerSizeValue.setText(currentScale + "%");
        headerSizeValue.setPadding(p, DimenUtils.dpToPx(4), p, p);

        headerSizeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float scale = progress / 100.0f;
                settings.setFloat("header_size_scale", scale);
                headerSizeValue.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        layout.addView(headerSizeSlider);
        layout.addView(headerSizeValue);
        
        layout.addView(new Divider(ctx));
        
        TextView info = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        info.setText(MDUtils.render("**Currently supports:**\n\n" +
                "- **Reddit** (<r/[Subreddit]>, <u/[User]>, *ex. <r/Aliucord>*)\n" +
                "- **Github** (<username/repo#issue> <gh:username/repo>, *ex. <Aliucord/Aliucord#127>*)\n" +
                "- **Plugin Settings** (ac://[Plugin Name], *ex. ac://MoreHighlight*)\n" +
                "- **Colors** *ex #1f8b4c*\n" +
                "- **Slash Commands** (</command:id>, *ex. </airhorn:816437322781949972>*)\n" +
                "- **Headers** (# Header 1, ## Header 2, ### Header 3)\n" +
                "- **Subtext** (-# tiny greyed out text)"));
        info.setPadding(0, p, 0, 0);
        layout.addView(info);
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}
