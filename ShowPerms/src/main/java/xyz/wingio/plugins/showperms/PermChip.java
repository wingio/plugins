package xyz.wingio.plugins.showperms;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.graphics.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.aliucord.Utils;
import com.aliucord.PluginManager;
import com.aliucord.api.SettingsAPI;
import com.aliucord.utils.ReflectUtils;

import com.discord.api.role.GuildRole;
import com.discord.utilities.color.ColorCompat;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.card.MaterialCardView;
import com.lytefast.flexinput.R;

public class PermChip extends MaterialCardView {
    public TextView text;
    public SettingsAPI settings = PluginManager.plugins.get("ShowPerms").settings;

    public PermChip(Context ctx, String value, PermData perm) {
        super(ctx);
        var clr = Color.parseColor("#" + String.format("%06x", perm.role.b()));
        int p = Utils.dpToPx(8);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        setRadius(Utils.dpToPx(4));
        setCardBackgroundColor(ColorCompat.getThemedColor(ctx, R.b.colorBackgroundTertiary));

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setVerticalGravity(Gravity.CENTER_VERTICAL);
        root.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        root.setPadding(Utils.dpToPx(8), Utils.dpToPx(6), Utils.dpToPx(8), Utils.dpToPx(6));

        text = new TextView(ctx, null, 0, R.h.UiKit_TextAppearance_Semibold);
        text.setTextSize(12);
        text.setMaxLines(1);
        text.setText(value);

        ImageView dot = new ImageView(ctx);
        Drawable dotImg = ContextCompat.getDrawable(ctx, R.d.drawable_circle_white_12dp);
        dotImg.setTint(perm.role.b() != 0 ? clr : ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        dot.setImageDrawable(dotImg);
        LayoutParams dotParams = new LayoutParams(Utils.dpToPx(12), Utils.dpToPx(12));
        dotParams.setMarginEnd(Utils.dpToPx(8));
        dot.setLayoutParams(dotParams);
        
        if(settings.getBool("showDot", true)) root.addView(dot);
        root.addView(text);
        root.setOnClickListener(v -> {
            Utils.showToast(ctx, perm.role.g());
        });
        addView(root);
    }
}