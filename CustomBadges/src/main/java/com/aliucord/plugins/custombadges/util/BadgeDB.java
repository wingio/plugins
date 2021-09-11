package com.aliucord.plugins.custombadges.util;

import com.aliucord.Http;
import com.aliucord.Utils;
import com.aliucord.Logger;
import java.util.*;

public class BadgeDB {
    public final Map<Long, List<APIBadge>> userBadges = new HashMap<>();
    private Logger logger = new Logger("BadgeDB");
    public class APIBadge {
        public String icon;
        public String toast;
    }

    class APIResponse {
        public List<APIBadge> badges;
    }

    public void requestBadgesForUser(Long userId) {
        String url = "https://raw.githubusercontent.com/wingio/BadgeDB/main/users/" + userId + ".json";
        Utils.threadPool.execute(() -> {
            try {
                APIResponse res = (APIResponse) Http.simpleJsonGet(url, APIResponse.class);
                userBadges.put(userId, res.badges);
            } catch (Exception e) {
                logger.error("Error while requesting badges for user: " + userId, e);
            }
        });
    }

    public List<APIBadge> getBadgesForUser(Long userId) {
        if(!userBadges.containsKey(userId)) {
            requestBadgesForUser(userId);
        }
        if(userBadges.containsKey(userId)) {
            return userBadges.get(userId);
        }
        return new ArrayList<>();
    }

    public void clearCache() {
        userBadges.clear();
    }

    public BadgeDB() { }
}