package xyz.wingio.plugins.betterchatbox;

import android.annotation.SuppressLint;
import android.view.*;
import android.widget.*;
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

        TextView disclaimer = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        disclaimer.setText("Some of these settings may require an app restart in order to properly take effect.");
        disclaimer.setPadding(p, p, p, p);
        
        layout.addView(createSwitch(ctx, settings, "show_avatar", "Show Avatar", "Show your avatar by the chat box\nPress to open the user sheet, Long press to change status", false));
        layout.addView(createSwitch(ctx, settings, "old_gallery_icon", "Use Old Gallery Icon", "Use the old image icon as opposed to the plus icon", false));
        layout.addView(createSwitch(ctx, settings, "small_gallery_button", "Use Small Gallery Button", "Use a smaller button inside the textbox rather than a large button outside of it", true));

        layout.addView(new Divider(ctx));
        layout.addView(disclaimer);
        
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}
