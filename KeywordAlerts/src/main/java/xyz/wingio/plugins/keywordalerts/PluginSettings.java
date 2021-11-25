package xyz.wingio.plugins.keywordalerts;

import android.annotation.SuppressLint;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.os.Bundle;
import android.graphics.drawable.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.*;

import xyz.wingio.plugins.KeywordAlerts;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.Http;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.SettingsAPI;
import com.aliucord.api.NotificationsAPI;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.fragments.InputDialog;
import com.aliucord.views.Divider;
import com.aliucord.views.Button;
import com.aliucord.views.ToolbarButton;
import com.aliucord.widgets.BottomSheet;
import com.aliucord.wrappers.*;
import com.aliucord.entities.NotificationData;

import com.discord.views.CheckedSetting;
import com.discord.models.guild.Guild;
import com.discord.utilities.color.*;
import com.discord.stores.*;

import com.discord.utilities.rest.*;

import com.lytefast.flexinput.R;

import kotlin.Unit;
import java.util.*;
import java.lang.reflect.*;

@SuppressLint("SetTextI18n")
public final class PluginSettings extends SettingsPage {

    public static class Settings extends BottomSheet {
        private SettingsAPI settings = PluginManager.plugins.get("KeywordAlerts").settings;

        public Settings() {}

        @Override
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            Context ctx = requireContext();
            
            addView(createSwitch(ctx, settings, "ignore_current_channel", "Ignore current channel", null, true));
            addView(createSwitch(ctx, settings, "ignore_me", "Ignore own messages", null, true));
        }
    }

    private SettingsAPI settings;
    public KeywordAlerts plugin;
    private int p = DimenUtils.dpToPx(16);
    
    public PluginSettings(KeywordAlerts plugin) {
        this.plugin = plugin;
        this.settings = plugin.settings;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        setActionBarTitle("Keywords");
        setPadding(p);

        var ctx = view.getContext();
        var layout = getLinearLayout();

        Button button = new Button(ctx);
        button.setText("New Keyword");

        KeywordAdapter adapter = new KeywordAdapter(this, plugin.getKeywordsList());

        RecyclerView recyclerView = new RecyclerView(ctx);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.setAdapter(adapter);

        layout.addView(button);
        layout.addView(recyclerView);

        button.setOnClickListener(v -> {
            InputDialog dialog = new InputDialog()
                .setTitle("New Keyword")
                .setPlaceholderText("Word or Regex")
                .setDescription("Supports Regex");
            dialog.setOnOkListener(w -> {
                if(dialog.getInput().isEmpty()) {
                    Toast.makeText(w.getContext(), "Keyword cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<Long, Keyword> keywords = plugin.getKeywords();
                Keyword kw = new Keyword(dialog.getInput(), false);
                keywords.put(kw.getId(), kw);
                settings.setObject("keywords", keywords);
                adapter.add(kw);
                dialog.dismiss();
            });
            dialog.show(getFragmentManager(), this.getClass().getSimpleName());
        });

        Drawable icon = ctx.getDrawable(R.e.ic_guild_settings_24dp).mutate();
        icon.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));

        getHeaderBar().getMenu().add(0, 0, 0, "Settings").setIcon(icon).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS).setOnMenuItemClickListener(item -> {
            Settings settings = new Settings();
            settings.show(getFragmentManager(), "settings");
            return true;
        });
    }

    public static CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}
