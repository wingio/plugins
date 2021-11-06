package xyz.wingio.plugins.custombadges;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;

import com.discord.utilities.color.ColorCompat;

import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.Constants;
import com.aliucord.utils.*;
import com.aliucord.views.Button;
import com.aliucord.views.ToolbarButton;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.card.MaterialCardView;

import com.lytefast.flexinput.R;

public class ItemCard extends MaterialCardView {
    public final SimpleDraweeView icon;
    public final TextView name;
    public final Button edit;
    public final ToolbarButton clear;

    public ItemCard(Context ctx) {
        super(ctx);
        int p = DimenUtils.dpToPx(16);
        int p2 = p/2;
        setRadius(DimenUtils.getDefaultCardRadius());
        setCardBackgroundColor(ColorCompat.getThemedColor(ctx, R.b.colorBackgroundSecondary));
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rootParams.setMargins(0, p/2, 0, 0);
        setLayoutParams(rootParams);

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        root.setGravity(Gravity.CENTER_VERTICAL);
        root.setPadding(p, p, p, p);

        icon = new SimpleDraweeView(ctx);
        icon.setLayoutParams(new LayoutParams(DimenUtils.dpToPx(38), DimenUtils.dpToPx(38)));
        root.addView(icon);

        name = new TextView(ctx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DimenUtils.dpToPx(110), LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(p, 0, 0, 0);
        name.setLayoutParams(params);
        name.setTextSize(16f);
        name.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_bold));
        name.setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        //wrap text to new line
        name.setSingleLine(false);


        root.addView(name);

        LinearLayout buttons = new LinearLayout(ctx);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        buttons.setHorizontalGravity(Gravity.END);
        buttons.setVerticalGravity(Gravity.CENTER_VERTICAL);

        edit = new Button(ctx);
        edit.setText("Edit Badges");
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(DimenUtils.dpToPx(103), DimenUtils.dpToPx(48));
        editParams.setMargins(0, 0, p2, 0);
        edit.setLayoutParams(editParams);
        buttons.addView(edit);

        clear = new ToolbarButton(ctx);
        clear.setPadding(p2, p2, p2, p2);
        LinearLayout.LayoutParams clearParams = new LinearLayout.LayoutParams(DimenUtils.dpToPx(40), DimenUtils.dpToPx(40));
        clear.setLayoutParams(clearParams);
        Drawable clearIcon = ContextCompat.getDrawable(ctx, R.e.ic_clear_24dp);
        clearIcon.setTint(0xFFED4245);
        clear.setImageDrawable(clearIcon);
        buttons.addView(clear);

        root.addView(buttons);
        addView(root);
    }
}