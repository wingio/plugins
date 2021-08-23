package com.aliucord.plugins.guildprofiles.pages;

import android.content.Context;
import android.view.*;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.DimenRes;

import com.aliucord.Utils;
import com.discord.models.member.GuildMember;
import com.discord.models.user.User;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.extensions.SimpleDraweeViewExtensionsKt;
import com.discord.utilities.icon.IconUtils;
import com.discord.utilities.images.MGImages;
import com.discord.stores.*;
import com.lytefast.flexinput.R;
import d0.z.d.m;
import com.facebook.drawee.view.SimpleDraweeView;

import kotlin.jvm.functions.Function1;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {
    private static final int layoutId = Utils.getResId("widget_user_profile_adapter_item_server", "layout");

    private final List<GuildMember> friends;
    private final MutualFriendsPage page;

    public Adapter(MutualFriendsPage page, List<GuildMember> friends) {
        this.friends = friends;
        this.page = page;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var layout = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(this, (RelativeLayout) layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        var friend = friends.get(position);
        var friendUser = StoreStream.getUsers().getUsers().get(friend.getUserId());
        var color = Integer.valueOf(ColorCompat.getThemedColor(holder.itemView.getContext(), R.b.colorBackgroundPrimary));
        if (friend.hasAvatar()) {
            int asu = Utils.getResId("avatar_size_unrestricted", "dimen");
            this.setAvatar(holder.icon, friendUser, false, 0, friend);
            holder.iconText.setVisibility(View.GONE);
        } else {
            holder.icon.setVisibility(View.GONE);
        }

        holder.name.setText(friendUser.getUsername());
    }

    public final void setAvatar(SimpleDraweeView simpleDraweeView, User user, boolean z2, @DimenRes int i, GuildMember guildMember) {
        //m.checkNotNullParameter(simpleDraweeView, "$this$setAvatar");
        int dimensionPixelSize = 1024;
        int iconResId = Utils.getResId("uikit_icon_url", "id");
        String forGuildMemberOrUser = IconUtils.INSTANCE.getForGuildMemberOrUser(user, guildMember, dimensionPixelSize > 0 ? Integer.valueOf(IconUtils.getMediaProxySize(dimensionPixelSize)) : null);
        if (!m.areEqual(forGuildMemberOrUser, simpleDraweeView.getTag(iconResId))) {
            simpleDraweeView.setTag(iconResId, forGuildMemberOrUser);
            IconUtils.setIcon$default(simpleDraweeView, forGuildMemberOrUser, i, (Function1) null, (MGImages.ChangeDetector) null, 24, (Object) null);
        }
    }

    public void onClick(Context ctx, int position) {
        
    }
}