package com.aliucord.plugins.guildprofiles.pages;

import android.content.Context;
import android.view.*;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliucord.Utils;
import com.discord.models.member.GuildMember;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.extensions.SimpleDraweeViewExtensionsKt;
import com.discord.stores.*;
import com.lytefast.flexinput.R;

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
            SimpleDraweeViewExtensionsKt.setAvatar(holder.icon, friendUser, false, Utils.getResId("avatar_size_unrestricted", "dimen"), friend);
            holder.iconText.setVisibility(View.GONE);
        } else {
            holder.icon.setVisibility(View.GONE);
        }

        holder.name.setText(friendUser.getUsername());
    }

    public void onClick(Context ctx, int position) {
        
    }
}