package xyz.wingio.plugins.custombadges;

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

import xyz.wingio.plugins.CustomBadges;
import com.aliucord.Utils;
import com.aliucord.utils.*;
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

import xyz.wingio.plugins.custombadges.util.BadgeDB;

import kotlin.Unit;
import java.io.*;
import java.util.*;

public class PrefsSheet extends BottomSheet {
    private SettingsPage page;
    private SettingsAPI settings;
    private BadgeDB badgeDB;

    public PrefsSheet(SettingsPage page, SettingsAPI settings, BadgeDB badgeDB) {
        this.page = page;
        this.settings = settings;
        this.badgeDB = badgeDB;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        int p = DimenUtils.dpToPx(8);
        Context ctx = requireContext();
        addView(createSwitch(ctx, settings, "replace_badges", "Replace Badges", "Whether to replace badges with custom badges or to just add to current badges.", true, false));
        addView(createSwitch(ctx, settings, "showBadgeBtn", "Show 'Edit Badges' button on the profile sheet", null, true, false));
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, CharSequence subtext, boolean defaultValue, boolean reRender) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> {
            sets.setBool(key, c);
            if (reRender) {
                page.reRender();
            }
        });
        return cs;
    }
}