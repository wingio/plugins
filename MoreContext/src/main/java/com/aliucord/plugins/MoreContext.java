package com.aliucord.plugins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.wrappers.ChannelWrapper;
import com.aliucord.wrappers.messages.MessageWrapper;
import com.discord.app.AppBottomSheet;
import com.discord.stores.StoreStream;
import com.discord.utilities.color.ColorCompat;
import com.discord.widgets.chat.list.actions.WidgetChatListActions;
import com.lytefast.flexinput.R$b;
import com.lytefast.flexinput.R$h;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@SuppressWarnings({ "unchecked", "unused" })
public class MoreContext extends Plugin {

  public MoreContext() {
    needsResources = true;
    settings = new Settings(PluginSettings.class, Settings.Type.BOTTOMSHEET);
  }

  public static final class PluginSettings extends AppBottomSheet {

    public int getContentViewResId() {
      return 0;
    }

    @Override
    public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState
    ) {
      SettingsAPI sets = PluginManager.plugins.get("MoreContext").sets;
      Context context = inflater.getContext();
      LinearLayout layout = new LinearLayout(context);
      layout.setBackgroundColor(
        ColorCompat.getThemedColor(context, R$b.colorBackgroundPrimary)
      );

      layout.addView(createSwitch(context, sets, "link", "Copy message link"));
      layout.addView(
        createSwitch(context, sets, "copyraw", "Copy text with markdown")
      );
      return layout;
    }

    private CheckedSetting createSwitch(
      Context context,
      SettingsAPI sets,
      String key,
      String label
    ) {
      CheckedSetting cs = Utils.createCheckedSetting(
        context,
        CheckedSetting.ViewType.SWITCH,
        label,
        null
      );
      cs.setChecked(sets.getBool(key, true));
      cs.setOnCheckedListener(c -> sets.setBool(key, c));
      return cs;
    }
  }

  @NonNull
  @Override
  public Manifest getManifest() {
    var manifest = new Manifest();
    manifest.authors =
      new Manifest.Author[] {
        new Manifest.Author("Wing", 298295889720770563L),
      };
    manifest.description = "Adds additional context menu items for messages";
    manifest.version = "1.0.0";
    manifest.updateUrl =
      "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    return manifest;
  }

  private void setClipboard(Context context, String text) {
    if (
      android.os.Build.VERSION.SDK_INT <
      android.os.Build.VERSION_CODES.HONEYCOMB
    ) {
      android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(
        Context.CLIPBOARD_SERVICE
      );
      clipboard.setText(text);
    } else {
      android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(
        Context.CLIPBOARD_SERVICE
      );
      android.content.ClipData clip = android.content.ClipData.newPlainText(
        "Copied Text",
        text
      );
      clipboard.setPrimaryClip(clip);
    }
  }

  @Override
  public void start(Context context) {
    Drawable icon = ResourcesCompat.getDrawable(
      resources,
      resources.getIdentifier("ic_link", "drawable", "com.aliucord.plugins"),
      null
    );
    Drawable copyicon = ResourcesCompat.getDrawable(
      resources,
      resources.getIdentifier("ic_copy", "drawable", "com.aliucord.plugins"),
      null
    );
    AtomicReference<LinearLayout> layoutRef = new AtomicReference<>();
    var linkid = View.generateViewId();
    var rawid = View.generateViewId();
    Utils.log("Generated ID");

    patcher.patch(
      WidgetChatListActions.class,
      "configureUI",
      new Class<?>[] { WidgetChatListActions.Model.class },
      new PinePatchFn(
        callFrame -> {
          var layout = layoutRef.get();
          Utils.log("Created layout");
          if (layout == null || layout.findViewById(id) != null) return;
          var ctx = layout.getContext();
          Utils.log("Recieved context");
          var msg =
            ((WidgetChatListActions.Model) callFrame.args[0]).getMessage();
          Utils.log("Got message");
          MessageWrapper mw = new MessageWrapper(msg);
          long channelId = mw.getChannelId();
          Long messageId = mw.getId();
          var channel = StoreStream.getChannels().getChannel(channelId);
          var guildId = channel != null &&
            ChannelWrapper.getGuildId(channel) != 0
            ? String.valueOf(ChannelWrapper.getGuildId(channel))
            : "@me";

          if (sets.getBool("link", true)) {
            var copylink = new TextView(ctx, null, 0, R$h.UiKit_Settings_Item_Icon);
            Utils.log("Created view");
            copylink.setId(linkid);
            copylink.setText("Copy Message Link");
            if (icon != null) icon.setTint(
              ColorCompat.getThemedColor(ctx, R$b.colorInteractiveNormal)
            );
            copylink.setCompoundDrawablesRelativeWithIntrinsicBounds(
              icon,
              null,
              null,
              null
            );
            copylink.setOnClickListener(
              e -> {
                setClipboard(
                  context,
                  String.format(
                    "https://discord.com/channels/%s/%s/%s",
                    guildId,
                    channelId,
                    messageId
                  )
                );
                Utils.showToast(context, "Copied link");
              }
            );
            layout.addView(copylink, 6);
          }


          if (sets.getBool("copyraw", true)) {
            var view = new TextView(ctx, null, 0, R$h.UiKit_Settings_Item_Icon);
            Utils.log("Created view");
            view.setId(rawid);
            view.setText("Copy text with markdown");
            if (copyicon != null) copyicon.setTint(
              ColorCompat.getThemedColor(ctx, R$b.colorInteractiveNormal)
            );
            view.setCompoundDrawablesRelativeWithIntrinsicBounds(
              copyicon,
              null,
              null,
              null
            );
            view.setOnClickListener(
              e -> {
                setClipboard(
                  context,
                  mw.getContent()
                );
                Utils.showToast(context, "Copied text");
              }
            );
            layout.addView(view, 5);
          }



        }
      )
    );

    patcher.patch(
      WidgetChatListActions.class,
      "onViewCreated",
      new Class<?>[] { View.class, Bundle.class },
      new PinePatchFn(
        callFrame -> {
          layoutRef.set(
            (LinearLayout) ((NestedScrollView) callFrame.args[0]).getChildAt(0)
          );
        }
      )
    );
  }

  @Override
  public void stop(Context context) {
    patcher.unpatchAll();
  }
}
