/*
 * Copyright (c) 2021 Juby210
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.plugins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.entities.Plugin;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.views.Divider;
import com.discord.databinding.WidgetChatListActionsBinding;
import com.discord.models.domain.ModelMessage;
import com.discord.simpleast.code.CodeNode;
import com.discord.simpleast.code.CodeNode$a;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.textprocessing.Rules$createCodeBlockRule$codeStyleProviders$1;
import com.discord.utilities.textprocessing.node.BasicRenderContext;
import com.discord.utilities.textprocessing.node.BlockBackgroundNode;
import com.discord.widgets.chat.list.actions.WidgetChatListActions;
import com.lytefast.flexinput.R$b;
import com.lytefast.flexinput.R$h;

@SuppressWarnings({"unchecked", "unused"})
public class ViewRaw extends Plugin {

    @NonNull
    @Override
    public Manifest getManifest() {
        var manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("Wing", 298295889720770563L) };
        manifest.description = "Adds a context menu option to copy the message link";
        manifest.version = "1.0.0";
        manifest.updateUrl = "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
        return manifest;
    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
          android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
          clipboard.setText(text);
        } else {
          android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
          android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
          clipboard.setPrimaryClip(clip);
        }
      }

    @Override
    @SuppressLint("SetTextI18n")
    public void start(Context ctx) throws Throwable {
        var icon = ResourcesCompat.getDrawable(resources,
                resources.getIdentifier("ic_viewraw", "drawable", "com.aliucord.plugins"), null);
        var id = View.generateViewId();

        var c = WidgetChatListActions.class;
        var getBinding = c.getDeclaredMethod("getBinding");
        getBinding.setAccessible(true);

        patcher.patch(c, "configureUI", new Class<?>[]{ WidgetChatListActions.Model.class }, new PinePatchFn(callFrame -> {
            try {
                var binding = (WidgetChatListActionsBinding) getBinding.invoke(callFrame.thisObject);
                if (binding == null) return;
                TextView viewRaw = binding.a.findViewById(id);
                var viewRawPage = new Page();
                viewRawPage.message = ((WidgetChatListActions.Model) callFrame.args[0]).getMessage();
                viewRaw.setOnClickListener(e -> setClipboard(ctx, "test"));
            } catch (Throwable ignored) {}
        }));

        patcher.patch(c, "onViewCreated", new Class<?>[]{ View.class, Bundle.class }, new PinePatchFn(callFrame -> {
            var linearLayout = (LinearLayout) ((NestedScrollView) callFrame.args[0]).getChildAt(0);
            var context = linearLayout.getContext();
            var viewRaw = new TextView(context, null, 0, R$h.UiKit_Settings_Item_Icon);
            viewRaw.setText("Copy Link");
            if (icon != null) icon.setTint(ColorCompat.getThemedColor(context, R$b.colorInteractiveNormal));
            viewRaw.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
            viewRaw.setId(id);
            linearLayout.addView(viewRaw);
        }));
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}