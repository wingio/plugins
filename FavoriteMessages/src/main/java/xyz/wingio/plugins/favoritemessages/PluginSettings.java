package xyz.wingio.plugins.favoritemessages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.content.res.Resources;
import android.content.res.AssetManager;
import android.net.Uri;
import android.text.*;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.view.*;
import android.widget.*;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import androidx.core.content.res.ResourcesCompat;

import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.Logger;
import com.aliucord.CollectionUtils;
import xyz.wingio.plugins.FavoriteMessages;
import xyz.wingio.plugins.favoritemessages.util.*;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.fragments.ConfirmDialog;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.utils.ReflectUtils;
import com.aliucord.views.TextInput;
import com.aliucord.views.ToolbarButton;
import com.aliucord.widgets.PluginCard;
import com.aliucord.widgets.BottomSheet;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.textprocessing.*;
import com.discord.utilities.time.ClockFactory;
import com.discord.utilities.time.TimeUtils;
import com.discord.utilities.SnowflakeUtils;
import com.discord.app.AppBottomSheet;
import com.discord.app.AppFragment;
import com.discord.widgets.user.usersheet.WidgetUserSheet;
import com.discord.widgets.changelog.WidgetChangeLog;
import com.discord.models.message.Message;
import com.discord.models.user.User;
import com.discord.api.role.GuildRole;
import com.discord.stores.*;
import com.discord.widgets.tabs.NavigationTab;
import com.facebook.drawee.span.DraweeSpanStringBuilder;
import com.lytefast.flexinput.R;

import kotlin.Unit;
import java.io.*;
import java.util.*;

public class PluginSettings extends SettingsPage {
    private static final int uniqueId = View.generateViewId();
    private final SettingsAPI settings;
    private static final int settingsId = View.generateViewId(); 
    public PluginSettings(SettingsAPI settings) {
        this.settings = settings;
    }

    public static class MessageOptions extends BottomSheet {
        private StoredMessage message;
        private AppFragment fragment;
        private SettingsPage page;
        private SettingsAPI sets = PluginManager.plugins.get("FavoriteMessages").settings;

        public MessageOptions(StoredMessage msg, AppFragment frag, SettingsPage page) {
            this.message = msg;
            this.fragment = frag;
            this.page = page;
        }

        private StoredMessage getMessage() {
            return message;
        }

        @Override
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            Context optCtx = requireContext();
            Drawable delIcon = ContextCompat.getDrawable(optCtx, R.e.ic_delete_24dp);
            delIcon.mutate();
            if (delIcon != null) delIcon.setTint(0xFFED4245);

            Drawable copyIcon = ContextCompat.getDrawable(optCtx, R.e.ic_copy_24dp);
            copyIcon.mutate();
            if (copyIcon != null) copyIcon.setTint(
                ColorCompat.getThemedColor(optCtx, R.b.colorInteractiveNormal)
            );

            Drawable jumpIcon = ContextCompat.getDrawable(optCtx, R.e.ic_reply_24dp);
            jumpIcon.mutate();
            if (jumpIcon != null) jumpIcon.setTint(
                ColorCompat.getThemedColor(optCtx, R.b.colorInteractiveNormal)
            );

            Drawable profileIcon = ContextCompat.getDrawable(optCtx, R.e.ic_profile_24dp);
            profileIcon.mutate();
            if (profileIcon != null) profileIcon.setTint(
                ColorCompat.getThemedColor(optCtx, R.b.colorInteractiveNormal)
            );
            
            var copyId = View.generateViewId();
            TextView copyOption = new TextView(optCtx, null, 0, R.i.UiKit_Settings_Item_Icon);
            copyOption.setText("Copy Text");
            copyOption.setId(copyId);
            copyOption.setCompoundDrawablesWithIntrinsicBounds(copyIcon, null, null, null);
            copyOption.setOnClickListener(e -> {
                Utils.setClipboard("Message Content", getMessage().content);
                Utils.showToast("Copied message content", false);
                dismiss();
            });

            var unfavId = View.generateViewId();
            TextView unfavOption = new TextView(optCtx, null, 0, R.i.UiKit_Settings_Item_Icon);
            unfavOption.setText("Unfavorite");
            unfavOption.setId(unfavId);
            unfavOption.setCompoundDrawablesWithIntrinsicBounds(delIcon, null, null, null);
            unfavOption.setTextColor(0xFFED4245);
            unfavOption.setOnClickListener(e -> {
                Map<Long, StoredMessage> favorites = sets.getObject("favorites", new HashMap<>(), FavoriteMessages.msgType);
                favorites.remove(getMessage().id);
                sets.setObject("favorites", favorites);
                Utils.showToast("Unfavorited message", false);
                page.reRender();
                dismiss();
            });

