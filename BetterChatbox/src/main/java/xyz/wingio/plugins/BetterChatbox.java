package xyz.wingio.plugins;

import android.content.Context;
import android.net.Uri;
import android.graphics.*;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.*;
import android.text.*;
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
import com.discord.api.channel.ChannelUtils;
import com.discord.api.permission.Permission;
import com.discord.models.user.User;
import com.discord.models.member.GuildMember;
import com.discord.stores.*;
import com.discord.utilities.icon.IconUtils;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.user.UserUtils;
import com.discord.widgets.chat.input.*;
import com.discord.widgets.user.usersheet.WidgetUserSheet;
import com.discord.widgets.user.*;

import com.lytefast.flexinput.widget.FlexEditText;
import com.lytefast.flexinput.model.Attachment;
import com.lytefast.flexinput.viewmodel.FlexInputState;
import com.lytefast.flexinput.fragment.FlexInputFragment;
import com.lytefast.flexinput.fragment.FlexInputFragment$d;

import com.lytefast.flexinput.R;

import com.facebook.drawee.view.SimpleDraweeView;

import xyz.wingio.plugins.betterchatbox.*;

import java.lang.reflect.*;
import java.util.*;

@SuppressWarnings("unused")
@AliucordPlugin(requiresRestart = true)
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

    //For other devs to use in their plugins
    public FlexEditText fet;
    public AppFlexInputViewModel vm;
    public FlexInputFragment fragment;
    public LinearLayout btnGroup;
    public SimpleDraweeView av;
    public String avatarUrl;
    public long gId = 0L;
    public long cId = 0L;

    @Override
    public void start(Context context) throws Throwable {
      Util.API.getOnPressActions();
      Util.API.getOnLongPressActions();
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
          WidgetChatInputEditText editText = WidgetChatInput.access$getChatInputEditTextHolder$p(_this);
          vm = (AppFlexInputViewModel) vmMethod.invoke(_this);
          Util.API.setVm(vm);
          if(editText == null) return;
          fet = (FlexEditText) etField2.get(editText);
          LinearLayout fetCont = (LinearLayout) fet.getParent();
          if(Settings.getCbHeight() != DimenUtils.dpToPx(40)) setSize(fet, Settings.getCbHeight(), false);
          if(Settings.useSquareChatbox()) fetCont.setBackground(getRoundedCornersShape(Settings.getCBRadius(), ColorCompat.getThemedColor(fet.getContext(), R.b.colorBackgroundSecondaryAlt)));
          gId = ChannelWrapper.getGuildId(StoreStream.getChannels().getChannel(editText.getChannelId()));  cId = editText.getChannelId(); Util.API.setGId(gId); Util.API.setCId(cId);
          RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fetCont.getLayoutParams();
          if(lp != null && Settings.showSend()) { lp.setMarginEnd(0); fetCont.setLayoutParams(lp); }
          if(Settings.getAvLongClick() != 3 && Settings.getAvOnClick() != 3) {
            var g = addGalleryButton(fet);
            g.setOnClickListener(v -> vm.onGalleryButtonClicked());
            g.setOnLongClickListener(v -> { Utils.showToast("Media Selector", false); return true; });
          }
          configureAvatar();
        } catch (Throwable e) {
          logger.error("Error", e);
        }
      }));

      patcher.patch(AppFlexInputViewModel.class.getDeclaredMethod("onInputTextChanged", String.class, Boolean.class), new Hook(callFrame -> {
        if(!Settings.showSend()) return;
        LinearLayout fetCont = (LinearLayout) fet.getParent();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fetCont.getLayoutParams();
        if(lp != null) { lp.setMarginEnd(0); fetCont.setLayoutParams(lp); }
      }));

      patcher.patch(WidgetChatInput.class, "getHint", new Class<?>[] { Context.class, Channel.class, boolean.class, boolean.class}, new InsteadHook(cf -> {
        String hint = Settings.getHint();
        ChannelWrapper cw = new ChannelWrapper((Channel) cf.args[1]);
        String target = ChannelUtils.e(cw.raw(), (Context) cf.args[0], false, 2);
        var g = StoreStream.getGuilds().getGuilds().get(cw.getGuildId());
        hint = hint.replaceAll("%u", StoreStream.getUsers().getMe().getUsername()); //username
        hint = hint.replaceAll("%tag", StoreStream.getUsers().getMe().getUsername() + UserUtils.INSTANCE.getDiscriminatorWithPadding(StoreStream.getUsers().getMe())); //tag
        hint = hint.replaceAll("%t", target); //target
        hint = hint.replaceAll("%n", (target.startsWith("#") || target.startsWith("@")) ? target.substring(1) : target); //name
        hint = hint.replaceAll("%id", cw.getId() + ""); //id
        hint = hint.replaceAll("%s", g == null ? target : g.getName()); //current server
        if(hint.isEmpty()) return getOriginalHint((Context) cf.args[0], cw.raw(), (boolean) cf.args[2], (boolean) cf.args[3]);
        return hint;
      }));

      patcher.patch(FlexInputFragment$d.class, "invoke", new Class<?>[] {Object.class}, new Hook(callFrame -> {
        FlexInputFragment fragment = (FlexInputFragment) ((FlexInputFragment$d) callFrame.thisObject).receiver;
        this.fragment = fragment;
        disableHideGift();
        btnGroup = (LinearLayout) fragment.j().getRoot().findViewById(btnGroupId);
        FrameLayout sendBtn = (FrameLayout) fragment.j().getRoot().findViewById(Utils.getResId("send_btn_container", "id"));
        sendBtn.setBackground(getRoundedCornersShape(Settings.getBtnRadius()));
        if(Settings.showSend()) sendBtn.setVisibility(View.VISIBLE);
        setSize(sendBtn, Settings.getBtnSize());
        if(Settings.showAvatar()) {
          Long guildId = ChannelWrapper.getGuildId(StoreStream.getChannelsSelected().getSelectedChannel()); Long channelId = ChannelWrapper.getId(StoreStream.getChannelsSelected().getSelectedChannel());
          User meUser = StoreStream.getUsers().getMe(); Long meId = meUser.getId();GuildMember me = StoreStream.getGuilds().getMember(guildId, meId);
          int discim = meUser.getDiscriminator() % 5;
          avatarUrl = ( me != null && me.hasAvatar() ) ? String.format("https://cdn.discordapp.com/guilds/%s/users/%s/avatars/%s.png", guildId, meId, me.getAvatarHash()) : String.format("https://cdn.discordapp.com/avatars/%s/%s.png", meUser.getId(), meUser.getAvatar());
          avatarUrl = meUser.getAvatar() == null || meUser.getAvatar().isEmpty() ? "https://cdn.discordapp.com/embed/avatars/" + discim + ".png" : avatarUrl;
          av = setUpAvatar(btnGroup.getContext(), Settings.getAvSize()); av.setId(avId);
          av.setOnClickListener(v -> Util.API.onPressAction(v, guildId, meId, fragment));
          av.setOnLongClickListener(v -> Util.API.onLongPressAction(v, guildId, meId, fragment));
          configureBtnGroup(btnGroup);
          configureAvatar();
        } else {
          if(Settings.useSmallBtn()) btnGroup.setVisibility(View.GONE); else btnGroup.setVisibility(View.VISIBLE);
        }
        configureBtnGroup(btnGroup);
      }));
  }

  public ToolbarButton addGalleryButton(FlexEditText et) throws Throwable {
    LinearLayout group = (LinearLayout) fet.getParent();
    ToolbarButton mediaPicker = new ToolbarButton(group.getContext());
    mediaPicker.setImageResource(Settings.useOldIcn() ? R.e.ic_flex_input_image_24dp_dark : R.e.ic_add_circle);
    mediaPicker.setPadding(p2, p2, p2, p2);
    mediaPicker.setId(btnId);

    if(group.findViewById(btnId) == null && Settings.useSmallBtn()) addToChatbox(mediaPicker, true);
    return mediaPicker;
  }

  public SimpleDraweeView setUpAvatar(Context ctx, int size) {
    SimpleDraweeView icon = new SimpleDraweeView(ctx);
    LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(size, size);
    iconParams.setMargins((Settings.useSmallBtn() || Settings.getAvOnClick() == 3 || Settings.getAvLongClick() == 3) ? DimenUtils.dpToPx(6) : 0, 0, DimenUtils.dpToPx(6), 0);
    icon.setLayoutParams(iconParams);
    icon.setImageURI(IconUtils.DEFAULT_ICON_BLURPLE);
    icon.setClipToOutline(true);
    var circle = Settings.getAvRadius() == size / 2 ? new ShapeDrawable(new OvalShape()) : getRoundedCornersShape(Settings.getAvRadius());
    circle.getPaint().setColor(Color.TRANSPARENT);
    icon.setBackground(circle);
    return icon;
  }

  public View addToChatbox(View view, boolean isBtn) {
    LinearLayout group = (LinearLayout) fet.getParent();
    group.setGravity(Gravity.CENTER_VERTICAL);
    Context context = fet.getContext();

    RelativeLayout.LayoutParams groupParams = (RelativeLayout.LayoutParams) group.getLayoutParams();
    if(groupParams != null && Settings.shouldChangeMargin()) groupParams.setMargins(p3, p3, p3, p3);
    
    FrameLayout itemContainer = new FrameLayout(context);
    LinearLayout.LayoutParams itemContainerParams = new LinearLayout.LayoutParams(p, p);
    itemContainerParams.setMargins(-1 * DimenUtils.dpToPx(8),0,0,0);
    itemContainer.setLayoutParams(itemContainerParams);
    int padding = DimenUtils.dpToPx(9);
    if(!isBtn) itemContainer.setPadding(padding, padding, padding, padding);
    int size = isBtn ? p : p - (2 * padding);
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
    view.setLayoutParams(params);
    itemContainer.addView(view);

    group.addView(itemContainer, 0);
    return view;
  }

  public void configureBtnGroup(LinearLayout btnGroup) {
    androidx.appcompat.widget.AppCompatImageButton gallery = (androidx.appcompat.widget.AppCompatImageButton) btnGroup.findViewById(Utils.getResId("gallery_btn", "id"));
    if(Settings.useSmallBtn() || Settings.getAvOnClick() == 3 || Settings.getAvLongClick() == 3) gallery.setVisibility(View.GONE); else gallery.setVisibility(View.VISIBLE);
    if(Settings.useOldIcn()) gallery.setImageResource(R.e.ic_flex_input_image_24dp_dark);
    setSize(gallery, Settings.getBtnSize());
    gallery.setBackground(getRoundedCornersShape(Settings.getBtnRadius()));
    btnGroup.findViewById(Utils.getResId("gift_btn", "id")).setVisibility(View.GONE);
    btnGroup.findViewById(Utils.getResId("expand_btn", "id")).setVisibility(View.GONE);
    btnGroup.setGravity(Gravity.CENTER_VERTICAL);
    if(Settings.shouldChangeMargin()) btnGroup.setVisibility(View.GONE); else btnGroup.setVisibility(View.VISIBLE);
  }

  public void configureAvatar() {
    switch (Settings.getAvDisplay()) {
      case Settings.AVATAR_DISPLAY_NORMAL:
        if(btnGroup.findViewById(avId) != null) ((SimpleDraweeView) btnGroup.findViewById(avId)).setImageURI(avatarUrl); else btnGroup.addView(av);
        break;
      case Settings.AVATAR_DISPLAY_INLINE:
        if(fet != null) if(((LinearLayout) fet.getParent()).findViewById(avId) != null) ((SimpleDraweeView) ((LinearLayout) fet.getParent()).findViewById(avId)).setImageURI(avatarUrl); else addToChatbox(av, false);
        break;
    }
  }

  public void disableHideGift(){
    var pl = PluginManager.plugins.get("LayoutController"); 
    if(pl != null){
        pl.settings.setBool("giftButton", false);
    }
  }

  public String getOriginalHint(Context context, Channel channel, boolean z2, boolean z3) {
    if (z2) {
        String string = context.getString(R.h.dm_verification_text_blocked);
        return string;
    } else if (z3) {
        return context.getString(R.h.dm_textarea_placeholder).replace("!!{channel}!!", ChannelUtils.e(channel, context, false, 2));
    } else {
        String string2 = context.getString(R.h.no_send_messages_permission_placeholder);
        return string2;
    }
  }

  public void setSize(View view, int size) {
    setSize(view, size, true);
  }

  public void setSize(View view, int size, boolean square) {
    ViewGroup.LayoutParams params = view.getLayoutParams();
    if(params != null) {
      if(square) { params.width = size; }
      params.height = size;
      view.setLayoutParams(params);
    }
  }

  public ShapeDrawable getRoundedCornersShape(int cornerRadius){
    float[] radii = new float[] {cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
    return new ShapeDrawable(new RoundRectShape(radii, null, null));
  }

  public ShapeDrawable getRoundedCornersShape(int cornerRadius, int color){
    float[] radii = new float[] {cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
    ShapeDrawable rounded = new ShapeDrawable(new RoundRectShape(radii, null, null));
    rounded.getPaint().setColor(color);
    return rounded;
  }

  @Override
  public void stop(Context context) {
      patcher.unpatchAll();
      commands.unregisterAll();
  }
}