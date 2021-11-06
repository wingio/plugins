package xyz.wingio.plugins.betterchannelicons.recycler;

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

public class RecyclerItem extends LinearLayout {
    public final TextView name;
    public final ToolbarButton delete;

    public RecyclerItem(Context ctx) {
        super(ctx);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        name = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon);
        
        LinearLayout buttons = new LinearLayout(ctx);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(DimenUtils.dpToPx(16));
        buttons.setLayoutParams(params);
        buttons.setHorizontalGravity(Gravity.END);
        buttons.setVerticalGravity(Gravity.CENTER_VERTICAL);

        delete = new ToolbarButton(ctx);
        delete.setImageDrawable(ContextCompat.getDrawable(ctx, R.e.ic_delete_24dp));
        buttons.addView(delete);

        addView(name);
        addView(buttons);
    }
}