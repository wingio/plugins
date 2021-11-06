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
import com.aliucord.utils.*;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.SettingsAPI;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.utils.DimenUtils;
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

public class IconListSheet extends BottomSheet {
    private SettingsPage page;
    private SettingsAPI settings;
    private AddChannelSheet acs;

    public IconListSheet(SettingsPage page, SettingsAPI settings, AddChannelSheet acs) {
        this.page = page;
        this.settings = settings;
        this.acs = acs;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        int p = DimenUtils.dpToPx(16);
        boolean isAdvanced = settings.getBool("advanced_mode", false);
        setPadding(p);
        Context ctx = requireContext();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, p);
        LinearLayout items = new LinearLayout(ctx);
        items.setOrientation(LinearLayout.VERTICAL);
        var resources = requireContext().getResources();
        
        if(isAdvanced){
            TextInput drName = new TextInput(ctx);
            drName.getEditText().setText("");
            Button button = new Button(ctx);
            button.setText("Use Icon");
            drName.setHint("Drawable name");
            addView(drName);
            addView(button);
            button.setOnClickListener(v -> {
                String iconName = drName.getEditText().getText().toString();
                if(!iconName.isEmpty()){
                    returnAndSetIcon(iconName);
                }
            });
        } else {
            setPadding(0);
            var iconMap = Constants.iconMap;
            var iconList = new ArrayList<String>(iconMap.keySet());
            for(String i : iconList){
                var iconId = iconMap.get(i);
                var icon = ContextCompat.getDrawable(ctx, Utils.getResId(iconId, "drawable")).mutate();
                icon.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
                TextView tv = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon);
                tv.setText(i);
                tv.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                tv.setOnClickListener(v -> {
                    returnAndSetIcon(iconId);
                });
                items.addView(tv);
            }
        }

        addView(items);
    }

    private void returnAndSetIcon(String icon){
        var resources = requireContext().getResources();
        try {
            ResourcesCompat.getDrawable(resources, Utils.getResId(icon, "drawable"), null);
        } catch(Throwable e) {
            Utils.showToast("Icon not found", false);
            return;
        }
        acs.setCurrentIcon(icon);
        dismiss();
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