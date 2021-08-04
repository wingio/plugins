package com.aliucord.plugins;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.plugins.testplugin.*;
import com.discord.api.premium.PremiumTier;
import com.discord.databinding.WidgetChatOverlayBinding;
import com.discord.stores.StoreStream;
import com.discord.widgets.chat.input.*;
import com.discord.widgets.chat.overlay.WidgetChatOverlay$binding$2;

public class TestPlugin extends Plugin {

    public TestPlugin() {
        settingsTab = new SettingsTab(PluginSettings.class).withArgs(settings);
        needsResources = true;
    }
    
    private Drawable pluginIcon;
    public RelativeLayout overlay;

    @NonNull
  @Override
  public Manifest getManifest() {
    var manifest = new Manifest();
    manifest.authors =
      new Manifest.Author[] {
        new Manifest.Author("Wing", 298295889720770563L),
      };
    manifest.description = "Used for testing";
    manifest.version = "1.0.0";
    manifest.updateUrl =
      "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    return manifest;
  }

    @Override
    public void start(Context context) throws NoSuchMethodException {
        pluginIcon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_editfriend", "drawable", "com.aliucord.plugins"), null );
        final String maxChars = StoreStream.getUsers().getMe().getPremiumTier() == PremiumTier.TIER_2 ? "4000" : "2000";
        final TextView counter = new TextView(context);
        counter.setTypeface(ResourcesCompat.getFont(context, Constants.Fonts.whitney_medium));
        counter.setTextSize(Utils.dpToPx(4));
        counter.setTextColor(Color.WHITE);
        counter.setVisibility(View.GONE);
        counter.setGravity(Gravity.CENTER_VERTICAL);

        final ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        lp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        final int overlayId = Utils.getResId("chat_overlay_typing", "id");

        patcher.patch(WidgetChatOverlay$binding$2.class.getDeclaredMethod("invoke", View.class), new PinePatchFn(callFrame -> {
            if (counter.getParent() != null) return;

            final WidgetChatOverlayBinding binding = (WidgetChatOverlayBinding) callFrame.getResult();
            // overlay = (RelativeLayout) binding.a.findViewById(overlayId);
            // overlay.addView(counter, lp);
            // binding.a.addView(overlay);
        }));

        patcher.patch(WidgetChatInput.class, "onViewBound", new Class<?>[] { View.class }, new PinePatchFn(callFrame -> {
          View _this = (View) callFrame.args[0];
          // var rootView = _this.getView();
          // if(rootView == null) return;
          // int inputId = Utils.getResId("main_input_container", "id");
          // LinearLayout shell = (LinearLayout) rootView.findViewById(inputId);
          FrameLayout chatinput = (FrameLayout) _this.findViewById(Utils.getResId("widget_chat_input", "id"));
          //LinearLayout input = (LinearLayout) chatinput.getChildAt(0).getChildAt(2).getChildAt(1).getChildAt(6).getChildAt(0).getChildAt(1);
          Utils.log("Hello");
          chatinput.addView(counter, 1);
        }));

        patcher.patch(AppFlexInputViewModel.class.getDeclaredMethod("onInputTextChanged", String.class, Boolean.class), new PinePatchFn(callFrame -> {
            final String str = (String) callFrame.args[0];
            counter.setVisibility(str.equals("") ? View.GONE : View.VISIBLE);
            counter.setText(String.format("%s/%s", str.length(), maxChars));
        }));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
