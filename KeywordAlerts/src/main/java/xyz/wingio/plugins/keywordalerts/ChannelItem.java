package xyz.wingio.plugins.keywordalerts;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;

import androidx.core.graphics.ColorUtils;
import androidx.core.content.ContextCompat;

import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.views.*;

import com.discord.views.CheckedSetting;
import com.discord.utilities.color.ColorCompat;

import com.google.android.material.card.MaterialCardView;

import com.lytefast.flexinput.R;

public class ChannelItem extends LinearLayout {
    public TextView name;
    public TextView server;
    public ToolbarButton remove;
    private int p = DimenUtils.dpToPx(16);

    public ChannelItem(Context context) {
        super(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params3.setMargins(0, p, 0, 0);
        setLayoutParams(params3);
        setVerticalGravity(Gravity.CENTER_VERTICAL);
        
        ImageView iv = new ImageView(context);
        Drawable ch = ContextCompat.getDrawable(context, R.e.ic_text_channel_white_24dp).mutate();
        ch.setTint(ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal));
        LinearLayout.LayoutParams ivparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ivparams.setMargins(0,p,p,p);
        iv.setLayoutParams(ivparams);
        iv.setImageDrawable(ch);
        addView(iv);
        
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(params);
        ll.setVerticalGravity(Gravity.CENTER_VERTICAL);

        name = new TextView(context, null, 0, R.i.UiKit_TextView);
        name.setText("Channel");
        name.setTextSize(18f);
        name.setLayoutParams(params);
        ll.addView(name);

        server = new TextView(context, null, 0, R.i.UiKit_TextView);
        server.setText("Unknown");
        server.setLayoutParams(params);
        server.setTextSize(14f);
        server.setVisibility(View.GONE);
        server.setPadding(0, p / 4, 0, 0);
        server.setTextColor(ColorUtils.setAlphaComponent(ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal), 153));

        ll.addView(server);
        
        LinearLayout buttons = new LinearLayout(context);
        buttons.setLayoutParams(params2);
        buttons.setHorizontalGravity(Gravity.END);
        buttons.setVerticalGravity(Gravity.CENTER_VERTICAL);
        buttons.setPadding(p, 0, p, 0);

        remove = new ToolbarButton(context);
        Drawable icon = ContextCompat.getDrawable(context, R.e.ic_close_grey_24dp).mutate();
        icon.setTint(ColorCompat.getThemedColor(context, R.b.colorInfoDangerForeground));
        remove.setImageDrawable(icon, false);
        buttons.addView(remove);

        addView(ll);
        addView(buttons);
    }
    
}