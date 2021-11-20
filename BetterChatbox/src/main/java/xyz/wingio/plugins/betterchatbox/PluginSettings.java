package xyz.wingio.plugins.betterchatbox;

import android.annotation.SuppressLint;
import android.view.*;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.content.Context;

import androidx.core.content.res.ResourcesCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.*;

import xyz.wingio.plugins.BetterChatbox;

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
import com.discord.models.user.User;
import com.discord.panels.*;
import com.discord.utilities.rest.RestAPI;
import com.discord.utilities.analytics.AnalyticSuperProperties;
import com.lytefast.flexinput.R;

import com.discord.stores.*;
import android.util.AttributeSet;
import android.util.Xml;
import com.discord.widgets.user.profile.UserProfileHeaderView;
import com.discord.widgets.user.profile.UserProfileHeaderViewModel;
import org.xmlpull.v1.XmlPullParser;
import com.discord.models.presence.Presence;
import com.discord.utilities.color.ColorCompat;

import kotlin.Unit;
import java.util.*;
import java.lang.reflect.*;

@SuppressLint("SetTextI18n")
public final class PluginSettings extends SettingsPage {
    private SettingsAPI settings;
    private BetterChatbox plugin;
    private int p = DimenUtils.dpToPx(16);

    private int avId = View.generateViewId();
    private int cbId = View.generateViewId();
    private int btnId = View.generateViewId();
    
    public PluginSettings(BetterChatbox plugin) {
        this.plugin = plugin;
        this.settings = plugin.settings;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        setActionBarTitle("BetterChatbox");
        setPadding(0);

        var ctx = view.getContext();
        var layout = getLinearLayout();
        var _20dp = DimenUtils.dpToPx(20);

        TextView disclaimer = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        disclaimer.setText("Some of these settings may require an app restart in order to properly take effect.");
        disclaimer.setPadding(p, p, p, p);
        
        layout.addView(createSwitch(ctx, settings, "show_avatar", "Show Avatar", "Show your avatar by the chat box\nPress to open the user sheet, Long press to change status", false));
        layout.addView(createSwitch(ctx, settings, "old_gallery_icon", "Use Old Gallery Icon", "Use the old image icon as opposed to the plus icon", false));
        layout.addView(createSwitch(ctx, settings, "small_gallery_button", "Use Small Gallery Button", "Use a smaller button inside the textbox rather than a large button outside of it", true));
        layout.addView(createSwitch(ctx, settings, "square_chatbox", "Square Chatbox", "Enable using a custom radius for the chatbox\nWARNING: This messes with themes for some reason", false));
        layout.addView(createSwitch(ctx, settings, "av_reverse", "Swap Avatar Actions", "Swaps the avatars press and long press actions", false));
        
        layout.addView(new Divider(ctx));

        TextView avLabel = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        avLabel.setPadding(p, p, p, p);
        avLabel.setText("Avatar Radius");
        layout.addView(avLabel);
        layout.addView(createSeekbar(ctx, "av_r", _20dp, _20dp));

        TextView cbLabel = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        cbLabel.setPadding(p, p, p, p);
        cbLabel.setText("Chatbox Radius");
        layout.addView(cbLabel);
        layout.addView(createSeekbar(ctx, "cb_r", _20dp, _20dp));
        
        TextView btnLabel = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        btnLabel.setPadding(p, p, p, p);
        btnLabel.setText("Button Radius");
        layout.addView(btnLabel);
        layout.addView(createSeekbar(ctx, "btn_r", _20dp, _20dp));

        layout.addView(new Divider(ctx));
        layout.addView(disclaimer);
        
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue, ViewGroup parent, int viewId) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        if(parent.findViewById(viewId) != null) parent.findViewById(viewId).setVisibility(sets.getBool(key, defaultValue) ? View.VISIBLE : View.GONE);
        cs.setOnCheckedListener(c -> {
            sets.setBool(key, c);
            parent.findViewById(viewId).setVisibility(c ? View.VISIBLE : View.GONE);
        });
        return cs;
    }

    private LinearLayout createSeekbar(Context context, String key, int max, int defaultValue){
        LinearLayout container = new LinearLayout(context, null, 0, R.i.UiKit_Settings_Item);
        TextView label = new TextView(context, null, 0, R.i.UiKit_TextView);
        SeekBar sb = new SeekBar(context, null, 0, R.i.UiKit_SeekBar);

        sb.setMax(max);
        sb.setProgress(settings.getInt(key, defaultValue));
        sb.setPadding(p, 0, p, 0);
        sb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                settings.setInt(key, seekBar.getProgress());
            }

            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                label.setText(progress + "");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
        });
        label.setText(settings.getInt(key, defaultValue) + "");

        container.addView(label);
        container.addView(sb);
        return container;
    }
}
