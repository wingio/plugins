/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.plugins.favoritemessages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.*;
import android.widget.*;
import android.graphics.*;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.views.*;
import com.aliucord.plugins.FavoriteMessages;
import com.discord.utilities.color.ColorCompat;
import com.discord.views.CheckedSetting;
import com.google.android.material.card.MaterialCardView;
import java.net.*;
import java.io.*;
import com.lytefast.flexinput.R;

public class MessageCard extends MaterialCardView {
    public final LinearLayout root;
    public final TextView authorView;
    public final TextView contentView;
    public final ImageView avatarView;

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            FavoriteMessages.logger.error("Error getting bitmap from URL", e);
            return null;
        }
    }

    @SuppressLint("SetTextI18n")
    public MessageCard(Context ctx) {
        super(ctx);
        setRadius(Utils.getDefaultCardRadius());
        setCardBackgroundColor(ColorCompat.getThemedColor(ctx, R.b.colorBackgroundSecondary));
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        int p = Utils.getDefaultPadding();
        int p2 = p / 2;

        root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);

        contentView = new TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Addition);
        contentView.setPadding(p, p, p, p2);

        avatarView = new ImageView(ctx);
        avatarView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(Utils.dpToPx(48),Utils.dpToPx(48));
        avatarView.setLayoutParams(parms);
        

        authorView = new TextView(ctx);
        authorView.setTextSize(17.0f);
        authorView.setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium));
        authorView.setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));

        LinearLayout authorField = new LinearLayout(ctx);
        authorField.setOrientation(LinearLayout.HORIZONTAL);
        authorField.setGravity(Gravity.CENTER_VERTICAL);
        authorField.setPadding(p, p, p, p2);
        authorField.addView(avatarView);
        authorField.addView(authorView);
        

        root.addView(authorField);
        root.addView(contentView);


        addView(root);
    }
}
