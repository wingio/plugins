package xyz.wingio.plugins.twemojieverywhere.patches;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;

import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.*;
import com.aliucord.patcher.*;
import com.aliucord.utils.*;
import com.aliucord.wrappers.*;

import xyz.wingio.plugins.twemojieverywhere.*;

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

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;
import kotlin.jvm.functions.Function0;

public class MemberListPatches extends Patches {
    public static void patchMemberList() {
        patcher.patch(ChannelMembersListViewHolderMember.class, "bind", new Class<?>[]{ ChannelMembersListAdapter.Item.Member.class, Function0.class}, new Hook(callFrame -> {
            try {
                var bindingField = ChannelMembersListViewHolderMember.class.getDeclaredField("binding");
                bindingField.setAccessible(true);
                WidgetChannelMembersListItemUserBinding binding = (WidgetChannelMembersListItemUserBinding) bindingField.get(callFrame.thisObject);
                ViewGroup layout = (ViewGroup) binding.getRoot();
                TextView uText = (TextView) layout.findViewById(Utils.getResId("username_text", "id"));
                TextView sText = (TextView) layout.findViewById(Utils.getResId("channel_members_list_item_game", "id"));
                if(Settings.inMemberList()) uText.setText(renderTwemoji(uText.getContext(), uText.getText()));
            } catch(Throwable e) {logger.error("Error adding Twemoji", e);}
        }));
    }
}