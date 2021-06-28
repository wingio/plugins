package com.aliucord.plugins.tags;

import com.aliucord.api.CommandsAPI;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.MessageEmbedBuilder;
import com.aliucord.plugins.FriendNicknames;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ClearCommand {
   public static CommandsAPI.CommandResult execute(Map<String, ?> args, SettingsAPI sets, FriendNicknames main) {
       var user = (String) args.get("user");
       if(user == null || user.equals("")){
           return new CommandsAPI.CommandResult("Missing arguments", null, false)
       }

       sets.setString(user, null);

       return new CommandsAPI.CommandResult("Cleared nickname", null, false)
   }
}
