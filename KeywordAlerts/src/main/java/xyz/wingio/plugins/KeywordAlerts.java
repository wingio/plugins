package xyz.wingio.plugins;

import android.content.Context;
import android.net.Uri;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.*;
import android.text.Editable;
import android.view.*;
import android.widget.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;

import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.*;
import com.aliucord.entities.NotificationData;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.*;
import com.aliucord.utils.*;
import com.aliucord.wrappers.*;
import com.aliucord.views.ToolbarButton;

import xyz.wingio.plugins.keywordalerts.*;

import com.discord.models.message.*;
import com.discord.models.user.*;
import com.discord.stores.*;

import com.google.gson.reflect.TypeToken;

import com.lytefast.flexinput.R;

import java.lang.reflect.*;
import java.util.*;

import kotlin.Unit;

@SuppressWarnings("unused")
@AliucordPlugin
public class KeywordAlerts extends Plugin {

  public KeywordAlerts(){
    settingsTab = new SettingsTab(PluginSettings.class).withArgs(this);
  }

  public static Logger logger = new Logger("KeywordAlerts");
  public static final Type keywordsType = TypeToken.getParameterized(HashMap.class, Long.class, Keyword.class).getType();
  private Drawable pluginIcon;

  @Override
  public void start(Context context) throws Throwable {
    convertToNewFormat();
    pluginIcon = ContextCompat.getDrawable(context, R.e.ic_sidebar_notifications_on_dark_24dp);
    RxUtils.subscribe(RxUtils.onBackpressureBuffer(StoreStream.getGatewaySocket().getMessageCreate()), RxUtils.createActionSubscriber(message -> {
			if (message == null) return;
			Message modelMessage = new Message(message);
      MeUser currentUser = StoreStream.getUsers().getMe();
			CoreUser author = new CoreUser(modelMessage.getAuthor());
			if (modelMessage.getEditedTimestamp() == null) {
        String content = modelMessage.getContent();
				for(Keyword keyword : getKeywordsList()){
          if(keyword.isEnabled() && keyword.matches(content)) {
            if(keyword.whitelistEnabled()){
              if(keyword.isWhitelisted(modelMessage.getChannelId())) showNotification(keyword, modelMessage);
            } else { showNotification(keyword, modelMessage); }
          }
        }
			}
		}));
  }

  public void convertToNewFormat() {
    for (Keyword keyword : getKeywordsList()) {
      if (keyword.getWhitelist() == null) {
        var kws = getKeywords();
        kws.put(keyword.getId(), new Keyword(keyword));
        settings.setObject("keywords", kws);
      }
    }
  }

  public List<Keyword> getKeywordsList() {
    return new ArrayList<>(getKeywords().values());
  }

  public Map<Long, Keyword> getKeywords() {
    Map<Long, Keyword> keywords = new HashMap<>();
    keywords.put(1L, new Keyword("trign\\b", true));
    // return keywords;
    return settings.getObject("keywords", new HashMap<>(), keywordsType);
  }

  private void showNotification(Keyword keyword, Message message) {
    CoreUser author = new CoreUser(message.getAuthor());
    String location = "";
    String icon = "";
    boolean isDm = false;
    ChannelWrapper channel = new ChannelWrapper(StoreStream.getChannels().getChannel(message.getChannelId()));
    if(channel.raw() != null) {
      var guild = StoreStream.getGuilds().getGuild(channel.getGuildId());
      if(channel.isGuild() && guild != null) {
        location += guild.getName();
        icon = String.format("https://cdn.discordapp.com/icons/%s/%s.png", guild.getId(), guild.getIcon());
      } else {
        CoreUser recipient = channel.raw().w().get(0) == null ? author : new CoreUser(channel.raw().w().get(0));
        icon = String.format("https://cdn.discordapp.com/avatars/%s/%s.png", recipient.getId(), recipient.getAvatar());
      }
      location += channel.isDM() ? /* Recipient */ new CoreUser(channel.raw().w().get(0)).getUsername() : " #" + channel.getName();
      isDm = channel.isDM();
    }
    NotificationData notD = new NotificationData();
      notD.setTitle("Keyword in " + location);
      notD.setBody(MDUtils.render(isDm ? message.getContent() : "**" + author.getUsername() + ": ** " + message.getContent() ) );
      notD.setAutoDismissPeriodSecs(5);
      notD.setIconUrl(icon);
      notD.setOnClick(v -> {
          StoreStream.Companion.getMessagesLoader().jumpToMessage(message.getChannelId(), message.getId());
          return Unit.a;
      });
      notD.setOnClickTopRightIcon(v -> {
        Utils.openPageWithProxy(v.getContext(), new PluginSettings(this));
        return Unit.a;
      });
      NotificationsAPI.display(notD);
  }

  @Override
  public void stop(Context context) {
      patcher.unpatchAll();
      commands.unregisterAll();
  }
}