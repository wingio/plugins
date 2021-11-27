package xyz.wingio.plugins.betterchatbox;

import android.annotation.SuppressLint;
import android.view.*;
import android.widget.*;
import android.text.*;
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
import com.aliucord.views.TextInput;
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
        int btn_size = settings.getInt("btn_size", _20dp * 2);
        int av_size = settings.getInt("av_size", _20dp * 2);
        int cb_size = settings.getInt("cb_size", _20dp * 2);

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
        layout.addView(createSeekbar(ctx, "av_r", av_size / 2, av_size / 2));

        TextView cbLabel = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        cbLabel.setPadding(p, p, p, p);
        cbLabel.setText("Chatbox Radius");
        layout.addView(cbLabel);
        layout.addView(createSeekbar(ctx, "cb_r", cb_size / 2, cb_size / 2));
        
        TextView btnLabel = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        btnLabel.setPadding(p, p, p, p);
        btnLabel.setText("Button Radius");
        layout.addView(btnLabel);
        layout.addView(createSeekbar(ctx, "btn_r", btn_size / 2, btn_size / 2));

        layout.addView(new Divider(ctx));
        TextView avSizeLabel = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        avSizeLabel.setPadding(p, p, p, p);
        avSizeLabel.setText("Avatar Size");
        layout.addView(avSizeLabel);
        layout.addView(createSeekbar(ctx, "av_size", _20dp * 4, _20dp * 2));

        TextView chatSizeLabel = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        chatSizeLabel.setPadding(p, p, p, p);
        chatSizeLabel.setText("Chatbox Height");
        layout.addView(chatSizeLabel);
        layout.addView(createSeekbar(ctx, "cb_size", _20dp * 2, DimenUtils.dpToPx(100), _20dp * 2));

        TextView btnSizeLabel = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        btnSizeLabel.setPadding(p, p, p, p);
        btnSizeLabel.setText("Button Size");
        layout.addView(btnSizeLabel);
        layout.addView(createSeekbar(ctx, "btn_size", _20dp * 4, _20dp * 2));

        layout.addView(new Divider(ctx));
        TextInput editText = new TextInput(ctx);
        editText.setHint("Custom Hint");
        editText.getEditText().setText(settings.getString("hint", ""));
        editText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {} @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                settings.setString("hint", s.toString());
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(p, p, p, p);
        layout.addView(editText, params);

        TextView placeholder = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        placeholder.setPadding(p, p/2, p, p/2);
        placeholder.setText(getPlaceholdersText());
        layout.addView(placeholder);

        layout.addView(new Divider(ctx));
        layout.addView(disclaimer);
        
    }

    private List<String> placeholders = new ArrayList<>() {{
        add("%t - Target (Ex. @Wing or #general)");
        add("%n - Name (Ex. Wing or general)");
        add("%id - Channel Id (Ex. 811261478875299840)");
        add("%s - Server Name (Ex. Aliuwucord)");
        add("%u - Username (Ex. Wing)");
        add("%tag - Tag (Ex. Wing#1000)");
    }};

    private String getPlaceholdersText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Placeholders:\n");
        for (String s : placeholders) {
            sb.append(s).append("\n");
        }
        return sb.toString();
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

    private LinearLayout createSeekbar(Context context, String key, int min, int max, int defaultValue){
        LinearLayout container = new LinearLayout(context, null, 0, R.i.UiKit_Settings_Item);
        TextView label = new TextView(context, null, 0, R.i.UiKit_TextView);
        SeekBar sb = new SeekBar(context, null, 0, R.i.UiKit_SeekBar);

        sb.setMax(max);
        sb.setProgress(settings.getInt(key, defaultValue) - min);
        sb.setPadding(p, 0, p, 0);
        sb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                settings.setInt(key, seekBar.getProgress() + min);
            }

            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                label.setText((progress + min) + "");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
        });
        label.setText(settings.getInt(key, defaultValue) + "");

        container.addView(label);
        container.addView(sb);
        return container;
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
