package xyz.wingio.plugins.achievements.recycler;

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

import xyz.wingio.plugins.achievements.Achievement;

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

import java.io.*;

public class AchListAdapter extends RecyclerView.Adapter<AchListAdapter.AchListHolder> {

    public class AchListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AchListAdapter adapter;
        public final RecyclerItem item;

        public AchListHolder(AchListAdapter adapter, RecyclerItem item) {
            super(item);
            this.adapter = adapter;
            this.item = item;
        }

        @Override public void onClick(View view) {
            adapter.onClick(view.getContext(), getAdapterPosition());
        }
    }

    private final Context ctx;
    private final List<Achievement> achievements;
    private final SettingsPage page;
    public Logger logger = new Logger("ACH");

    public AchListAdapter(SettingsPage page, List<Achievement> achievements) {
        this.achievements = achievements;
        this.page = page;
        ctx = page.getContext();
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    @NonNull
    @Override
    public AchListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AchListHolder(this, new RecyclerItem(ctx));
    }

    @Override
    public void onBindViewHolder(@NonNull AchListHolder holder, int position) {
        Achievement ach = achievements.get(position);
        holder.item.name.setText(ach.getName());
        Drawable icon = ContextCompat.getDrawable(ctx, R.d.ic_slash_command_24dp).mutate();
        icon.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
        holder.item.name.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
    }

    public void onClick(Context ctx, int position) {
        
    }
}
