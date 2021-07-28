package com.aliucord.plugins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.aliucord.Logger;
import com.aliucord.Utils;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.patcher.PinePrePatchFn;
import com.discord.utilities.SnowflakeUtils;
import com.discord.utilities.icon.IconUtils;
import com.discord.widgets.user.usersheet.*;

import java.lang.reflect.Field;


@SuppressWarnings("unused")
public class HideCallButton extends Plugin {
    private static final Logger logger = new Logger("ViewProfileImages");
    private static Field fileNameField;
    private static Field idField;
    private static Field urlField;
    private static Field proxyUrlField;

    @NonNull
    @Override
    public Manifest getManifest() {
        var manifest = new Manifest();
        manifest.authors =
        new Manifest.Author[] {
          new Manifest.Author("Wing", 298295889720770563L),
        };
        manifest.description = "Hides the video and call buttons";
        manifest.version = "1.0.0";
        manifest.updateUrl = "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
        return manifest;
    }

    @SuppressWarnings({"AccessStaticViaInstance", "JavaReflectionMemberAccess"})
    @SuppressLint("SetTextI18n")
    @Override
    public void start(Context context) throws Throwable {

        final int videoId = Utils.getResId("user_sheet_video_action_button", "id");
        final int callId = Utils.getResId("user_sheet_call_action_button", "id");

        patcher.patch(WidgetUserSheet.class.getDeclaredMethod("onConfigure", WidgetUserSheetModel.class), new PinePatchFn(callFrame -> {
            var binding = WidgetUserSheet.access$getBinding$p((WidgetUserSheet) callFrame.thisObject);
            var root = binding.getRoot();

            var videoView = root.findViewById(videoId);
            var callView = root.findViewById(callId);

            videoView.setVisibility(View.GONE);
            videoView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            callView.setVisibility(View.GONE);
            callView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        }));
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}