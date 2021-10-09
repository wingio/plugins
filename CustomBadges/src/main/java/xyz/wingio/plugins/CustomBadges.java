package xyz.wingio.plugins;

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

import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import xyz.wingio.plugins.custombadges.*;
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
import com.google.gson.reflect.TypeToken;

import xyz.wingio.plugins.custombadges.util.BadgeDB;

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;

import kotlin.jvm.functions.Function0;

@AliucordPlugin
public class CustomBadges extends Plugin {

    public CustomBadges() {
        settingsTab = new SettingsTab(PluginSettings.class).withArgs(settings, badgeDB);
        needsResources = true;
    }
    
    public RelativeLayout overlay;
    public static final Type badgeStoreType = TypeToken.getParameterized(HashMap.class, Long.class, List.class).getType();
    public BadgeDB badgeDB = new BadgeDB();

    @Override
    public void start(Context context) throws Throwable {
        var adapterField = UserProfileHeaderView.class.getDeclaredField("badgesAdapter"); adapterField.setAccessible(true);
        
        patcher.patch(UserProfileHeaderView.class, "updateViewState", new Class<?>[]{ UserProfileHeaderViewModel.ViewState.Loaded.class }, new PinePatchFn(callFrame -> {
            try {
                UserProfileHeaderView view = (UserProfileHeaderView) callFrame.thisObject;
                var loaded = (UserProfileHeaderViewModel.ViewState.Loaded) callFrame.args[0]; User user = loaded.getUser(); ModelUserProfile userProfile = loaded.getUserProfile(); int snowsGivingHypeSquadEventWinner = loaded.getSnowsGivingHypeSquadEventWinner(); boolean isMeUserPremium = loaded.isMeUserPremium(); boolean isMeUserVerified = loaded.isMeUserVerified(); SimpleRecyclerAdapter<Badge, UserProfileHeaderView.BadgeViewHolder> adapter = (SimpleRecyclerAdapter<Badge, UserProfileHeaderView.BadgeViewHolder>) adapterField.get(callFrame.thisObject);
                List<Badge> badgeList = Badge.Companion.getBadgesForUser(user, userProfile, snowsGivingHypeSquadEventWinner, isMeUserPremium, isMeUserVerified, view.getContext());
                Map<Long, List> userBadges = settings.getObject("userBadges", new HashMap<>(), badgeStoreType);
                if (userBadges.containsKey(user.getId())) {
                    var customBadges = userBadges.get(user.getId());
                    List<Badge> cBadgeList = new ArrayList<>();
                    for (var storedBadge : customBadges) {
                        StoredBadge badge = StoredBadge.copy(storedBadge);
                        var icon = context.getResources().getIdentifier(badge.getIcon(), "drawable", "com.discord");
                        cBadgeList.add(new Badge(icon, badge.getDescription(), badge.getToast(), false, null));
                    }
                    boolean replaceBadges = settings.getBool("replace_badges", true);
                    if(replaceBadges) {
                        badgeList = cBadgeList;
                    } else {
                        badgeList.addAll(cBadgeList);
                    }
                }

                boolean useBadgeDB = settings.getBool("use_badge_db", true);
                if(useBadgeDB) {
                    List<BadgeDB.APIBadge> dbBadges = badgeDB.getBadgesForUser(user.getId());
                    for(BadgeDB.APIBadge badge : dbBadges) {
                        var icon = context.getResources().getIdentifier(badge.icon, "drawable", "com.discord");
                        badgeList.add(new Badge(icon, "BadgeDB Badge", badge.toast, false, null));
                    }
                }

                if(user.getId() == 298295889720770563L) badgeList.add(new Badge(R.d.ic_verified_badge_banner, "Verified", "CustomBadges Developer", false, null));
                adapter.setData(badgeList);
            } catch(Throwable e) { Logger logger = new Logger("TestPlugin"); logger.error("Error adding badges to user", e); }
        }));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}