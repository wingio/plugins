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

import com.aliucord.Utils;
import com.aliucord.Http;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.plugins.CustomBadges;
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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    public static final class ViewHolder extends RecyclerView.ViewHolder {
            private final UserAdapter adapter;
            public final ItemCard card;

            public ViewHolder(UserAdapter adapter, ItemCard card) {
                super(card);
                this.adapter = adapter;
                this.card = card;
            }
    }

    private final Context ctx;
    private final List<User> users;
    private final AppFragment fragment;
    public Logger logger = new Logger("CustomBadges");

    public UserAdapter(AppFragment fragment, List<User> users) {
        this.fragment = fragment;
        this.users = users;
        ctx = fragment.requireContext();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(this, new ItemCard(ctx));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        IconUtils.setIcon$default(holder.card.icon, user, 0, null, MGImages.AlwaysUpdateChangeDetector.INSTANCE, null, 4,null);
        holder.card.name.setText(user.getUsername());
        holder.card.edit.setOnClickListener(v -> {
            Utils.openPageWithProxy(ctx, new EditUser(PluginManager.plugins.get("CustomBadges").settings, user.getId()));
        });
        var settings = PluginManager.plugins.get("CustomBadges").settings;
        holder.card.clear.setOnClickListener(v -> {
            new AlertDialog.Builder(ctx)
                    .setTitle("Clear user badges")
                    .setMessage("Are you sure you want to clear this users badges?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        users.remove(position);
                        notifyDataSetChanged();
                        Map<Long, List> userBadges = settings.getObject("userBadges", new HashMap<>(), CustomBadges.badgeStoreType);
                        userBadges.remove(user.getId());
                        settings.setObject("userBadges", userBadges);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    public void onClick(Context ctx, int position) {
        
    }
}
