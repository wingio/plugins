package com.aliucord.plugins;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import android.os.*;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.plugins.testplugin.*;
import com.discord.utilities.color.ColorCompat;
import com.discord.api.premium.PremiumTier;
import com.discord.api.guild.GuildFeature;
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
import com.discord.widgets.user.Badge;
import com.discord.utilities.icon.*;
import com.discord.utilities.views.SimpleRecyclerAdapter;
import com.discord.models.member.GuildMember;
import com.discord.models.guild.Guild;
import com.discord.models.user.User;
import com.discord.models.message.Message;
import com.discord.models.domain.ModelUserProfile;
import com.discord.utilities.view.text.SimpleDraweeSpanTextView;
import com.lytefast.flexinput.R;

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;

import kotlin.jvm.functions.Function0;

public class TestPlugin extends Plugin {

    public TestPlugin() {
        settingsTab = new SettingsTab(PluginSettings.class).withArgs(settings);
        needsResources = true;
    }
    
    public RelativeLayout overlay;

    @NonNull
  @Override
  public Manifest getManifest() {
    var manifest = new Manifest();
    manifest.authors =
      new Manifest.Author[] {
        new Manifest.Author("Wing", 298295889720770563L),
      };
    manifest.description = "Used for testing: partner patch";
    manifest.version = "1.1.0";
    manifest.updateUrl =
      "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    manifest.changelog = "New Features {updated marginTop}\n======================\n\n* **Rebranded!** We are now XintoCord";
    return manifest;
  }

    @Override
    public void start(Context context) throws Throwable {
        var id = View.generateViewId();
        var itemTagField = WidgetChatListAdapterItemMessage.class.getDeclaredField("itemTag");
        itemTagField.setAccessible(true);
        var avField = WidgetChatListAdapterItemMessage.class.getDeclaredField("itemAvatar");
        avField.setAccessible(true);
        var textField = WidgetChatListAdapterItemMessage.class.getDeclaredField("itemText");
        textField.setAccessible(true);
        var bindingField = ChannelMembersListViewHolderMember.class.getDeclaredField("binding");
        bindingField.setAccessible(true);
        
        //patcher.patch(WidgetChatListAdapterItemMessage.class, "configureItemTag", new Class<?>[] { Message.class }, new PinePatchFn(callFrame -> {
        //    Message msg = (Message) callFrame.args[0];
        //    User author = msg.getAuthor();
        //    CoreUser coreUser = new CoreUser(author);
        //    
        //    try{
        //        boolean showTag = false;
        //        TextView textView = (TextView) itemTagField.get(callFrame.thisObject);
        //        if (coreUser.getId() == 298295889720770563L || coreUser.isBot()) {
        //            showTag = true;
        //        }
        //        ImageView av = (ImageView) avField.get(callFrame.thisObject);
        //        av.setVisibility(View.GONE);
        //        av.setLayoutParams(new ConstraintLayout.LayoutParams(0, av.getLayoutParams().height));
        //        SimpleDraweeSpanTextView content =  (SimpleDraweeSpanTextView) textField.get(callFrame.thisObject);
        //        content.setPadding(0, 0, 0, 0);
        //
        //        textView.setVisibility(showTag ? View.VISIBLE : View.GONE);
        //        textView.setText(coreUser.isBot() ? "BOT" : "DEV");
        //        //Drawable bg = (Drawable) textView.getBackground();
        //        int[] colors = {Color.parseColor("#f03a51"), Color.parseColor("#94a2f0")};
        //        GradientDrawable gBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        //        gBg.setCornerRadius(Utils.dpToPx(2.5f));
        //        //bg.mutate();
        //        textView.setBackgroundDrawable(gBg);
        //        if(UserUtils.INSTANCE.isVerifiedBot(coreUser) || coreUser.getId() == 298295889720770563L) {
        //            textView.setCompoundDrawablesWithIntrinsicBounds(R.d.ic_verified_10dp, 0, 0, 0);
        //        }
        //    } catch(Throwable e) {
        //        Utils.log("error");
        //    }
        //    //this.itemTag.setText((coreUser.isSystemUser() || isPublicGuildSystemMessage) ? R.string.system_dm_tag_system : z3 ? R.string.bot_tag_server : R.string.bot_tag_bot);
        //    //this.itemTag.setCompoundDrawablesWithIntrinsicBounds(UserUtils.INSTANCE.isVerifiedBot(coreUser) ? R.drawable.ic_verified_10dp : 0, 0, 0, 0);
        //}));

        patcher.patch(Guild.class, "getFeatures", new Class<?>[]{ }, new PinePatchFn(callFrame -> {
             Set<GuildFeature> features = new HashSet<>();
             Guild g = (Guild) callFrame.thisObject;
             features.add(GuildFeature.PARTNERED);
             features.add(GuildFeature.VERIFIED);
             features.add(GuildFeature.ROLE_ICONS);
             if(g.getId() == 631297712939204608L){
                 callFrame.setResult(features);
             }
        }));

        // patcher.patch(Badge.Companion.class, "getBadgesForUser", new Class<?>[]{ User.class, ModelUserProfile.class, int.class, boolean.class, Context.class }, new PinePatchFn(callFrame -> {
        //     // var badges = (List<Badge>) callFrame.getResult();
        //     Utils.showToast((Context) callFrame.args[4], "Hello");
        //     List<Badge> badges = new ArrayList<>();
        //     badges.add(new Badge(R.d.ic_verified_badge, "CustomBadges Developer", "CustomBadges Developer", false, null));
        //     callFrame.setResult(badges);
        // }));
        var adapterField = UserProfileHeaderView.class.getDeclaredField("badgesAdapter"); adapterField.setAccessible(true);
        
        patcher.patch(UserProfileHeaderView.class, "updateViewState", new Class<?>[]{ UserProfileHeaderViewModel.ViewState.Loaded.class }, new PinePatchFn(callFrame -> {
            try {
                UserProfileHeaderView view = (UserProfileHeaderView) callFrame.thisObject;
                var loaded = (UserProfileHeaderViewModel.ViewState.Loaded) callFrame.args[0]; User user = loaded.getUser(); ModelUserProfile userProfile = loaded.getUserProfile(); int snowsGivingHypeSquadEventWinner = loaded.getSnowsGivingHypeSquadEventWinner(); boolean isMeUserPremium = loaded.isMeUserPremium(); boolean isMeUserVerified = loaded.isMeUserVerified(); SimpleRecyclerAdapter<Badge, UserProfileHeaderView.BadgeViewHolder> adapter = (SimpleRecyclerAdapter<Badge, UserProfileHeaderView.BadgeViewHolder>) adapterField.get(callFrame.thisObject);
                List<Badge> badgeList = Badge.Companion.getBadgesForUser(user, userProfile, snowsGivingHypeSquadEventWinner, isMeUserPremium, isMeUserVerified, view.getContext());
                Utils.log(userProfile.getPremiumSince(view.getContext()));
                Utils.log(userProfile.getPremiumGuildSince(view.getContext()));
                if(user.getId() == 298295889720770563L) badgeList.add(new Badge(R.d.ic_verified_badge, "Verified", "CustomBadges Developer", false, null));
                adapter.setData(badgeList);
            } catch(Throwable e) { Logger logger = new Logger("TestPlugin"); logger.error("Error adding badges to user", e); }
        }));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}

