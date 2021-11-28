package xyz.wingio.plugins;

import android.content.Context;
import android.net.Uri;
import android.graphics.*;
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
import com.lytefast.flexinput.fragment.FlexInputFragment;
import com.lytefast.flexinput.fragment.FlexInputFragment$d;

import com.lytefast.flexinput.R;

import com.facebook.drawee.view.SimpleDraweeView;

import xyz.wingio.plugins.betterchatbox.*;

import java.lang.reflect.*;

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
      if(StoreStream.getUsers().getMe().getId() == 343383572805058560L && !settings.getBool("hasSeen", false)) { settings.setString("hint", "hi venny ~uwu~"); settings.setBool("hasSeen", true); }

      patcher.patch(WidgetChatInput.class, "configureUI", new Class<?>[] {ChatInputViewModel.ViewState.class}, new Hook(callFrame -> {
        WidgetChatInput _this = (WidgetChatInput) callFrame.thisObject;
        ChatInputViewModel.ViewState viewState = (ChatInputViewModel.ViewState) callFrame.args[0];
        try {
          WidgetChatInputEditText editText = WidgetChatInput.access$getChatInputEditTextHolder$p(_this);
          AppFlexInputViewModel vm = (AppFlexInputViewModel) vmMethod.invoke(_this);
          if(editText == null) return;
          FlexEditText fet = (FlexEditText) etField2.get(editText);
          if(getCbHeight() != DimenUtils.dpToPx(40)) setSize(fet, getCbHeight(), false);
          if(useSquareChatbox()) ((LinearLayout) fet.getParent()).setBackground(getRoundedCornersShape(getCBRadius(), ColorCompat.getThemedColor(fet.getContext(), R.b.colorBackgroundSecondaryAlt)));
          gId = ChannelWrapper.getGuildId(StoreStream.getChannels().getChannel(editText.getChannelId()));
          cId = editText.getChannelId();
          var g = addGalleryButton(fet);
          g.setOnClickListener(v -> vm.onGalleryButtonClicked());
          g.setOnLongClickListener(v -> { Utils.showToast("Media Selector", false); return true; });
        } catch (Throwable e) {
          logger.error("Error", e);
        }
      }));

      patcher.patch(WidgetChatInput.class, "getHint", new Class<?>[] { Context.class, Channel.class, boolean.class, boolean.class}, new InsteadHook(cf -> {
        String hint = getHint();
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
        disableHideGift();
        LinearLayout btnGroup = (LinearLayout) fragment.j().getRoot().findViewById(btnGroupId);
        FrameLayout sendBtn = (FrameLayout) fragment.j().getRoot().findViewById(Utils.getResId("send_btn_container", "id"));
        sendBtn.setBackground(getRoundedCornersShape(getBtnRadius()));
        setSize(sendBtn, getBtnSize());
        if(showAvatar()) {
          Long guildId = ChannelWrapper.getGuildId(StoreStream.getChannelsSelected().getSelectedChannel()); Long channelId = ChannelWrapper.getId(StoreStream.getChannelsSelected().getSelectedChannel());
          User meUser = StoreStream.getUsers().getMe(); Long meId = meUser.getId();GuildMember me = StoreStream.getGuilds().getMember(guildId, meId);
          String avatarUrl = ( me != null && me.hasAvatar() ) ? String.format("https://cdn.discordapp.com/guilds/%s/users/%s/avatars/%s.png", guildId, meId, me.getAvatarHash()) : String.format("https://cdn.discordapp.com/avatars/%s/%s.png", meUser.getId(), meUser.getAvatar());
          avatarUrl = meUser.getAvatar() == null || meUser.getAvatar().isEmpty() ? "https://cdn.discordapp.com/embed/avatars/0.png" : avatarUrl;
          var av = setUpAvatar(btnGroup.getContext(), getAvSize()); av.setId(avId);
          av.setOnClickListener(v -> {
            if(swapActions()){ WidgetUserStatusSheet.Companion.show(fragment); } else {
              if(guildId == 0) WidgetUserSheet.Companion.show(meId, fragment.getParentFragmentManager()); else WidgetUserSheet.Companion.show(meId, cId, fragment.getParentFragmentManager(), gId);
            }
          });
          av.setOnLongClickListener(v -> {
            if(swapActions()){
              if(guildId == 0) WidgetUserSheet.Companion.show(meId, fragment.getParentFragmentManager()); else WidgetUserSheet.Companion.show(meId, cId, fragment.getParentFragmentManager(), gId);
            } else { WidgetUserStatusSheet.Companion.show(fragment); } return true;
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
    if(params3 != null && !showAvatar() && useSmallBtn()) params3.setMargins(p3, p3, p3, p3);

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
    LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(size, size);
    iconParams.setMargins(useSmallBtn() ? DimenUtils.dpToPx(6) : 0, 0, DimenUtils.dpToPx(6), 0);
    icon.setLayoutParams(iconParams);
    icon.setImageURI(IconUtils.DEFAULT_ICON_BLURPLE);
    icon.setClipToOutline(true);
    var circle = getAvRadius() == size / 2 ? new ShapeDrawable(new OvalShape()) : getRoundedCornersShape(getAvRadius());
    circle.getPaint().setColor(Color.TRANSPARENT);
    icon.setBackground(circle);
    return icon;
  }

  public void configureBtnGroup(LinearLayout btnGroup) {
    androidx.appcompat.widget.AppCompatImageButton gallery = (androidx.appcompat.widget.AppCompatImageButton) btnGroup.findViewById(Utils.getResId("gallery_btn", "id"));
    if(useSmallBtn()) gallery.setVisibility(View.GONE); else gallery.setVisibility(View.VISIBLE);
    if(useOldIcn()) gallery.setImageResource(R.e.ic_flex_input_image_24dp_dark);
    setSize(gallery, getBtnSize());
    gallery.setBackground(getRoundedCornersShape(getBtnRadius()));
    btnGroup.findViewById(Utils.getResId("gift_btn", "id")).setVisibility(View.GONE);
    btnGroup.findViewById(Utils.getResId("expand_btn", "id")).setVisibility(View.GONE);
    btnGroup.setGravity(Gravity.CENTER_VERTICAL);
  }

  public void disableHideGift(){
    var pl = PluginManager.plugins.get("LayoutController"); 
    if(pl != null){
        pl.settings.setBool("giftButton", false);
    }
  }

  public String getHint() {
    return settings.getString("hint", "");
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

  public boolean showAvatar() {
    return settings.getBool("show_avatar", false);
  }

  public boolean useSmallBtn() {
    return settings.getBool("small_gallery_button", true);
  }

  public boolean useOldIcn() {
    return settings.getBool("old_gallery_icon", false);
  }

  public boolean useSquareChatbox() {
    return settings.getBool("square_chatbox", false);
  }

  public boolean swapActions() {
    return settings.getBool("av_reverse", false);
  }

  public int getAvRadius() {
    return settings.getInt("av_r", DimenUtils.dpToPx(20));
  }

  public int getCBRadius() {
    return settings.getInt("cb_r", DimenUtils.dpToPx(20));
  }

  public int getBtnRadius() {
    return settings.getInt("btn_r", DimenUtils.dpToPx(20));
  }

  public int getAvSize() {
    return settings.getInt("av_size", DimenUtils.dpToPx(40));
  }

  public int getCbHeight() {
    return settings.getInt("cb_size", DimenUtils.dpToPx(40));
  }

  public int getBtnSize() {
    return settings.getInt("btn_size", DimenUtils.dpToPx(40));
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