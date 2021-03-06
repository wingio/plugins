package xyz.wingio.plugins.keywordalerts;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;

import androidx.core.content.ContextCompat;

import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.views.*;

import com.discord.views.CheckedSetting;
import com.discord.utilities.color.ColorCompat;

import com.google.android.material.card.MaterialCardView;

import com.lytefast.flexinput.R;

public class Card extends MaterialCardView {
    public CheckedSetting word;
    public CheckedSetting regex;
    public CheckedSetting whitelist;
    public ToolbarButton delete;
    public ToolbarButton edit;
    public ToolbarButton bl;
    public ToolbarButton channels;
    private int p = DimenUtils.dpToPx(16);

    public Card(Context context) {
        super(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, p, 0, 0);
        setLayoutParams(params);
        setRadius(DimenUtils.getDefaultCardRadius());
        setCardBackgroundColor(ColorCompat.getThemedColor(context, R.b.colorBackgroundSecondary));

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        word = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, "Word", null);
        regex = Utils.createCheckedSetting(context, CheckedSetting.ViewType.CHECK, "Use Regex", null);
        whitelist = Utils.createCheckedSetting(context, CheckedSetting.ViewType.CHECK, "Only in specific channels", null);

        word.setBackgroundColor(ColorCompat.getThemedColor(context, R.b.colorBackgroundSecondaryAlt));
        word.l.a().setTextAppearance(R.i.UiKit_TextView_H6);
        word.l.a().setTextSize(18f);
        word.l.a().setAllCaps(false);

        regex.getChildAt(0).setPadding(p, p/2, p, p/2);
        regex.l.a().setTextAppearance(R.i.UiKit_TextView);
        regex.l.a().setTextSize(14f);
        regex.l.d().setVisibility(View.GONE);

        whitelist.getChildAt(0).setPadding(p, p/2, p, p/2);
        whitelist.l.a().setTextAppearance(R.i.UiKit_TextView);
        whitelist.l.a().setTextSize(14f);
        whitelist.l.d().setVisibility(View.GONE);

        root.addView(word);
        root.addView(new Divider(context));
        root.addView(regex);
        root.addView(whitelist);
        
        LinearLayout buttons = new LinearLayout(context);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        buttons.setPadding(p, p, p + p/4, p);
        buttons.setGravity(Gravity.END);

        delete = new ToolbarButton(context);
        Drawable icon = ContextCompat.getDrawable(context, R.e.ic_delete_24dp).mutate();
        icon.setTint(ColorCompat.getThemedColor(context, R.b.colorInfoDangerForeground));
        delete.setImageDrawable(icon, false);

        edit = new ToolbarButton(context);
        edit.setImageResource(R.e.ic_edit_24dp);
        LayoutParams btnparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        btnparams.setMarginEnd(p);
        edit.setLayoutParams(btnparams);

        bl = new ToolbarButton(context);
        Drawable blicon = ContextCompat.getDrawable(context, R.e.ic_channels_24dp).mutate();
        blicon.setTint(ColorCompat.getThemedColor(context, R.b.colorInfoDangerForeground));
        bl.setImageDrawable(blicon, false);
        bl.setLayoutParams(btnparams);

        channels = new ToolbarButton(context);
        channels.setImageResource(R.e.ic_channels_24dp);
        channels.setLayoutParams(btnparams);
        channels.setVisibility(View.GONE);

        buttons.addView(bl);
        buttons.addView(channels);
        buttons.addView(edit);
        buttons.addView(delete);

        root.addView(buttons);

        addView(root);
    }
    
}