package xyz.wingio.plugins.custombadges;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Base64;
import android.app.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.DimenRes;
import androidx.fragment.app.FragmentActivity;

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
import java.util.List;

import java.io.*;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeHolder> {
    private static final int layoutId = Utils.getResId("user_profile_header_badge", "layout");
    private final Context ctx;
    private final List<Badge> badges;
    private final ProfileWidget profile;
    public Logger logger = new Logger("GP");

    public BadgeAdapter(ProfileWidget profile, List<Badge> badges) {
        this.badges = badges;
        this.profile = profile;
        ctx = profile.getContext();
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    @NonNull
    @Override
    public BadgeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BadgeHolder(this, new ImageView(ctx));
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeHolder holder, int position) {
        Badge badge = badges.get(position);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dpToPx(20), Utils.dpToPx(20));
        params.setMargins(Utils.dpToPx(5), Utils.dpToPx(5), 0, 0);
        holder.icon.setLayoutParams(params);
        holder.icon.setImageResource(badge.getIcon());
    }

    public void onClick(Context ctx, int position) {
        
    }
}
