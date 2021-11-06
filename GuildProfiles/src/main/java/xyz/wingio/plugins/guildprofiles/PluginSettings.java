package xyz.wingio.plugins.guildprofiles;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.*;
import android.content.Context;

import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.utils.*;
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
        LinearLayout layout = getLinearLayout();

        var actHeader = new TextView(context, null, 0, R.i.UiKit_Settings_Item_Header);
        actHeader.setTypeface(ResourcesCompat.getFont(context, Constants.Fonts.whitney_semibold));
        actHeader.setText("Actions");
        layout.addView(actHeader);
        layout.addView(createSwitch(context, settings, "friendsAct", "Display 'Friends'", null, true));
        layout.addView(createSwitch(context, settings, "blockedAct", "Display 'Blocked Users'", null, true));
        layout.addView(createSwitch(context, settings, "rolesAct", "Display 'Roles'", null, true));
        layout.addView(new Divider(context));

        var expHeader = new TextView(context, null, 0, R.i.UiKit_Settings_Item_Header);
        expHeader.setTypeface(ResourcesCompat.getFont(context, Constants.Fonts.whitney_semibold));
        expHeader.setText("Details");
        layout.addView(expHeader);

        layout.addView(createSwitch(context, settings, "createdAt", "Display 'Created At'", null, true));
        layout.addView(createSwitch(context, settings, "joinedAt", "Display 'Joined At'", null, true));
        layout.addView(createSwitch(context, settings, "vanityUrl", "Display 'Vanity URL'", null, true));
        layout.addView(createSwitch(context, settings, "owner", "Display 'Owner'", MDUtils.render("**Hint**: Long press on this field to open up their profile!"), true));
        layout.addView(createSwitch(context, settings, "locale", "Display 'Language'", null, true));
        layout.addView(createSwitch(context, settings, "tier", "Display 'Boost Level'", null, true));
        layout.addView(createSwitch(context, settings, "verificationLevel", "Display 'Verification Level'", null, true));
        layout.addView(createSwitch(context, settings, "contentFilter", "Display 'Content Filter'", null, true));
        layout.addView(createSwitch(context, settings, "features", "Display 'Features'", null, true));
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
