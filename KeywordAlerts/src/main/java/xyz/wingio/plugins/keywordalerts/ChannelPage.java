package xyz.wingio.plugins.keywordalerts;

import android.annotation.SuppressLint;
import android.view.*;
import android.widget.*;
import android.content.Context;

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
import com.aliucord.wrappers.*;
import com.aliucord.entities.NotificationData;

import com.discord.api.channel.Channel;
import com.discord.views.CheckedSetting;
import com.discord.views.RadioManager;
import com.discord.models.user.User;
import com.discord.models.guild.Guild;
import com.discord.panels.*;
import com.discord.utilities.SnowflakeUtils;
import com.discord.utilities.time.*;
import com.discord.utilities.rest.RestAPI;
import com.discord.utilities.analytics.AnalyticSuperProperties;
import com.discord.stores.*;

import com.discord.utilities.rest.*;

import com.lytefast.flexinput.R;

import kotlin.Unit;
import java.util.*;
import java.lang.reflect.*;

@SuppressLint("SetTextI18n")
public final class ChannelPage extends SettingsPage {
    private SettingsAPI settings;
    public KeywordAlerts plugin;
    private Keyword keyword;
    private int p = DimenUtils.dpToPx(16);
    
    public ChannelPage(KeywordAlerts plugin, Keyword keyword) {
        this.plugin = plugin;
        this.keyword = keyword;
        this.settings = plugin.settings;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        setActionBarTitle("Edit Channels");
        setActionBarSubtitle(keyword.getWord());
        setPadding(p);

        var ctx = view.getContext();
        var layout = getLinearLayout();

        Button button = new Button(ctx);
        button.setText("Add Channel");
        ChannelAdapter adapter = new ChannelAdapter(this, keyword);
        RecyclerView recyclerView = new RecyclerView(ctx);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.setAdapter(adapter);

        layout.addView(button);
        layout.addView(recyclerView);

        button.setOnClickListener(v -> {
            InputDialog dialog = new InputDialog()
                .setTitle("Add Channel")
                .setPlaceholderText("Channel Id (Ex. 811275334342541353)")
                .setDescription("Enter the id of the channel you want to add");
            dialog.setOnOkListener(w -> {
                if(dialog.getInput().isEmpty()) {
                    Toast.makeText(w.getContext(), "Keyword cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Long id = Long.parseLong(dialog.getInput());
                    if(keyword.isWhitelisted(id)) {
                        Toast.makeText(w.getContext(), "Channel already added", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Map<Long, Keyword> keywords = plugin.getKeywords();
                    keyword.addToWhitelist(id);
                    keywords.put(keyword.getId(), keyword);
                    settings.setObject("keywords", keywords);
                    reRender();
                    dialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(w.getContext(), "Invalid Channel Id", Toast.LENGTH_SHORT).show();
                    return;
                }
                
            });
            dialog.show(getFragmentManager(), this.getClass().getSimpleName());
        });
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, String subtext, boolean defaultValue) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> sets.setBool(key, c));
        return cs;
    }
}
