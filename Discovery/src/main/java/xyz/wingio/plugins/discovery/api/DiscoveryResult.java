package xyz.wingio.plugins.discovery.api;

import java.util.List;

public class DiscoveryResult {
    public int total;
    public List<DiscoveryGuild> guilds;
    public int offset;
    public int limit;
}