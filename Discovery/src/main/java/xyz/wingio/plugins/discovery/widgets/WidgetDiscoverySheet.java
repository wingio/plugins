package xyz.wingio.plugins.discovery.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.*;
import android.widget.*;
import android.os.Bundle;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.*;

import com.google.android.material.chip.ChipGroup;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.SettingsAPI;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.widgets.BottomSheet;
import com.lytefast.flexinput.R;

import com.discord.utilities.color.ColorCompat;

import kotlin.Unit;
import java.io.*;
import java.util.*;

import xyz.wingio.plugins.discovery.api.*;

import com.facebook.drawee.view.SimpleDraweeView;

public class WidgetDiscoverySheet extends BottomSheet {
    private SettingsAPI sets = PluginManager.plugins.get("Discovery").settings;
    private SettingsPage page;
    private DiscoveryGuild guild;

    public WidgetDiscoverySheet(SettingsPage page) {
        this.page = page;
    }

    public void setGuild(DiscoveryGuild guild) {
        this.guild = guild;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Context ctx = requireContext();
        int p = DimenUtils.dpToPx(16);
        int p2 = p/2;
        SimpleDraweeView banner = new SimpleDraweeView(ctx);
        LinearLayout.LayoutParams bannerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DimenUtils.dpToPx(70));
        banner.setLayoutParams(bannerParams);
        if(guild.splash != null) {
            banner.setVisibility(View.VISIBLE);
            var splashUrl = String.format("https://cdn.discordapp.com/discovery-splashes/%s/%s.jpg?size=1024", guild.id, guild.splash);
            banner.setImageURI(splashUrl);
        } else if (guild.banner != null) {
            banner.setVisibility(View.VISIBLE);
            var bannerUrl = String.format("https://cdn.discordapp.com/banners/%s/%s.jpg?size=1024", guild.id, guild.banner);
            banner.setImageURI(bannerUrl);
        } else {
            banner.setVisibility(View.GONE);
        }

        SimpleDraweeView icon = new SimpleDraweeView(ctx);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(DimenUtils.dpToPx(58), DimenUtils.dpToPx(58));
        iconParams.setMargins(DimenUtils.dpToPx(16), -DimenUtils.dpToPx(58/2), 0, 0);
        icon.setLayoutParams(iconParams);
        icon.setClipToOutline(true);
        var circle = new ShapeDrawable(new OvalShape());
        var paint = circle.getPaint();
        paint.setColor(android.graphics.Color.TRANSPARENT);
        icon.setBackground(circle);
        if(guild.icon != null) {
            var iconUrl = String.format("https://cdn.discordapp.com/icons/%s/%s.png?size=256", guild.id, guild.icon);
            icon.setImageURI(iconUrl);
        }

        TextView name = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(DimenUtils.dpToPx(16), DimenUtils.dpToPx(4), 0, 0);
        name.setLayoutParams(params);
        name.setTextSize(18f);
        name.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_bold));
        name.setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        name.setCompoundDrawablePadding(DimenUtils.dpToPx(6));
        name.setSingleLine(false);
        name.setText(guild.name);
        if(guild.features.contains("VERIFIED")){
            name.setCompoundDrawablesWithIntrinsicBounds(R.e.ic_verified_badge_banner, 0, 0, 0);
        } else if (guild.features.contains("PARTNERED")) {
            name.setCompoundDrawablesWithIntrinsicBounds(R.e.ic_partnered_badge_banner, 0, 0, 0);
        } else {
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        WidgetItemCategory primaryCategory = new WidgetItemCategory(ctx).setCategory(guild.primary_category_id);
        LinearLayout.LayoutParams primaryCategoryParams = (LinearLayout.LayoutParams) primaryCategory.getLayoutParams();
        if(primaryCategoryParams != null) {
            primaryCategoryParams.setMargins(DimenUtils.dpToPx(16), DimenUtils.dpToPx(8), 0, 0);
        }

        TextView kwHeader = new TextView(ctx, null, 0, R.i.UserProfile_Section_Header); kwHeader.setText("Keywords");
        kwHeader.setPadding(p, p2, p, 0);
        
        ChipGroup keywords = new ChipGroup(ctx);
        LinearLayout.LayoutParams keywordsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        keywordsParams.setMargins(DimenUtils.dpToPx(16), DimenUtils.dpToPx(8), 0, 0);
        keywords.setLayoutParams(keywordsParams);
        keywords.setChipSpacing(DimenUtils.dpToPx(4));
        if(guild.keywords != null) {
            for(var keyword : guild.keywords) {
                var chip = new WidgetItemCategory(ctx);
                chip.icon.setVisibility(View.GONE);
                chip.name.setText(keyword);
                ((LinearLayout.LayoutParams) chip.name.getLayoutParams()).setMargins(0, 0, 0, 0);
                keywords.addView(chip);
            }
        }

        LinearLayout memberCountLayout = new LinearLayout(ctx);
        memberCountLayout.setOrientation(LinearLayout.HORIZONTAL);
        memberCountLayout.setLayoutParams(params);

        Drawable dotImg = ContextCompat.getDrawable(ctx, R.e.drawable_circle_white_12dp).mutate();
        Drawable onlineDot = ContextCompat.getDrawable(ctx, R.e.drawable_circle_white_12dp).mutate();

        TextView memberCount = new TextView(ctx);
        memberCount.setTextSize(12f);
        memberCount.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium));
        memberCount.setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        memberCount.setSingleLine(true);
        memberCount.setText(String.format("%s Members", guild.approximate_member_count));
        memberCount.setCompoundDrawablePadding(p2);
        dotImg.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        memberCount.setCompoundDrawablesWithIntrinsicBounds(dotImg, null, null, null);

        TextView onlineCount = new TextView(ctx);
        onlineCount.setTextSize(12f);
        onlineCount.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium));
        onlineCount.setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        onlineCount.setSingleLine(true);
        onlineCount.setText(String.format("%s Online", guild.approximate_presence_count));
        onlineCount.setPadding(0, 0, p2, 0);
        onlineCount.setCompoundDrawablePadding(p2);
        onlineDot.setTint(0xFF3ba55d);
        onlineCount.setCompoundDrawablesWithIntrinsicBounds(onlineDot, null, null, null);
        
        memberCountLayout.addView(onlineCount);
        memberCountLayout.addView(memberCount);

        addView(banner);
        addView(icon);
        addView(name);
        addView(memberCountLayout);
        addView(primaryCategory);
        if(guild.keywords != null && guild.keywords.size() > 0) {
            addView(kwHeader);
            addView(keywords);
        }
    }
}