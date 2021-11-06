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

public class WidgetDiscoveryItem extends MaterialCardView {
    public final TextView name;
    public final TextView description;
    public final SimpleDraweeView icon;
    public final SimpleDraweeView banner;
    public final Button joinBtn;
    public final TextView memberCount;
    public final TextView onlineCount;

    public WidgetDiscoveryItem(Context ctx) {
        super(ctx);
        int p = DimenUtils.dpToPx(16);
        int p2 = p/2;
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, p2);
        setLayoutParams(cardParams);
        setCardBackgroundColor(ColorCompat.getThemedColor(ctx, R.b.colorBackgroundSecondary));
        setRadius(DimenUtils.getDefaultCardRadius());

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        root.setGravity(Gravity.CENTER_VERTICAL);
        root.setPadding(p, p, p, p);

        name = new TextView(ctx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, p2, 0, 0);
        name.setLayoutParams(params);
        name.setTextSize(16f);
        name.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_bold));
        name.setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        name.setCompoundDrawablePadding(DimenUtils.dpToPx(6));
        name.setSingleLine(false);

        description = new TextView(ctx);
        description.setTextSize(14f);
        description.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium));
        description.setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        description.setSingleLine(false);
        description.setVisibility(View.GONE);
        description.setLayoutParams(params);

        banner = new SimpleDraweeView(ctx);
        LinearLayout.LayoutParams bannerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DimenUtils.dpToPx(60));
        banner.setLayoutParams(bannerParams);
        banner.setImageURI(IconUtils.DEFAULT_ICON_BLURPLE);
        banner.setVisibility(View.GONE);

        icon = new SimpleDraweeView(ctx);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(DimenUtils.dpToPx(48), DimenUtils.dpToPx(48));
        iconParams.setMargins(0, p, 0, 0);
        icon.setLayoutParams(iconParams);
        icon.setImageURI(IconUtils.DEFAULT_ICON_BLURPLE);
        icon.setClipToOutline(true);
        var circle = new ShapeDrawable(new OvalShape());
        var paint = circle.getPaint();
        paint.setColor(android.graphics.Color.TRANSPARENT);
        icon.setBackground(circle);

        joinBtn = new Button(ctx);
        joinBtn.setText("Join");
        joinBtn.setLayoutParams(params);

        LinearLayout memberCountLayout = new LinearLayout(ctx);
        memberCountLayout.setOrientation(LinearLayout.HORIZONTAL);
        memberCountLayout.setLayoutParams(params);

        Drawable dotImg = ContextCompat.getDrawable(ctx, R.e.drawable_circle_white_12dp).mutate();
        Drawable onlineDot = ContextCompat.getDrawable(ctx, R.e.drawable_circle_white_12dp).mutate();

        memberCount = new TextView(ctx);
        memberCount.setTextSize(12f);
        memberCount.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium));
        memberCount.setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        memberCount.setSingleLine(true);
        memberCount.setText("0 Members");
        memberCount.setCompoundDrawablePadding(p2);
        dotImg.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        memberCount.setCompoundDrawablesWithIntrinsicBounds(dotImg, null, null, null);

        onlineCount = new TextView(ctx);
        onlineCount.setTextSize(12f);
        onlineCount.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium));
        onlineCount.setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        onlineCount.setSingleLine(true);
        onlineCount.setText("0 Online");
        onlineCount.setPadding(0, 0, p2, 0);
        onlineCount.setCompoundDrawablePadding(p2);
        onlineDot.setTint(0xFF3ba55d);
        onlineCount.setCompoundDrawablesWithIntrinsicBounds(onlineDot, null, null, null);
        
        memberCountLayout.addView(onlineCount);
        memberCountLayout.addView(memberCount);

        addView(banner);
        root.addView(icon);
        root.addView(name);
        root.addView(description);
        root.addView(memberCountLayout);
        root.addView(joinBtn);
        
        addView(root);
    }
}