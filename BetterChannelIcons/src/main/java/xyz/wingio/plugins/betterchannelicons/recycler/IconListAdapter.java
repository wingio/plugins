package xyz.wingio.plugins.betterchannelicons.recycler;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.Base64;
import android.app.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.DimenRes;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;

import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.Http;
import com.aliucord.Logger;
import com.aliucord.fragments.SettingsPage;
import com.discord.models.member.GuildMember;
import com.discord.models.user.User;
import com.discord.utilities.color.ColorCompat;
import com.discord.databinding.UserProfileHeaderBadgeBinding;
import com.discord.utilities.extensions.SimpleDraweeViewExtensionsKt;
import com.discord.utilities.icon.IconUtils;
import com.discord.utilities.images.MGImages;
import com.discord.stores.*;
import com.discord.widgets.user.usersheet.WidgetUserSheet;
import com.discord.widgets.user.*;
import com.lytefast.flexinput.R;
import com.facebook.drawee.view.SimpleDraweeView;

import kotlin.jvm.functions.Function1;
import java.util.*;

import xyz.wingio.plugins.betterchannelicons.*;

import java.io.*;

public class IconListAdapter extends RecyclerView.Adapter<IconListAdapter.IconListHolder> {

    public class IconListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final IconListAdapter adapter;
        public final RecyclerItem item;

        public IconListHolder(IconListAdapter adapter, RecyclerItem item) {
            super(item);
            this.adapter = adapter;
            this.item = item;
        }

        @Override public void onClick(View view) {
            adapter.onClick(view.getContext(), getAdapterPosition());
        }
    }

    private final Context ctx;
    private final Map<String, Integer> icons;
    private final SettingsPage page;
    public Logger logger = new Logger("BCI");

    public IconListAdapter(SettingsPage page, Map<String, Integer> icons) {
        this.icons = icons;
        this.page = page;
        ctx = page.getContext();
    }

    @Override
    public int getItemCount() {
        return new ArrayList<>(icons.keySet()).size();
    }

    @NonNull
    @Override
    public IconListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IconListHolder(this, new RecyclerItem(ctx));
    }

    @Override
    public void onBindViewHolder(@NonNull IconListHolder holder, int position) {
        String name = new ArrayList<>(icons.keySet()).get(position);
        Integer iconId = icons.get(name);
        holder.item.name.setText(name);
        holder.item.delete.setOnClickListener(v -> {
            icons.remove(name);
            PluginManager.plugins.get("BetterChannelIcons").settings.setObject("icons", icons);
            notifyDataSetChanged();
        });
        try {
            Drawable icon = ContextCompat.getDrawable(ctx, Constants.getIcons().get(iconId)).mutate();
            icon.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
            holder.item.name.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        } catch (Throwable e) {
            logger.error("Failed to load icon", e);
        }
    }

    public void onClick(Context ctx, int position) {
        
    }
}
