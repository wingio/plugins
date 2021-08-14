/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.plugins.favoritemessages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.views.*;
import com.discord.utilities.color.ColorCompat;
import com.discord.views.CheckedSetting;
import com.google.android.material.card.MaterialCardView;
import com.lytefast.flexinput.R;

public class MessageCard extends MaterialCardView {
    public final LinearLayout root;
    // public final TextView authorView;
    public final TextView contentView;
    // public final ImageView avatarView;

    @SuppressLint("SetTextI18n")
    public MessageCard(Context ctx) {
        super(ctx);
        setRadius(Utils.getDefaultCardRadius());
        setCardBackgroundColor(ColorCompat.getThemedColor(ctx, R.b.colorBackgroundSecondary));
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        int p = Utils.getDefaultPadding();
        int p2 = p / 2;

        root = new LinearLayout(ctx);

        contentView = new TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Addition);
        contentView.setPadding(p, p, p, p2);
        root.addView(contentView);

        addView(root);
    }
}
