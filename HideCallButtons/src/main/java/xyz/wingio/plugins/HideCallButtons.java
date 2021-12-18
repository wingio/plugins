package xyz.wingio.plugins;

import android.content.Context;
import android.view.View;

import com.aliucord.Logger;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.Hook;
import com.discord.widgets.user.usersheet.*;

@SuppressWarnings("unused")
@AliucordPlugin
public class HideCallButtons extends Plugin {
    @Override
    public void start(Context context) throws Throwable {

        final int videoId = Utils.getResId("user_sheet_video_action_button", "id");
        final int callId = Utils.getResId("user_sheet_call_action_button", "id");

        patcher.patch(WidgetUserSheet.class, "configureNote", new Class<?>[]{ WidgetUserSheetViewModel.ViewState.Loaded.class }, new Hook(callFrame -> {
            var binding = WidgetUserSheet.access$getBinding$p((WidgetUserSheet) callFrame.thisObject);
            var root = binding.getRoot();

            var videoView = root.findViewById(videoId);
            var callView = root.findViewById(callId);

            videoView.setVisibility(View.GONE);
            callView.setVisibility(View.GONE);
        }));
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}