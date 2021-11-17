package xyz.wingio.plugins;

import android.content.Context;
import android.net.Uri;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.*;
import android.text.Editable;
import android.view.*;
import android.widget.*;

import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.*;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.*;
import com.aliucord.utils.*;
import com.aliucord.wrappers.*;
import com.aliucord.views.ToolbarButton;

import com.discord.api.channel.Channel;
import com.discord.api.channel.Channel;
import com.discord.api.permission.Permission;
import com.discord.models.user.User;
import com.discord.models.member.GuildMember;
import com.discord.stores.*;
import com.discord.utilities.icon.IconUtils;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.widgets.chat.input.*;
import com.discord.widgets.user.usersheet.WidgetUserSheet;
import com.discord.widgets.user.*;

import com.lytefast.flexinput.widget.FlexEditText;
import com.lytefast.flexinput.model.Attachment;
import com.lytefast.flexinput.fragment.FlexInputFragment;
import com.lytefast.flexinput.fragment.FlexInputFragment$d;

import com.lytefast.flexinput.R;

import com.facebook.drawee.view.SimpleDraweeView;

import xyz.wingio.plugins.betterchatbox.*;

import java.lang.reflect.*;

@SuppressWarnings("unused")
@AliucordPlugin
public class BetterChatbox extends Plugin {

    public BetterChatbox(){
      settingsTab = new SettingsTab(PluginSettings.class).withArgs(this);
    }

    public static Logger logger = new Logger("BetterChatbox");

    public final int p = DimenUtils.dpToPx(40);
    public final int p2 = DimenUtils.dpToPx(10);
    public final int p3 = DimenUtils.dpToPx(8);

    public final int btnId = View.generateViewId();
    public final int avId = View.generateViewId();
    public final int btnGroupId = Utils.getResId("left_btns_container", "id");

    public long gId = 0L;
    public long cId = 0L;

    @Override
    public void start(Context context) throws Throwable {
      Method vmMethod = WidgetChatInput.class.getDeclaredMethod("getFlexInputViewModel");
      vmMethod.setAccessible(true);
      Field etField = WidgetChatInput.class.getDeclaredField("chatInputEditTextHolder");
      etField.setAccessible(true);
      Field etField2 = WidgetChatInputEditText.class.getDeclaredField("editText");
      etField2.setAccessible(true);

      patcher.patch(WidgetChatInput.class, "configureUI", new Class<?>[] {ChatInputViewModel.ViewState.class}, new Hook(callFrame -> {
        WidgetChatInput _this = (WidgetChatInput) callFrame.thisObject;
        ChatInputViewModel.ViewState viewState = (ChatInputViewModel.ViewState) callFrame.args[0];
        try {
          WidgetChatInputEditText editText = (WidgetChatInputEditText) etField.get(_this);
          AppFlexInputViewModel vm = (AppFlexInputViewModel) vmMethod.invoke(_this);
          if(editText == null) return;
          gId = ChannelWrapper.getGuildId(StoreStream.getChannels().getChannel(editText.getChannelId()));
          cId = editText.getChannelId();
          var g = addGalleryButton((FlexEditText) etField2.get(editText));
          g.setOnClickListener(v -> vm.onGalleryButtonClicked());
          g.setOnLongClickListener(v -> { Utils.showToast("Media Selector", false); return true; });
        } catch (Throwable e) {
          logger.error("Error", e);
        }
      }));

      patcher.patch(FlexInputFragment$d.class, "invoke", new Class<?>[] {Object.class}, new Hook(callFrame -> {
        FlexInputFragment fragment = (FlexInputFragment) ((FlexInputFragment$d) callFrame.thisObject).receiver;
        disableHideGift();
        LinearLayout btnGroup = (LinearLayout) fragment.j().getRoot().findViewById(btnGroupId);
        if(showAvatar()) {
          Long guildId = ChannelWrapper.getGuildId(StoreStream.getChannelsSelected().getSelectedChannel()); Long channelId = ChannelWrapper.getId(StoreStream.getChannelsSelected().getSelectedChannel());
          User meUser = StoreStream.getUsers().getMe(); Long meId = meUser.getId();GuildMember me = StoreStream.getGuilds().getMember(guildId, meId);
          String avatarUrl = ( me != null && me.hasAvatar() ) ? String.format("https://cdn.discordapp.com/guilds/%s/users/%s/avatars/%s.png", guildId, meId, me.getAvatarHash()) : String.format("https://cdn.discordapp.com/avatars/%s/%s.png", meUser.getId(), meUser.getAvatar());
          avatarUrl = meUser.getAvatar() == null || meUser.getAvatar().isEmpty() ? "https://cdn.discordapp.com/embed/avatars/0.png" : avatarUrl;
          var av = setUpAvatar(btnGroup.getContext(), 40); av.setId(avId);
          av.setOnClickListener(v -> {
            if(guildId == 0) WidgetUserSheet.Companion.show(meId, fragment.getParentFragmentManager()); else WidgetUserSheet.Companion.show(meId, cId, fragment.getParentFragmentManager(), gId);
          });
          av.setOnLongClickListener(v -> {
            WidgetUserStatusSheet.Companion.show(fragment);
            return true;
          });
          if(btnGroup.findViewById(avId) != null) ((SimpleDraweeView) btnGroup.findViewById(avId)).setImageURI(avatarUrl); else btnGroup.addView(av);
          configureBtnGroup(btnGroup);
        } else {
          if(useSmallBtn()) btnGroup.setVisibility(View.GONE); else btnGroup.setVisibility(View.VISIBLE);
        }
        configureBtnGroup(btnGroup);
      }));
      
  }