            var openId = View.generateViewId();
            TextView openOption = new TextView(optCtx, null, 0, R.i.UiKit_Settings_Item_Icon);
            openOption.setText("Jump To Message");
            openOption.setId(openId);
            openOption.setCompoundDrawablesWithIntrinsicBounds(jumpIcon, null, null, null);
            openOption.setOnClickListener(e -> {
                StoredMessage msg = getMessage();
                StoreStream.Companion.getMessagesLoader().jumpToMessage(msg.channelId, msg.id);
                dismiss();
                fragment.getActivity().onBackPressed();
                StoreStream.Companion.getTabsNavigation().selectTab(NavigationTab.HOME, true);
            });

            var copyUrlId = View.generateViewId();
            TextView copyUrlOption = new TextView(optCtx, null, 0, R.i.UiKit_Settings_Item_Icon);
            copyUrlOption.setText("Copy Message Link");
            copyUrlOption.setId(copyUrlId);
            copyUrlOption.setCompoundDrawablesWithIntrinsicBounds(copyIcon, null, null, null);
            copyUrlOption.setOnClickListener(e -> {
                String url = getMessage().getUrl();
                Utils.setClipboard("Message Link", url);
                dismiss();
            });

            var profileId = View.generateViewId();
            TextView profileOption = new TextView(optCtx, null, 0, R.i.UiKit_Settings_Item_Icon);
            profileOption.setText("View Profile");
            profileOption.setId(copyUrlId);
            profileOption.setCompoundDrawablesWithIntrinsicBounds(profileIcon, null, null, null);
            profileOption.setOnClickListener(e -> {
                WidgetUserSheet.Companion.show(getMessage().author.id, fragment.getParentFragmentManager());
                dismiss();
            });

