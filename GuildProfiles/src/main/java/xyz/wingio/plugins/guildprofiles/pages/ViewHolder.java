package xyz.wingio.plugins.guildprofiles.pages;

import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.facebook.drawee.view.SimpleDraweeView;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final int iconId = Utils.getResId("user_profile_adapter_item_server_image", "id");
    private static final int iconTextId = Utils.getResId("user_profile_adapter_item_server_text", "id");
    private static final int serverNameId = Utils.getResId("user_profile_adapter_item_server_name", "id");
    private static final int serverNickId = Utils.getResId("user_profile_adapter_item_user_display_name", "id");
    private static final int gmaId = Utils.getResId("guild_member_avatar", "id");

    private final Adapter adapter;

    public final ImageView icon;
    public final TextView iconText;
    public final TextView name;
    public final TextView nick;

    public ViewHolder(Adapter adapter, @NonNull ConstraintLayout layout) {
        super(layout);
        this.adapter = adapter;

        icon = (ImageView) layout.findViewById(iconId);
        iconText = (TextView) layout.findViewById(iconTextId);
        name = (TextView) layout.findViewById(serverNameId);
        nick = (TextView) layout.findViewById(serverNickId);
        name.setGravity(Gravity.CENTER_VERTICAL);
        layout.findViewById(gmaId).setVisibility(View.GONE);

        layout.setOnClickListener(this);
    }

    @Override public void onClick(View view) {
        adapter.onClick(view.getContext(), getAdapterPosition());
    }
}