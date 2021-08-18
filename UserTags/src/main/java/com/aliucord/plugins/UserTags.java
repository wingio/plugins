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
import com.aliucord.api.CommandsAPI;
import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.plugins.usertags.*;
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
import com.discord.utilities.guilds.*;
import com.discord.models.member.GuildMember;
import com.discord.models.guild.Guild;
import com.discord.models.user.CoreUser;
import com.discord.models.message.Message;
import com.discord.models.commands.ApplicationCommandOption;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.api.commands.CommandChoice;
import com.lytefast.flexinput.R;

import java.util.*;
import java.lang.reflect.*;

import kotlin.jvm.functions.Function0;

public class UserTags extends Plugin {

    public UserTags() {
        //settingsTab = new SettingsTab(PluginSettings.class).withArgs(settings);
        //needsResources = true;
    }
    
    public RelativeLayout overlay;
    public Logger logger = new Logger("UserTags");

    @NonNull
  @Override
  public Manifest getManifest() {
    var manifest = new Manifest();
    manifest.authors =
      new Manifest.Author[] {
        new Manifest.Author("Wing", 298295889720770563L),
      };
    manifest.description = "Gives everyone custom bot tags";
    manifest.version = "1.1.1";
    manifest.changelog = "New {added marginTop}\n======================\n\n* **Verified tags!** You can now set a tag as verified when using the '/usertags set' command";
    manifest.updateUrl =
      "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    return manifest;
  }

    @Override
    public void start(Context context) throws Throwable {
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
                String tag = settings.getString(String.valueOf(coreUser.getId()), null);
                boolean verified = settings.getBool(coreUser.getId() + "_verified", false);
                boolean isServer = PublicGuildUtils.INSTANCE.isPublicGuildSystemMessage(msg);
                if (coreUser.getId() == 298295889720770563L || coreUser.isBot() || tag != null) {
                    showTag = true;
                }
                if(textView != null){
                    textView.setVisibility(showTag ? View.VISIBLE : View.GONE);
                    textView.setText(isServer ? "SERVER" : coreUser.isBot() ? "BOT" : String.valueOf(tag));
                    if(coreUser.getId() == 298295889720770563L) {
                        textView.setText("DEV");
                    }
                    if(UserUtils.INSTANCE.isVerifiedBot(coreUser) || coreUser.getId() == 298295889720770563L || verified == true) {
                        textView.setCompoundDrawablesWithIntrinsicBounds(R.d.ic_verified_10dp, 0, 0, 0);
                    }
                }
            } catch(Throwable e) {
                logger.error("Error adding tag to message", e);
            }
        }));
        
        patcher.patch(ChannelMembersListViewHolderMember.class, "bind", new Class<?>[]{ ChannelMembersListAdapter.Item.Member.class, Function0.class}, new PinePatchFn(callFrame -> {
            try {
                WidgetChannelMembersListItemUserBinding binding = (WidgetChannelMembersListItemUserBinding) bindingField.get(callFrame.thisObject);
                ConstraintLayout layout = (ConstraintLayout) binding.getRoot();
                ChannelMembersListAdapter.Item.Member user = (ChannelMembersListAdapter.Item.Member) callFrame.args[0];
                String tag = settings.getString(String.valueOf(user.getUserId()), null);
                boolean verified = settings.getBool(user.getUserId() + "_verified", false);
                if(user.getUserId() == 298295889720770563L) {
                    tag = "DEV";
                }
                if(tag != null && user.isBot() == false) { 
                    TextView tagText = (TextView) layout.findViewById(Utils.getResId("username_tag", "id"));
                    tagText.setText(String.valueOf(tag));
                    if(user.getUserId() == 298295889720770563L || verified == true) {
                        tagText.setCompoundDrawablesWithIntrinsicBounds(R.d.ic_verified_10dp, 0, 0, 0);
                    }
                    tagText.setVisibility(View.VISIBLE);
                }
            } catch(Throwable e) {logger.error("Error setting bot text in member list", e);}
        }));

        var profileBinding = UserProfileHeaderView.class.getDeclaredField("binding");
        profileBinding.setAccessible(true);

        patcher.patch(UserProfileHeaderView.class, "updateViewState", new Class<?>[]{ UserProfileHeaderViewModel.ViewState.Loaded.class }, new PinePatchFn(callFrame -> {
            try {
                UserProfileHeaderViewBinding binding = (UserProfileHeaderViewBinding) profileBinding.get(callFrame.thisObject);
                
                var user = ((UserProfileHeaderViewModel.ViewState.Loaded) callFrame.args[0]).getUser();
                var tag = settings.getString(String.valueOf(user.getId()), null);
                boolean verified = settings.getBool(user.getId() + "_verified", false);
                if(user.getId() == 298295889720770563L) {
                    tag = "UserTags Developer";
                }
                if(tag != null && user.isBot() == false) { 
                    TextView tagText = (TextView) binding.a.findViewById(Utils.getResId("username_tag", "id"));
                    tagText.setText(String.valueOf(tag));
                    if(user.getId() == 298295889720770563L || verified == true) {
                        tagText.setCompoundDrawablesWithIntrinsicBounds(R.d.ic_verified_10dp, 0, 0, 0);
                    }
                    tagText.setVisibility(View.VISIBLE);
                }
            } catch(Throwable e) {logger.error("Error setting bot text in profile sheet", e);}
        }));

        var userOption = new ApplicationCommandOption(ApplicationCommandType.USER,"user","User you want to give a tag to",null,true,true,null,null);
        var labelOption = new ApplicationCommandOption(ApplicationCommandType.STRING,"label","The label for the tag",null,true,true,null,null);
        var verifiedOption = new ApplicationCommandOption(ApplicationCommandType.BOOLEAN, "verified", "Whether the tag should show as verified", null, false, false, null, null);
        var setOption = new ApplicationCommandOption(ApplicationCommandType.SUBCOMMAND,"set","Set a tag",null,false,false,null,Arrays.asList(userOption, labelOption, verifiedOption));
        var clearOption = new ApplicationCommandOption(ApplicationCommandType.SUBCOMMAND,"clear","Clear a tag",null,false,false,null,Arrays.asList(userOption));
        commands.registerCommand(
        "usertags",
        "Modify a tag for a particular user",
        Arrays.asList(setOption, clearOption),
        ctx -> {
            if (ctx.containsArg("set")) {
                var setargs = ctx.getSubCommandArgs("set");
                var user = (String) setargs.get("user");
                var label = (String) setargs.get("label");
                var verified = (Boolean) setargs.get("verified");
                verified = verified == null ? false : verified;
                if ( user == null || user.equals("") || label == null || label.equals("")) {
                    return new CommandsAPI.CommandResult("Missing arguments",null,false);
                }

                settings.setString(user, String.valueOf(label));
                settings.setBool(user + "_verified", verified);

                return new CommandsAPI.CommandResult("Set tag", null, false);
            }

            if (ctx.containsArg("clear")) {
                var setargs = ctx.getSubCommandArgs("clear");
                var user = (String) setargs.get("user");
                if (user == null || user.equals("")) {
                    return new CommandsAPI.CommandResult("Missing arguments",null,false);
                }

                settings.setString(user, null);

                return new CommandsAPI.CommandResult("Cleared tag", null, false);
            }

            return new CommandsAPI.CommandResult();
        }
        );
    }

    @Override
    public void stop(Context context) { 
        patcher.unpatchAll();
        commands.unregisterAll();
    }
}
