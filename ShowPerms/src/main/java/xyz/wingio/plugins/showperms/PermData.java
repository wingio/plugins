package xyz.wingio.plugins.showperms;

import com.discord.api.role.GuildRole;

public class PermData {
    public String name;
    public GuildRole role;

    public PermData(String name, GuildRole role) {
      this.name = name;
      this.role = role;
    }

    public PermData(String name) {
      this.name = name;
    }

    public PermData setRole(GuildRole role){
      this.role = role;
      return this;
    }

    public PermData setName(String newName){
      this.name = newName;
      return this;
    }
    
  }