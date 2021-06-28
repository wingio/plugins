package com.aliucord.plugins.tags;

import com.aliucord.api.CommandsAPI;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.MessageEmbedBuilder;
import com.aliucord.plugins.FriendNicknames;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class SetCommand {
   public static CommandsAPI.CommandResult execute(Map<String, ?> args, SettingsAPI sets, FriendNicknames main) {
       var user = (String) args.get("user");
       var nickname = (String) args.get("nickname");
       if(user == null || user.equals("") || nickname == null || nickname.equals("")){
           return new CommandsAPI.CommandResult("Missing arguments", null, false);
       }

       sets.setString(user, nickname);

       return new CommandsAPI.CommandResult("Set nickname", null, false);
   }
}
