package com.aliucord.plugins.favoritemessages;

import android.view.*;
import android.widget.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;

import com.aliucord.*;
import com.discord.app.AppBottomSheet;
import com.discord.utilities.color.ColorCompat;
import com.aliucord.plugins.FavoriteMessages;
import com.lytefast.flexinput.*;

public static class MessageOptions extends BottomSheet {
    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Context ctx = requireContext();
        var resources = FavoriteMessages.resources;
        Drawable icon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_editfriend", "drawable", "com.aliucord.plugins"), null );
        if (icon != null) icon.setTint(
          ColorCompat.getThemedColor(ctx, R$b.colorInteractiveNormal)
        );
        
        var copyId = View.generateViewId();
        TextView copyOption = new TextView(ctx, null, 0, R$h.UiKit_Settings_Item_Icon);
        copyOption.setText("Copy Text");
        copyOption.setId(copyId);
        copyOption.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        copyOption.setOnClickListener(e -> {
            Utils.setClipboard("Message Content", bundle.getString("content"));
            Utils.showToast(ctx, "Copied message content");
            dismiss();
        });

        addView(copyOption);
    }
}