  public ToolbarButton addGalleryButton(FlexEditText et) throws Throwable {
    LinearLayout group = (LinearLayout) et.getParent();
    group.setGravity(Gravity.CENTER_VERTICAL);
    Context context = et.getContext();
    FrameLayout mediaPickerContainer = new FrameLayout(context);

    RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) group.getLayoutParams();
    if(params3 != null && !showAvatar()) params3.setMargins(p3, p3, p3, p3);

    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(p, p);
    params2.setMargins(-1 * DimenUtils.dpToPx(8),0,0,0);
    mediaPickerContainer.setLayoutParams(params2);

    ToolbarButton mediaPicker = new ToolbarButton(context);
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(p, p);
    mediaPicker.setImageResource(useOldIcn() ? R.e.ic_flex_input_image_24dp_dark : R.e.ic_add_circle);
    mediaPicker.setPadding(p2, p2, p2, p2);
    mediaPicker.setLayoutParams(params);
    mediaPicker.setId(btnId);
    mediaPickerContainer.addView(mediaPicker);

    if(group.findViewById(btnId) == null && useSmallBtn()) group.addView(mediaPickerContainer, 0);
    return mediaPicker;
  }

  public SimpleDraweeView setUpAvatar(Context ctx, int size) {
    SimpleDraweeView icon = new SimpleDraweeView(ctx);
    LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(DimenUtils.dpToPx(size), DimenUtils.dpToPx(size));
    iconParams.setMargins(useSmallBtn() ? DimenUtils.dpToPx(6) : 0, 0, DimenUtils.dpToPx(6), 0);
    icon.setLayoutParams(iconParams);
    icon.setImageURI(IconUtils.DEFAULT_ICON_BLURPLE);
    icon.setClipToOutline(true);
    var circle = new ShapeDrawable(new OvalShape());
    var paint = circle.getPaint();
    paint.setColor(android.graphics.Color.TRANSPARENT);
    icon.setBackground(circle);
    return icon;
  }

  public void configureBtnGroup(LinearLayout btnGroup) {
    androidx.appcompat.widget.AppCompatImageButton gallery = (androidx.appcompat.widget.AppCompatImageButton) btnGroup.findViewById(Utils.getResId("gallery_btn", "id"));
    if(useSmallBtn()) gallery.setVisibility(View.GONE);
    if(!useSmallBtn()) gallery.setVisibility(View.VISIBLE);
    if(useOldIcn()) gallery.setImageResource(R.e.ic_flex_input_image_24dp_dark);
    btnGroup.findViewById(Utils.getResId("gift_btn", "id")).setVisibility(View.GONE);
    btnGroup.findViewById(Utils.getResId("expand_btn", "id")).setVisibility(View.GONE);
  }

  public void disableHideGift(){
    var pl = PluginManager.plugins.get("LayoutController"); 
    if(pl != null){
        pl.settings.setBool("giftButton", false);
    }
  }

  public boolean showAvatar() {
    return settings.getBool("show_avatar", false);
  }

  public boolean useSmallBtn() {
    return settings.getBool("small_gallery_button", true);
  }

  public boolean useOldIcn() {
    return settings.getBool("old_gallery_icon", false);
  }

  @Override
  public void stop(Context context) {
      patcher.unpatchAll();
      commands.unregisterAll();
  }
}