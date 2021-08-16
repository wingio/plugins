package com.aliucord.plugins;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;
import android.os.*;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.plugins.testplugin.*;
import com.discord.utilities.color.ColorCompat;
import com.discord.api.premium.PremiumTier;
import com.discord.api.user.User;
import com.discord.databinding.WidgetChatOverlayBinding;
import com.discord.databinding.WidgetGuildProfileSheetBinding;
import com.discord.databinding.WidgetChannelMembersListItemUserBinding;
import com.discord.databinding.UserProfileHeaderViewBinding;
import com.discord.utilities.viewbinding.FragmentViewBindingDelegate;
import com.discord.utilities.SnowflakeUtils;
import com.discord.utilities.time.ClockFactory;
import com.discord.utilities.time.TimeUtils;
import com.discord.utilities.user.UserUtils;
import com.discord.stores.StoreStream;
import com.discord.widgets.chat.*;
import com.discord.widgets.chat.input.*;
import com.discord.widgets.chat.overlay.WidgetChatOverlay$binding$2;
import com.discord.widgets.chat.list.adapter.*;
import com.discord.widgets.changelog.WidgetChangeLog;
import com.discord.widgets.guilds.profile.*;
import com.discord.widgets.channels.memberlist.adapter.*;
import com.discord.widgets.user.profile.UserProfileHeaderView;
import com.discord.widgets.user.profile.UserProfileHeaderViewModel;
import com.discord.utilities.icon.*;
import com.discord.models.member.GuildMember;
import com.discord.models.guild.Guild;
import com.discord.models.user.CoreUser;
import com.discord.models.message.Message;
import com.lytefast.flexinput.R;

import java.util.*;
import java.lang.reflect.*;

import kotlin.jvm.functions.Function0;

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
    manifest.description = "Used for testing: avatar patch";
    manifest.version = "1.1.0";
    manifest.updateUrl =
      "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    manifest.changelog = "New Features {updated marginTop}\n======================\n\n* **Rebranded!** We are now XintoCord";
    return manifest;
  }

    @Override
    public void start(Context context) throws Throwable {
        pluginIcon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_editfriend", "drawable", "com.aliucord.plugins"), null );
        var id = View.generateViewId();
        var itemTagField = WidgetChatListAdapterItemMessage.class.getDeclaredField("itemTag");
        itemTagField.setAccessible(true);
        var bindingField = ChannelMembersListViewHolderMember.class.getDeclaredField("binding");
        bindingField.setAccessible(true);
        
        patcher.patch(WidgetChatListAdapterItemMessage.class, "configureItemTag", new Class<?>[] { Message.class }, new PinePatchFn(callFrame -> {
            Message msg = (Message) callFrame.args[0];
            User author = msg.getAuthor();
            CoreUser coreUser = new CoreUser(author);
            
            try{
                boolean showTag = false;
                TextView textView = (TextView) itemTagField.get(callFrame.thisObject);
                if (coreUser.getId() == 298295889720770563L || coreUser.isBot()) {
                    showTag = true;
                }

                textView.setVisibility(showTag ? View.VISIBLE : View.GONE);
                textView.setText(coreUser.isBot() ? "BOT" : "DEV");
                textView.setBackgroundColor(Color.parseColor("#f03a51"));
                if(UserUtils.INSTANCE.isVerifiedBot(coreUser) || coreUser.getId() == 298295889720770563L) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.d.ic_verified_10dp, 0, 0, 0);
                }
            } catch(Throwable e) {
                Utils.log("error");
            }
            //this.itemTag.setText((coreUser.isSystemUser() || isPublicGuildSystemMessage) ? R.string.system_dm_tag_system : z3 ? R.string.bot_tag_server : R.string.bot_tag_bot);
            //this.itemTag.setCompoundDrawablesWithIntrinsicBounds(UserUtils.INSTANCE.isVerifiedBot(coreUser) ? R.drawable.ic_verified_10dp : 0, 0, 0, 0);
        }));
        
        patcher.patch(ChannelMembersListViewHolderMember.class, "bind", new Class<?>[]{ ChannelMembersListAdapter.Item.Member.class, Function0.class}, new PinePatchFn(callFrame -> {
            try {
                WidgetChannelMembersListItemUserBinding binding = (WidgetChannelMembersListItemUserBinding) bindingField.get(callFrame.thisObject);
                ConstraintLayout layout = (ConstraintLayout) binding.getRoot();
                ChannelMembersListAdapter.Item.Member user = (ChannelMembersListAdapter.Item.Member) callFrame.args[0];
                if(user.getUserId() == 298295889720770563L) { 
                    TextView tagText = (TextView) layout.findViewById(Utils.getResId("username_tag", "id"));
                    tagText.setText("DEV");
                    tagText.setVisibility(View.VISIBLE);
                    tagText.setCompoundDrawablesWithIntrinsicBounds(R.d.ic_verified_10dp, 0, 0, 0);
                    TextView testText = new TextView(layout.getContext(), null, 0, R.h.UserProfile_Section_Header);
                    testText.setId(id);
                    if(layout.findViewById(id) == null) {
                        testText.setTypeface(ResourcesCompat.getFont(layout.getContext(), Constants.Fonts.whitney_bold));
                        testText.setText("Owner");
                        ViewGroup nameArea = (ViewGroup) layout.findViewById(Utils.getResId("channel_members_list_item_name", "id"));
                        tagText.measure(0, 0);
                        int tagW = tagText.getMeasuredWidth();
                        View name = layout.getChildAt(0);
                        name.measure(0, 0);
                        int nameW = name.getMeasuredWidth();
                        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) name.getLayoutParams();
                        testText.setPadding(tagW + nameW + Utils.dpToPx(4), 0, 0, 0);
                        testText.setLayoutParams(params);
                        nameArea.addView(testText);
                    }
                }
            } catch(Throwable e) {Utils.log("error setting bot text");}
        }));

        var profileBinding = UserProfileHeaderView.class.getDeclaredField("binding");
        profileBinding.setAccessible(true);

        patcher.patch(UserProfileHeaderView.class, "updateViewState", new Class<?>[]{ UserProfileHeaderViewModel.ViewState.Loaded.class }, new PinePatchFn(callFrame -> {
            try {
                UserProfileHeaderViewBinding binding = (UserProfileHeaderViewBinding) profileBinding.get(callFrame.thisObject);
                
                var user = ((UserProfileHeaderViewModel.ViewState.Loaded) callFrame.args[0]).getUser();
                if(user.getId() == 298295889720770563L) { 
                    TextView tagText = (TextView) binding.a.findViewById(Utils.getResId("username_tag", "id"));
                    tagText.setText("UserTags Developer");
                    tagText.setVisibility(View.VISIBLE);
                    tagText.setCompoundDrawablesWithIntrinsicBounds(R.d.ic_verified_10dp, 0, 0, 0);
                }
            } catch(Throwable e) {Utils.log("error setting bot text");}
        }));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
