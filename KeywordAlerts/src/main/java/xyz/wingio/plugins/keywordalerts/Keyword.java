package xyz.wingio.plugins.keywordalerts;

import com.discord.utilities.SnowflakeUtils;
import com.discord.utilities.time.*;

import java.util.regex.*;

public class Keyword {
    private String word;
    private boolean isRegex;
    private boolean isEnabled = true;
    private Long id = SnowflakeUtils.fromTimestamp(TimeUtils.parseUTCDate(TimeUtils.currentTimeUTCDateString(ClockFactory.get())));

    public Keyword(String word, boolean isRegex) {
        this.word = word;
        this.isRegex = isRegex;
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

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
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