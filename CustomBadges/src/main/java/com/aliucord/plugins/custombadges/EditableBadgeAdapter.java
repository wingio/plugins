package com.aliucord.plugins.custombadges;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Base64;
import android.app.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.DimenRes;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.*;

import com.aliucord.plugins.CustomBadges;
import com.aliucord.Utils;
import com.aliucord.Http;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.SettingsAPI;
import com.aliucord.fragments.SettingsPage;
import com.discord.app.*;
import com.discord.models.member.GuildMember;
import com.discord.models.user.User;
import com.discord.utilities.color.ColorCompat;
import com.discord.databinding.UserProfileHeaderBadgeBinding;
import com.discord.utilities.extensions.SimpleDraweeViewExtensionsKt;
import com.discord.utilities.icon.IconUtils;
import com.discord.utilities.images.MGImages;
import com.discord.stores.*;
import com.discord.widgets.user.usersheet.WidgetUserSheet;
import com.discord.widgets.user.*;
import com.lytefast.flexinput.R;
import com.facebook.drawee.view.SimpleDraweeView;

import kotlin.jvm.functions.Function1;
import java.util.*;

import java.io.*;

public class EditableBadgeAdapter extends RecyclerView.Adapter<EditableBadgeAdapter.ViewHolder> {

    public static final class ViewHolder extends RecyclerView.ViewHolder {
            private final EditableBadgeAdapter adapter;
            public final ItemCard card;

            public ViewHolder(EditableBadgeAdapter adapter, ItemCard card) {
                super(card);
                this.adapter = adapter;
                this.card = card;
            }
    }

    private final Context ctx;
    private final List<StoredBadge> badges;
    private final SettingsPage fragment;
    private final Long userId;
    public Logger logger = new Logger("CustomBadges");

    public EditableBadgeAdapter(SettingsPage fragment, List<StoredBadge> badges, Long userId) {
        this.fragment = fragment;
        this.badges = badges;
        this.userId = userId;
        ctx = fragment.requireContext();
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(this, new ItemCard(ctx));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoredBadge badge = badges.get(position);
        var icon = ctx.getResources().getIdentifier(badge.getIcon(), "drawable", "com.discord");
        holder.card.icon.setImageResource(icon);
        holder.card.edit.setText("Edit Badge");
        SettingsAPI settings = PluginManager.plugins.get("CustomBadges").settings;
        holder.card.edit.setOnClickListener(v -> {
            new SettingsSheet(fragment, PluginManager.plugins.get("CustomBadges").settings, userId, badge, position).show(fragment.getParentFragmentManager(), "CustomBadges");
        });

        holder.card.clear.setOnClickListener(v -> {
            new AlertDialog.Builder(ctx)
                    .setTitle("Delete Badge")
                    .setMessage("Are you sure you want to delete this badge?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        badges.remove(position);
                        notifyDataSetChanged();
                        Map<Long, List> userBadges = settings.getObject("userBadges", new HashMap<>(), CustomBadges.badgeStoreType);
                        userBadges.get(userId).remove(position);
                        settings.setObject("userBadges", userBadges);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    public void onClick(Context ctx, int position) {
        
    }
}
