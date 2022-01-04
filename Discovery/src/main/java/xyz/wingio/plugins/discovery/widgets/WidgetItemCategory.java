package xyz.wingio.plugins.discovery.widgets;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;

import com.aliucord.PluginManager;
import com.aliucord.Constants;
import com.aliucord.utils.*;
import com.aliucord.views.Button;

import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.icon.IconUtils;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.card.MaterialCardView;

import xyz.wingio.plugins.discovery.api.Category;

import com.lytefast.flexinput.R;

public class WidgetItemCategory extends MaterialCardView {
    public final TextView name;
    public final SimpleDraweeView icon;
    public int category = 0;

    public WidgetItemCategory(Context ctx) {
        super(ctx);
        int p = DimenUtils.dpToPx(16);
        int p2 = p/2;
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, p2);
        setLayoutParams(cardParams);
        setCardBackgroundColor(ColorCompat.getThemedColor(ctx, R.b.colorBackgroundSecondary));
        setRadius(DimenUtils.getDefaultCardRadius());

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        root.setGravity(Gravity.CENTER_VERTICAL);
        root.setPadding(p2, p2, p2, p2);

        name = new TextView(ctx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(p2, 0, 0, 0);
        name.setLayoutParams(params);
        name.setTextSize(14f);
        name.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_bold));
        name.setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        name.setCompoundDrawablePadding(DimenUtils.dpToPx(6));
        name.setSingleLine(true);
        name.setText("Gaming");

        icon = new SimpleDraweeView(ctx);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(DimenUtils.dpToPx(24), DimenUtils.dpToPx(24));
        icon.setLayoutParams(iconParams);
        // icon.setImageURI(IconUtils.DEFAULT_ICON_BLURPLE);
        icon.setImageResource(R.e.ic_controller_24dp);

        root.addView(icon);
        root.addView(name);
        
        addView(root);
    }

    public WidgetItemCategory setCategory(int category) {
        switch(category){
            case Category.GAMING:
                name.setText("Gaming");
                setThemedIcon(R.e.ic_controller_24dp, false);
                break;
            case Category.MUSIC:
                name.setText("Music");
                setThemedIcon(getPluginDrawable("ic_music_24dp"), false);
                break;
            case Category.ENTERTAINMENT:
                name.setText("Entertainment");
                setThemedIcon(getPluginDrawable("ic_tv_24dp"), false);
                break;
            case Category.ART:
                name.setText("Art");
                setThemedIcon(R.e.ic_theme_24dp, false);
                break;
            case Category.SCIENCE:
                name.setText("Science & Tech");
                setThemedIcon(getPluginDrawable("ic_science_24dp"), false);
                break;
            case Category.EDUCATION:
                name.setText("Education");
                setThemedIcon(getPluginDrawable("ic_edu_24dp"), false);
                break;
        }
        this.category = category;
        return this;
    }

    public WidgetItemCategory setThemedIcon(int icon, boolean hl) {
        Drawable ic = getContext().getDrawable(icon);
        ic.setTint(ColorCompat.getThemedColor(getContext(), R.b.colorInteractiveNormal));
        if(hl) ic.setTint(ColorCompat.getThemedColor(getContext(), R.b.colorOnPrimary));
        this.icon.setImageDrawable(ic);
        return this;
    }

    public WidgetItemCategory setThemedIcon(Drawable icon, boolean hl) {
        icon.setTint(ColorCompat.getThemedColor(getContext(), R.b.colorInteractiveNormal));
        if(hl) icon.setTint(ColorCompat.getThemedColor(getContext(), R.b.colorOnPrimary));
        this.icon.setImageDrawable(icon);
        return this;
    }

    public Drawable getPluginDrawable(String icon) {
        var res = PluginManager.plugins.get("Discovery").resources;
        return ResourcesCompat.getDrawable(res, res.getIdentifier(icon, "drawable", "com.aliucord.plugins"), null);
    }

    public void resetBackground() {
        setCardBackgroundColor(ColorCompat.getThemedColor(getContext(), R.b.colorBackgroundSecondary));
        name.setTextColor(ColorCompat.getThemedColor(getContext(), R.b.colorInteractiveNormal));
        setCategory(category);
    }

    public void highlight() {
        setCardBackgroundColor(ColorCompat.getThemedColor(getContext(), R.b.colorButtonNormal));
        name.setTextColor(ColorCompat.getThemedColor(getContext(), R.b.colorOnPrimary));
        switch(category){
            case Category.GAMING:
                name.setText("Gaming");
                setThemedIcon(R.e.ic_controller_24dp, true);
                break;
            case Category.MUSIC:
                name.setText("Music");
                setThemedIcon(getPluginDrawable("ic_music_24dp"), true);
                break;
            case Category.ENTERTAINMENT:
                name.setText("Entertainment");
                setThemedIcon(getPluginDrawable("ic_tv_24dp"), true);
                break;
            case Category.ART:
                name.setText("Art");
                setThemedIcon(R.e.ic_theme_24dp,true);
                break;
            case Category.SCIENCE:
                name.setText("Science & Tech");
                setThemedIcon(getPluginDrawable("ic_science_24dp"),true);
                break;
            case Category.EDUCATION:
                name.setText("Education");
                setThemedIcon(getPluginDrawable("ic_edu_24dp"),true);
                break;
        }
    }
}