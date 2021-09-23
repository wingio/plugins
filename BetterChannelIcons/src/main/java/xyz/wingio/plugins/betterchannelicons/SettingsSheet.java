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

public class SettingsSheet extends BottomSheet {
    private SettingsPage page;
    private SettingsAPI settings;
    private Integer currentIcon;

    public SettingsSheet(SettingsPage page, SettingsAPI settings) {
        this.page = page;
        this.settings = settings;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Context ctx = requireContext();
        
        addView(createSwitch(ctx, settings, "advanced_mode", "Advanced Mode", "Allows you to set whatever drawable you want for a channel icon", false, false));
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