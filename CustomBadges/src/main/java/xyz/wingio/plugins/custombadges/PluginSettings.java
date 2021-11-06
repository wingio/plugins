package xyz.wingio.plugins.custombadges;

import android.annotation.SuppressLint;
import android.view.*;
import android.widget.*;
import android.content.Context;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.*;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import xyz.wingio.plugins.CustomBadges;
import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.SettingsAPI;
import com.aliucord.api.NotificationsAPI;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.fragments.InputDialog;
import com.aliucord.views.Divider;
import com.aliucord.views.Button;
import com.aliucord.views.SaveButton;
import com.aliucord.views.ToolbarButton;
import com.aliucord.entities.NotificationData;

import com.discord.views.CheckedSetting;
import com.discord.views.RadioManager;
import com.discord.widgets.user.profile.UserProfileHeaderView;
import com.discord.widgets.user.*;
import com.discord.stores.*;
import com.discord.models.user.User;
import com.lytefast.flexinput.R;

import xyz.wingio.plugins.custombadges.util.BadgeDB;

import kotlin.Unit;
import java.util.*;

@SuppressLint("SetTextI18n")
public final class PluginSettings extends SettingsPage {
    private final int settingsId = View.generateViewId();
    private final SettingsAPI settings;
    private BadgeDB badgeDB;
    public PluginSettings(SettingsAPI settings, BadgeDB badgeDB) {
        this.settings = settings;
        this.badgeDB = badgeDB;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        setActionBarTitle("Custom Badges");

        var context = view.getContext();
        var layout = getLinearLayout();
        Map<Long, List<StoredBadge>> userBadges = settings.getObject("userBadges", new HashMap<>(), CustomBadges.badgeStoreType);
        var keys = new ArrayList<>(userBadges.keySet());
        Map<Long, User> users = StoreStream.getUsers().getUsers((Collection<Long>) keys, true);
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        var userList = new ArrayList<>(users.values());
        recyclerView.setAdapter(new UserAdapter(this, userList, badgeDB));
        

        Button addUserButton = new Button(context);
        addUserButton.setText("Add User");

        layout.addView(addUserButton);
        layout.addView(recyclerView);

        InputDialog dialog = new InputDialog();
        dialog.setTitle("Add User");
        dialog.setDescription("Enter the user's ID");
        dialog.setPlaceholderText("User ID (ex. 298295889720770563)");
        dialog.setOnOkListener(e -> {
            var text = dialog.getInput();
            if (!text.isEmpty()) {
                try{
                    var id = Long.parseLong(text);
                    StoreStream.getUsers().fetchUsers(Arrays.asList(id));
                    Utils.openPageWithProxy(context, new EditUser(settings, id, badgeDB));
                } catch (Throwable err) {
                    Logger logger = new Logger("CustomBadges");
                    logger.error("Error adding user", err);
                }
                dialog.dismiss();   
            }
        });

        addUserButton.setOnClickListener(e -> {
            dialog.show(getFragmentManager(), "addUser");
        });

        LinearLayout toolbarButtons = new LinearLayout(context);
        toolbarButtons.setOrientation(LinearLayout.HORIZONTAL);
        toolbarButtons.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams marginEndParams = new LinearLayout.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        toolbarButtons.setHorizontalGravity(Gravity.END);
        marginEndParams.setMarginEnd(DimenUtils.getDefaultPadding());
        ToolbarButton settingsBtn = new ToolbarButton(context);
        settingsBtn.setLayoutParams(marginEndParams);
        settingsBtn.setImageDrawable(ContextCompat.getDrawable(context, R.e.ic_guild_settings_24dp));
        toolbarButtons.setId(settingsId);
        toolbarButtons.addView(settingsBtn);

        settingsBtn.setOnClickListener(e -> {
            new PrefsSheet(this, settings, badgeDB).show(getParentFragmentManager(), "Settings");
        });
        ViewGroup toolbar = (ViewGroup) getHeaderBar();
        if(toolbar.findViewById(settingsId) == null) toolbar.addView(toolbarButtons);
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}
