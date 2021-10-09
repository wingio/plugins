package xyz.wingio.plugins;

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
import com.aliucord.utils.*;
import com.aliucord.wrappers.ChannelWrapper;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.annotations.AliucordPlugin;
import com.discord.utilities.color.ColorCompat;
import com.discord.widgets.chat.list.actions.WidgetChatListActions;
import com.discord.stores.StoreStream;
import com.discord.models.message.Message;
import com.lytefast.flexinput.R;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@AliucordPlugin
@SuppressWarnings({ "unchecked", "unused" })
public class MessageLinkContext extends Plugin {
  public MessageLinkContext() {
    needsResources = true;
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
      resources.getIdentifier("ic_copy", "drawable", "com.aliucord.plugins"),
      null
    );
    var id = View.generateViewId();

    patcher.patch(
      WidgetChatListActions.class,
      "configureUI",
      new Class<?>[]{ WidgetChatListActions.Model.class },
      new PinePatchFn(
        callFrame -> {
          var _this = (WidgetChatListActions) callFrame.thisObject;
          var rootView = (NestedScrollView) _this.getView();
          if(rootView == null) return;
          var layout = (LinearLayout) rootView.getChildAt(0);
          if (layout == null || layout.findViewById(id) != null) return;
          var ctx = layout.getContext();
          var msg = ((WidgetChatListActions.Model) callFrame.args[0]).getMessage();
          long channelId = msg.getChannelId();
          Long messageId = msg.getId();
          var channel = StoreStream.getChannels().getChannel(channelId);
          var guildId = channel != null && ChannelWrapper.getGuildId(channel) != 0 ? String.valueOf(ChannelWrapper.getGuildId(channel)) : "@me";
          var view = new TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Icon);
          view.setId(id);
          view.setText("Copy Message Link");
          if (icon != null) icon.setTint(
            ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal)
          );
          view.setCompoundDrawablesRelativeWithIntrinsicBounds(
            icon,
            null,
            null,
            null
          );
          view.setOnClickListener(
            e -> {
              setClipboard(context,
                String.format(
                  "https://discord.com/channels/%s/%s/%s",
                  guildId,
                  channelId,
                  messageId
                )
              );
              Utils.showToast("Copied link", false);
              _this.dismiss();
            }
          );
          layout.addView(view, 6);
        }
      )
    );
  }

  @Override
  public void stop(Context context) {
    patcher.unpatchAll();
  }
}
