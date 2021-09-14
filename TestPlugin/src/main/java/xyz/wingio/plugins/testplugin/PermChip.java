package xyz.wingio.plugins.testplugin;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.graphics.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.discord.utilities.color.ColorCompat;
import com.aliucord.Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.card.MaterialCardView;
import com.lytefast.flexinput.R;

public class PermChip extends MaterialCardView {
    public TextView text;

    public PermChip(Context ctx, String value, int roleColor) {
        super(ctx);
        var clr = Color.parseColor("#" + String.format("%06x", roleColor));
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
        if(roleColor != 0) dotImg.setTint(clr);
        dot.setImageDrawable(dotImg);
        LayoutParams dotParams = new LayoutParams(Utils.dpToPx(12), Utils.dpToPx(12));
        dotParams.setMarginEnd(Utils.dpToPx(8));
        dot.setLayoutParams(dotParams);
        
        root.addView(dot);
        root.addView(text);
        addView(root);
    }
}