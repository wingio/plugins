package xyz.wingio.plugins.twemojieverywhere.patches;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import android.text.*;
import android.text.style.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;

import xyz.wingio.plugins.twemojieverywhere.*;

import com.aliucord.Utils;
import com.aliucord.Constants;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.*;
import com.aliucord.patcher.*;
import com.aliucord.utils.*;
import com.aliucord.wrappers.*;

import com.discord.databinding.*;
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheet;
import com.discord.simpleast.core.parser.*;
import com.discord.utilities.textprocessing.*;
import com.discord.utilities.color.*;
import com.discord.utilities.user.*;
import com.discord.utilities.textprocessing.node.ZeroSpaceWidthNode;
import com.discord.simpleast.core.node.Node;
import com.discord.stores.*;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;
import com.discord.widgets.chat.list.entries.ChatListEntry;
import com.discord.widgets.user.profile.UserProfileHeaderView;
import com.discord.widgets.channels.memberlist.adapter.*;
import com.discord.widgets.user.profile.UserProfileHeaderViewModel;
import com.lytefast.flexinput.R;

import com.facebook.drawee.span.DraweeSpanStringBuilder;

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;
import kotlin.jvm.functions.Function0;

public class ProfileSheetPatches extends Patches {
    public static void patchUserSheet() {
        patcher.patch(UserProfileHeaderView.class, "updateViewState", new Class<?>[]{ UserProfileHeaderViewModel.ViewState.Loaded.class }, new Hook(callFrame -> {
        if(!Settings.inProfileSheet()) return;
        try {
            var profileBinding = UserProfileHeaderView.class.getDeclaredField("binding");
            profileBinding.setAccessible(true);
            UserProfileHeaderViewBinding binding = (UserProfileHeaderViewBinding) profileBinding.get(callFrame.thisObject);
            UserProfileHeaderViewModel.ViewState.Loaded loaded = (UserProfileHeaderViewModel.ViewState.Loaded) callFrame.args[0];
            TextView unameText = (TextView) binding.a.findViewById(Utils.getResId("username_text", "id"));TextView nickText = (TextView) binding.a.findViewById(Utils.getResId("user_profile_header_secondary_name", "id"));
            unameText.setTextColor(ColorCompat.getThemedColor(unameText.getContext(), R.b.colorHeaderPrimary)); unameText.setText(renderTwemoji(unameText.getContext(), unameText.getText()));
            if(nickText == null || nickText.getVisibility() == View.GONE) {
              Context ctx = unameText.getContext();
              DraweeSpanStringBuilder builder = new DraweeSpanStringBuilder(); var user = loaded.getUser();builder.append(renderTwemoji(unameText.getContext(), loaded.getUser().getUsername() + UserUtils.INSTANCE.getDiscriminatorWithPadding(loaded.getUser())));if(ResourcesCompat.getFont(ctx, Constants.Fonts.ginto_medium) != null) builder.setSpan(new CustomTypefaceSpan("", ResourcesCompat.getFont(ctx, Constants.Fonts.ginto_medium)), builder.length() - 6, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);builder.setSpan(new ForegroundColorSpan(ColorCompat.getThemedColor(unameText.getContext(), R.b.colorInteractiveNormal)), builder.length() - 6, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
              unameText.setText(builder);
            }
        } catch(Throwable e) {logger.error("Error adding Twemoji", e);}
    }));
    }
}