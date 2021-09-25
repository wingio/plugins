package xyz.wingio.plugins.betterchannelicons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.content.res.Resources;
import android.content.res.AssetManager;
import android.net.Uri;
import android.text.*;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.view.*;
import android.widget.*;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import androidx.core.content.res.ResourcesCompat;

import xyz.wingio.plugins.BetterChannelIcons;
import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.SettingsAPI;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.utils.ReflectUtils;
import com.aliucord.widgets.BottomSheet;
import com.discord.widgets.user.Badge;
import com.aliucord.views.TextInput;
import com.aliucord.views.Button;
import com.discord.app.AppBottomSheet;
import com.discord.app.AppFragment;
import com.discord.views.CheckedSetting;
import com.discord.utilities.color.ColorCompat;
import com.lytefast.flexinput.R;

import kotlin.Unit;
import java.io.*;
import java.util.*;

public class AddChannelSheet extends BottomSheet {
    private SettingsPage page;
    private SettingsAPI settings;
    private String currentIcon;
    private ImageView icon;

    public AddChannelSheet(SettingsPage page, SettingsAPI settings) {
        this.page = page;
        this.settings = settings;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        int p = Utils.dpToPx(16);
        Map<String, String> iconSets = settings.getObject("icons", new HashMap<>(), BetterChannelIcons.iconStoreType);
        
        setPadding(p);
        Context ctx = requireContext();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, p);


        LinearLayout iconLayout = new LinearLayout(ctx);
        iconLayout.setOrientation(LinearLayout.HORIZONTAL);
        iconLayout.setGravity(Gravity.CENTER);
        iconLayout.setLayoutParams(params);

        TextInput channelName = new TextInput(ctx);
        channelName.setHint("Channel name");
        LinearLayout.LayoutParams iconLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        channelName.setLayoutParams(iconLParams);
        channelName.getEditText().setText("");

        var resources = ctx.getResources();
        Drawable badge = ResourcesCompat.getDrawable(resources, R.d.ic_open_in_new_grey_24dp, null).mutate();
        badge.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));

        icon = new ImageView(ctx);
        icon.setImageDrawable(badge);
        var iconParams = new LinearLayout.LayoutParams(Utils.dpToPx(32), Utils.dpToPx(32));
        iconParams.setMargins(p / 2, 0, p, 0);
        icon.setLayoutParams(iconParams);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setOnClickListener(v -> {
            new IconListSheet(page, settings, this).show(getFragmentManager(), "icon_list");
        });
        
        Button add = new Button(ctx);
        add.setText("Add");

        iconLayout.addView(icon);
        iconLayout.addView(channelName);

        add.setOnClickListener(v -> {
            String name = channelName.getEditText().getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(ctx, "Please enter a channel name", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    var ic = ContextCompat.getDrawable(ctx, Utils.getResId(currentIcon, "drawable"));
                    var chName = name.toLowerCase();
                    if(!iconSets.containsKey(chName)){
                        iconSets.put(chName, currentIcon);
                        settings.setObject("icons", iconSets);
                        Toast.makeText(ctx, "Added icon", Toast.LENGTH_SHORT).show();
                    }
                    page.reRender();
                    dismiss();
                } catch (Throwable e) {
                    Logger logger = new Logger("BCI");
                    logger.error("Error setting icon", e);
                    Toast.makeText(ctx, "Drawable not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addView(iconLayout);
        addView(add);
    }

    public void setCurrentIcon(String icon) {
        this.currentIcon = icon;
        if(this.icon != null){
            var ic = ContextCompat.getDrawable(requireContext(), Utils.getResId(icon, "drawable")).mutate();
            ic.setTint(ColorCompat.getThemedColor(requireContext(), R.b.colorInteractiveNormal));
            this.icon.setImageDrawable(ic);
        }
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, CharSequence subtext, boolean defaultValue, boolean reRender) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> {
            sets.setBool(key, c);
        });
        return cs;
    }
}