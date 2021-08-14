package com.aliucord.plugins.favoritemessages;

import com.discord.models.message.Message;
import com.discord.models.user.CoreUser;
import com.discord.api.user.User;
import java.utils.*;

public static class StoredMessage {
    public String content;
    public Author author;
    
    public class Author {
        public String name;
        public Long id;
        public String avatar;
        public boolean isBot;

        public Author(CoreUser user){
            this.name = user.getUsername();
            this.id = user.getId();
            this.avatar = user.getAvatar();
            this.isBot = user.isBot();
        }
    }

    public static StoredMessage(Message message) {
        this.content = message.getContent();
        User a = Message.getAuthor();
        this.author = new Author(new CoreUser(a));
    }

}