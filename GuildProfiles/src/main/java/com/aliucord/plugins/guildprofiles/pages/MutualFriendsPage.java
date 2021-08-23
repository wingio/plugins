package com.aliucord.plugins.guildprofiles.pages;

import android.content.Context;
import android.util.Base64;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliucord.*;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.utils.RxUtils;
import com.discord.api.permission.Permission;
import com.discord.models.guild.Guild;
import com.discord.models.member.GuildMember;
import com.discord.restapi.RestAPIParams;
import com.discord.stores.*;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.utilities.rest.RestAPI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MutualFriendsPage extends SettingsPage {

    private final Map<Long, GuildMember> members;
    private final String name;

    public MutualFriendsPage(Map<Long, GuildMember> members, String name) {
        this.members = members;
        this.name = name;
    }

    @Override
    public void onViewBound(View view) {
        super.onViewBound(view);
        var storeUserRelationships = StoreStream.getUserRelationships();
        var users = members
                        .entrySet()
                        .stream()
                        .filter(r -> storeUserRelationships.getRelationships().get(r.getKey()) == 1)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<GuildMember> userList = new ArrayList<>();
        userList.addAll(users.values());
        setActionBarTitle(userList.size() + " Mutual Friends");
        setActionBarSubtitle(name);
        Utils.showToast(ctx, String.valueOf(storeUserRelationships.getRelationships()));

        var ctx = view.getContext();

        setPadding(0);

        var recycler = new RecyclerView(ctx);
        recycler.setLayoutManager(new LinearLayoutManager(ctx, RecyclerView.VERTICAL, false));
        
        var adapter = new Adapter(this, userList);
        recycler.setAdapter(adapter);

        addView(recycler);
    }
}