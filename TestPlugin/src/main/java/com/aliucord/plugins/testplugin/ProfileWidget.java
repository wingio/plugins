package com.aliucord.plugins.testplugin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.text.*;
import android.text.style.*;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.*;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.views.*;
import com.aliucord.views.Divider;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.icon.IconUtils;
import com.discord.utilities.images.MGImages;

import com.aliucord.Constants;
import com.discord.databinding.UserProfileHeaderBadgeBinding;
import com.discord.api.user.NsfwAllowance;
import com.discord.utilities.user.UserUtils;
import com.discord.utilities.views.*;
import com.discord.views.CheckedSetting;
import com.discord.models.user.User;
import com.discord.models.domain.ModelUserProfile;
import com.discord.widgets.user.profile.RightToLeftGridLayoutManager;
import com.discord.widgets.user.*;
import com.discord.nullserializable.NullSerializable;
import com.google.android.material.card.MaterialCardView;
import java.net.*;
import java.io.*;
import java.util.*;
import com.lytefast.flexinput.R;
import d0.z.d.o;
import kotlin.jvm.functions.Function2;
import com.facebook.drawee.view.SimpleDraweeView;

public class ProfileWidget extends LinearLayout {

    public static final class BadgeViewHolder extends SimpleRecyclerAdapter.ViewHolder<Badge> {
        private final UserProfileHeaderBadgeBinding binding;
        private ProfileWidget p;

        public BadgeViewHolder(UserProfileHeaderBadgeBinding userProfileHeaderBadgeBinding, ProfileWidget p) {
            super(p);
            this.p = p;
            ImageView imageView = userProfileHeaderBadgeBinding.a;
            this.binding = userProfileHeaderBadgeBinding;
        }

        public void bind(Badge badge) {
            this.binding.b.setImageResource(badge.getIcon());
            ImageView imageView = this.binding.b;
            CharSequence text = badge.getText();
            if (text == null) {
                text = badge.getTooltip();
            }
            imageView.setContentDescription(text);
        }
    }

    @SuppressLint("SetTextI18n")
    public ProfileWidget(Context ctx, User user) {
        super(ctx);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setBackgroundColor(0xFF202225);
        UserUtils userUtils = UserUtils.INSTANCE;

        View inflate = LayoutInflater.from(ctx).inflate(Utils.getResId("user_profile_header_view", "layout"), null, false);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflate;
        constraintLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        IconUtils.setIcon$default(constraintLayout.findViewById(Utils.getResId("avatar", "id")), user, 0, null, MGImages.AlwaysUpdateChangeDetector.INSTANCE, null, 4,null);

        TextView username = (TextView) constraintLayout.findViewById(Utils.getResId("username_text", "id"));
        String userTag = (String) userUtils.getUserNameWithDiscriminator(user, null, null);
        Spannable username_string = new SpannableString(userTag);
        username_string.setSpan(new ForegroundColorSpan(Color.WHITE), 0, user.getUsername().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        username_string.setSpan(new TypefaceSpan(ResourcesCompat.getFont(ctx, Constants.Fonts.ginto_bold)), 0, user.getUsername().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        username_string.setSpan(new TypefaceSpan(ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_semibold)), user.getUsername().length(), user.getUsername().length() + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        username.setText(username_string);

        RecyclerView badges = (RecyclerView) constraintLayout.findViewById(Utils.getResId("user_profile_header_badges_recycler", "id"));
        RightToLeftGridLayoutManager rightToLeftGridLayoutManager = new RightToLeftGridLayoutManager(ctx, 3, 1, true);
        badges.setLayoutManager(rightToLeftGridLayoutManager);
        Badge vbadge = new Badge(R.d.ic_verified_badge, "Verified", "CustomBadges Developer", false, null);
        NullSerializable.a aVar = new NullSerializable.a(null);
        NullSerializable.a aVar2 = new NullSerializable.a(null);
        
        List<Badge> badgeList = Badge.Companion.getBadgesForUser(user, new ModelUserProfile(), -1, true, true, ctx);
        badgeList.add(vbadge);
        badges.setAdapter(new BadgeAdapter(this, badgeList));

        LinearLayout username_wrap = (LinearLayout) constraintLayout.findViewById(Utils.getResId("user_profile_header_name_wrap", "id"));
        ViewGroup.LayoutParams params = username_wrap.getLayoutParams();
        params.height = Utils.dpToPx(31);
        username_wrap.setLayoutParams(params);

        SimpleDraweeView banner = (SimpleDraweeView) constraintLayout.findViewById(Utils.getResId("banner", "id"));
        banner.setBackgroundColor(0xFF9F4C4F);
        MGImages.setImage$default(banner, IconUtils.INSTANCE.getForUserBanner(user.getId(), user.getBanner(), 4096, false), 0, 0, false, null, null, 92, null);

        addView(constraintLayout);
    }

    public ProfileWidget(Context ctx){
        super(ctx);
    }
}
