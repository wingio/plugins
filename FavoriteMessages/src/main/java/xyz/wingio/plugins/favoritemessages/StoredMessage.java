package xyz.wingio.plugins.favoritemessages;

import com.discord.models.message.Message;
import com.discord.models.user.CoreUser;
import com.discord.api.user.User;
import com.discord.stores.StoreStream;
import com.aliucord.wrappers.ChannelWrapper;
import java.util.*;

public class StoredMessage {
    public String content;
    public Author author;
    public Long id;
    public String channelName;
    public Long channelId;
    public String guildName;
    public String guildId;
    
    public String getUrl() {
        return String.format("https://discord.com/channels/%s/%s/%s", guildId, channelId, id);
    }

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

    public  StoredMessage(Message message) {
        content = message.getContent();
        id = message.getId();
        author = new Author(new CoreUser(message.getAuthor()));
        channelId = message.getChannelId();
        var channel = StoreStream.getChannels().getChannel(message.getChannelId());
        var guildIdStr = channel != null && ChannelWrapper.getGuildId(channel) != 0 ? String.valueOf(ChannelWrapper.getGuildId(channel)) : "@me";
        channelName = channel != null ? ChannelWrapper.getName(channel) : "Deleted Channel";
        var guild = channel != null && ChannelWrapper.getGuildId(channel) != 0 ? StoreStream.getGuilds().getGuilds().get(ChannelWrapper.getGuildId(channel)) : null;
        guildName = guild != null ? guild.getName() : "Direct Messages";
        guildId = guildIdStr;
    }

}