package xyz.wingio.plugins.betterchannelicons;

import android.content.Context;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.*;
import androidx.core.content.ContextCompat;

import xyz.wingio.plugins.BetterChannelIcons;
import xyz.wingio.plugins.betterchannelicons.recycler.*;
import xyz.wingio.plugins.betterchannelicons.*;

import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.api.SettingsAPI;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.views.Button;
import com.aliucord.views.ToolbarButton;

import com.lytefast.flexinput.R;

import java.util.*;

public class PluginSettings extends SettingsPage {
    private SettingsAPI settings;

    public PluginSettings(SettingsAPI settings){
        this.settings = settings;
    }

    private final int settingsId = View.generateViewId();

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        Context ctx = view.getContext();
        LinearLayout layout = getLinearLayout();
        setActionBarTitle("BetterChannelIcons");

        RecyclerView recyclerView = new RecyclerView(ctx);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        Map<String, String> icons = settings.getObject("icons", new HashMap<>(), xyz.wingio.plugins.betterchannelicons.Utils.iconStoreType);
        
        
        recyclerView.setAdapter(new IconListAdapter(this, icons));

        Button newIcon = new Button(ctx);
        newIcon.setText("Add Icon");
        newIcon.setOnClickListener(v -> {
            new AddChannelSheet(this, settings).show(getFragmentManager(), "add_channel_sheet");
        });

        layout.addView(newIcon);
        layout.addView(recyclerView);

        LinearLayout toolbarButtons = new LinearLayout(ctx);
        toolbarButtons.setOrientation(LinearLayout.HORIZONTAL);
        toolbarButtons.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams marginEndParams = new LinearLayout.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        toolbarButtons.setHorizontalGravity(Gravity.END);
        marginEndParams.setMarginEnd(DimenUtils.getDefaultPadding());
        ToolbarButton settingsBtn = new ToolbarButton(ctx);
        settingsBtn.setLayoutParams(marginEndParams);
        settingsBtn.setImageDrawable(ContextCompat.getDrawable(ctx, R.d.ic_guild_settings_24dp));
        toolbarButtons.setId(settingsId);
        toolbarButtons.addView(settingsBtn);

        settingsBtn.setOnClickListener(e -> {
            new SettingsSheet(this, settings).show(getParentFragmentManager(), "Settings");
        });
        ViewGroup toolbar = (ViewGroup) getHeaderBar();
        if(toolbar.findViewById(settingsId) == null) toolbar.addView(toolbarButtons);
    }
}