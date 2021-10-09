package xyz.wingio.plugins.favoritemessages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.text.*;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.views.*;
import xyz.wingio.plugins.FavoriteMessages;
import com.aliucord.views.Divider;
import com.discord.utilities.color.ColorCompat;
import com.discord.views.CheckedSetting;
import com.google.android.material.card.MaterialCardView;
import java.net.*;
import java.io.*;
import com.lytefast.flexinput.R;

public class MessageCard extends LinearLayout {
    public final LinearLayout root;
    public final TextView authorView;
    public final TextView contentView;
    public final TextView tagView;
    public final TextView dateView;
    public final ImageView avatarView;

    @SuppressLint("SetTextI18n")
    public MessageCard(Context ctx) {
        super(ctx);
        //setRadius(Utils.getDefaultCardRadius());
        //setCardBackgroundColor(Color.TRANSPARENT);
        setBackgroundColor(Color.TRANSPARENT);
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        int p = DimenUtils.getDefaultPadding();
        int p2 = p / 2;

        root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout cnt = new LinearLayout(ctx);
        cnt.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        cnt.setLayoutParams(lparams);

        contentView = new TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Addition);
        contentView.setPadding(0, 0, p, p2);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(lp);
        LinearLayout avSection = new LinearLayout(ctx);
        avSection.setOrientation(LinearLayout.VERTICAL);
        avSection.setGravity(Gravity.TOP);
        //make height fill container
        avSection.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        avatarView = new ImageView(ctx);
        avatarView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(DimenUtils.dpToPx(38),DimenUtils.dpToPx(38));
        parms.setMargins(p2, p2, p2 + (p / 3), p2);
        avatarView.setLayoutParams(parms);
        avSection.addView(avatarView);

        dateView = new TextView(ctx);
        dateView.setTextSize(12.0f);
        dateView.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium));
        dateView.setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorTextMuted));
        dateView.setPadding(p2, DimenUtils.dpToPx(2), 0, 0);
        dateView.setSingleLine(true);
        //dateView.setMinimumWidth(DimenUtils.dpToPx(118));

        tagView = new TextView(ctx);
        tagView.setTextSize(9.0f);
        tagView.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium));
        tagView.setTextColor(Color.WHITE);
        tagView.setPadding(DimenUtils.dpToPx(2), DimenUtils.dpToPx(1), DimenUtils.dpToPx(2), DimenUtils.dpToPx(1));
        tagView.setBackgroundDrawable(ContextCompat.getDrawable(ctx, R.d.drawable_button_brand_neutral));
        tagView.setText("BOT");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(DimenUtils.dpToPx(4),0,0,0);
        params.gravity = Gravity.CENTER_VERTICAL;
        tagView.setLayoutParams(params);
        tagView.setSingleLine(true);
        //tagView.setMinimumWidth(DimenUtils.dpToPx(21));

        authorView = new TextView(ctx);
        authorView.setTextSize(16.0f);
        authorView.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium));
        authorView.setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        authorView.setEllipsize(TextUtils.TruncateAt.END);
        authorView.setHorizontallyScrolling(false);
        authorView.setSingleLine();
        authorView.setMaxWidth(DimenUtils.dpToPx(170));

        LinearLayout authorField = new LinearLayout(ctx);
        authorField.setOrientation(LinearLayout.HORIZONTAL);
        authorField.setPadding(0, p2, p, 0);
        authorField.addView(authorView);
        authorField.addView(tagView);
        authorField.addView(dateView);

        cnt.addView(authorField);
        cnt.addView(contentView);

        root.addView(avSection);
        root.addView(cnt);

        addView(root);
    }
}
