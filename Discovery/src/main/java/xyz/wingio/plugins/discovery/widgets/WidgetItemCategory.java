package xyz.wingio.plugins.discovery.widgets;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;

import com.aliucord.Constants;
import com.aliucord.utils.*;
import com.aliucord.views.Button;

import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.icon.IconUtils;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.card.MaterialCardView;

import com.lytefast.flexinput.R;

public class WidgetItemCategory extends MaterialCardView {
    public final TextView name;
    public final SimpleDraweeView icon;

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
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(DimenUtils.dpToPx(30), DimenUtils.dpToPx(30));
        icon.setLayoutParams(iconParams);
        // icon.setImageURI(IconUtils.DEFAULT_ICON_BLURPLE);
        icon.setImageResource(R.d.ic_controller_24dp);

        root.addView(icon);
        root.addView(name);
        
        addView(root);
    }
}