            addView(openOption);
            addView(profileOption);
            addView(copyOption);
            addView(copyUrlOption);
            addView(unfavOption);
        }
    }

    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable {
        public static final class ViewHolder extends RecyclerView.ViewHolder {
            private final Adapter adapter;
            public final MessageCard card;

            @SuppressLint("SetTextI18n")
            public ViewHolder(Adapter adapter, MessageCard card) {
                super(card);
                this.adapter = adapter;
                this.card = card;
            }
        }

        private final AppFragment fragment;
        private final SettingsPage page;
        private final Context ctx;
        private final List<StoredMessage> originalData;
        private List<StoredMessage> data;
        private Map<Long, String> usernames = new HashMap<>();

        public Adapter(AppFragment fragment, Map<Long, StoredMessage> favorites, SettingsPage page) {
            super();

            this.fragment = fragment;
            ctx = fragment.requireContext();
            
            this.originalData = new ArrayList<StoredMessage>(favorites.values());
            originalData.sort(Comparator.comparing(p -> p.content));

            this.page = page;

            data = originalData;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(this, new MessageCard(ctx));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            StoredMessage msg = data.get(position);
            Long meId = StoreStream.getUsers().getMe().getId();
            boolean showAvatar = PluginManager.plugins.get("FavoriteMessages").settings.getBool("avatars", true);
            try {
                MessageRenderContext mrc = new MessageRenderContext(ctx, meId, true);
                Map<Long, User> users = StoreStream.getUsers().getUsers();
                for(Long uid : new ArrayList<>(users.keySet())){
                    User u = users.get(uid);
                    usernames.put(uid, u.getUsername());
                }
                Map<Long, String> channelNames = StoreStream.getChannels().getChannelNames();
                Map<Long, GuildRole> roles = StoreStream.getGuilds().getRoles().get(msg.guildId);
                ReflectUtils.setField(mrc, "userNames", usernames);
                ReflectUtils.setField(mrc, "channelNames", channelNames);
                ReflectUtils.setField(mrc, "roles", roles);
                DraweeSpanStringBuilder cnt = DiscordParser.parseChannelMessage(ctx, msg.content, mrc, new MessagePreprocessor(meId, null), DiscordParser.ParserOptions.DEFAULT, false);
                holder.card.contentView.setText(cnt);
            } catch (Throwable e) {
                Logger l = new Logger("FavoriteMessages");
                l.error("Error displaying message content", e);
            }
            
            if(showAvatar){
                Utils.threadPool.execute(() -> {
                    byte[] decodedString = Base64.decode(AvatarUtils.toBase64("https://cdn.discordapp.com/avatars/" + msg.author.id + "/" + msg.author.avatar + ".png"), Base64.DEFAULT);
                    Bitmap bitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Utils.mainThread.post(() -> {
                        holder.card.avatarView.setImageBitmap(AvatarUtils.makeCircle(bitMap));
                    });
                });
                holder.card.avatarView.setOnClickListener(e -> {
                    WidgetUserSheet.Companion.show(msg.author.id, fragment.getParentFragmentManager());
                });
            }
            holder.card.avatarView.setVisibility(showAvatar ? View.VISIBLE : View.GONE);

        
            var clock = ClockFactory.get();
            var timestamp = String.valueOf(TimeUtils.toReadableTimeString(ctx, SnowflakeUtils.toTimestamp(msg.id), clock));
            holder.card.authorView.setText(msg.author.name);
            holder.card.authorView.setOnClickListener(e -> {
                WidgetUserSheet.Companion.show(msg.author.id, fragment.getParentFragmentManager());
            });
            holder.card.dateView.setText(timestamp);
            holder.card.tagView.setVisibility(msg.author.isBot ? View.VISIBLE : View.GONE);

            int p = DimenUtils.getDefaultPadding();
            int p2 = p / 2;
            if(showAvatar == false){
                holder.card.setPadding(p, 0, p, 0);
            }

            holder.card.setOnLongClickListener(e -> {
                new MessageOptions(msg, fragment, page).show(fragment.getParentFragmentManager(), "Message Options");
                return true;
            });
        }

        public void copyText(int position) {
            StoredMessage msg = data.get(position);
            Utils.setClipboard("Message Text", msg.content);
            Utils.showToast("Copied message content", false);
        }

        private final Adapter _this = this;

        private final Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<StoredMessage> resultsList;
                if (constraint == null || constraint.equals(""))
                    resultsList = originalData;
                else {
                    final String search = constraint.toString().toLowerCase().trim();
                    resultsList = CollectionUtils.filter(originalData, p -> {
                                String content = p.content;
                                String guildName = p.guildName;
                                String channelName = p.channelName;
                                String authorName = p.author.name;
                                String guildId = p.guildId;
                                String channelId = String.valueOf(p.channelId);
                                String query = search;
                                if(query.startsWith("#")){
                                    query = query.substring(1);
                                    return channelName.toLowerCase().contains(query);
                                } else if(query.startsWith("*")){
                                    query = query.substring(1);
                                    return guildName.toLowerCase().contains(query);
                                } else if(query.startsWith("@")){
                                    query = query.substring(1);
                                    return authorName.toLowerCase().contains(query);
                                } else if (content.toLowerCase().contains(query)) return true;
                                return false;
                            }
                    );
                }
                FilterResults results = new FilterResults();
                results.values = resultsList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<StoredMessage> res = (List<StoredMessage>) results.values;
                DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    @Override
                    public int getOldListSize() {
                        return getItemCount();
                    }
                    @Override
                    public int getNewListSize() {
                        return res.size();
                    }
                    @Override
                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                        return data.get(oldItemPosition).content.equals(res.get(newItemPosition).content);
                    }
                    @Override
                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                        return true;
                    }
                }, false).dispatchUpdatesTo(_this);
                data = res;
            }
        };

        @Override
        public Filter getFilter() {
            return filter;
        }
    }
        

    @Override
    @SuppressLint("SetTextI18n")
    public void onViewBound(View view) {
        super.onViewBound(view);
        //noinspection ResultOfMethodCallIgnored
        setActionBarTitle("Favorite Messages");
        setPadding(0);

        Context context = requireContext();
        int padding = DimenUtils.getDefaultPadding();
        int p = padding / 2;

        TextInput input = new TextInput(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(padding, padding, padding, 0);
        input.setLayoutParams(params);
        input.setHint(context.getString(R.h.search));

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        Map<Long, StoredMessage> favorites = settings.getObject("favorites", new HashMap<>(), FavoriteMessages.msgType);
        Adapter adapter = new Adapter(this, favorites, this);
        recyclerView.setAdapter(adapter);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.setTint(Color.TRANSPARENT);
        shape.setIntrinsicHeight(0);
        DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(shape);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setPadding(0, padding, 0, 0);

        addView(input);
        addView(recyclerView);

        Toolbar.LayoutParams marginEndParams = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        marginEndParams.gravity = Gravity.END;
        marginEndParams.setMarginEnd(padding);
        ToolbarButton settingsBtn = new ToolbarButton(context);
        settingsBtn.setLayoutParams(marginEndParams);
        settingsBtn.setImageDrawable(ContextCompat.getDrawable(context, R.e.ic_guild_settings_24dp));
        settingsBtn.setId(settingsId);

        settingsBtn.setOnClickListener(e -> {
            new SettingsSheet(this).show(getParentFragmentManager(), "Settings");
        });

        if(getHeaderBar().findViewById(settingsId) == null) addHeaderButton(settingsBtn);

        EditText editText = input.getEditText();
        if (editText != null) {
            editText.setMaxLines(1);
            editText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    adapter.getFilter().filter(s);
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });
        }
    }

}
