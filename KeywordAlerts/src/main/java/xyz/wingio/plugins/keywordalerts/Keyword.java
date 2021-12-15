package xyz.wingio.plugins.keywordalerts;

import com.discord.utilities.SnowflakeUtils;
import com.discord.utilities.time.*;

import java.util.regex.*;
import java.util.*;

public class Keyword {
    public static final int CURRENT_VERSION = 3;
    private int version = CURRENT_VERSION;
    private String word;
    private boolean isRegex;
    private boolean isEnabled = true;
    private boolean whitelistEnabled = false;
    private List<Long> whitelist = new ArrayList<>();
    private List<Long> blacklist = new ArrayList<>();
    private Long id = SnowflakeUtils.fromTimestamp(TimeUtils.parseUTCDate(TimeUtils.currentTimeUTCDateString(ClockFactory.get())));

    public Keyword(String word, boolean isRegex) {
        this.word = word;
        this.isRegex = isRegex;
    }

    public Keyword(Keyword keyword) {
        this.word = keyword.getWord();
        this.isRegex = keyword.isRegex();
        this.isEnabled = keyword.isEnabled();
        this.whitelistEnabled = keyword.whitelistEnabled();
        this.whitelist = keyword.getWhitelist() != null ? keyword.getWhitelist() : new ArrayList<>();
        this.id = keyword.getId();
        this.blacklist = keyword.getBlacklist() != null ? keyword.getBlacklist() : new ArrayList<>();
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Long getId() {
        return id;
    }

    public boolean isRegex() {
        return isRegex;
    }
    
    public void setRegex(boolean regex) {
        isRegex = regex;
    }

    public boolean whitelistEnabled() {
        return whitelistEnabled;
    }
    
    public void setWhitelistEnabled(boolean enabled) {
        whitelistEnabled = enabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isWhitelisted(Long id) {
        return whitelist.contains(id);
    }

    public void addToWhitelist(Long id) {
        whitelist.add(id);
    }

    public void removeFromWhitelist(Long id) {
        int pos = whitelist.indexOf(id);
        if (pos != -1) {
            whitelist.remove(pos);
        }
    }

    public List<Long> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<Long> whitelist) {
        this.whitelist = whitelist;
    }

    public boolean isBlacklisted(Long id) {
        return blacklist.contains(id);
    }

    public void addToBlacklist(Long id) {
        blacklist.add(id);
    }

    public void removeFromBlacklist(Long id) {
        int pos = blacklist.indexOf(id);
        if (pos != -1) {
            blacklist.remove(pos);
        }
    }

    public List<Long> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<Long> blacklist) {
        this.blacklist = blacklist;
    }

    public int getVersion() {
        return version;
    }

    public boolean matches(String text) {
        if (isEnabled == false) return false;

        if (isRegex) {
            Pattern pattern = Pattern.compile(word, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            return matcher.find();
        } else {
            return text.contains(word);
        }
    }
}