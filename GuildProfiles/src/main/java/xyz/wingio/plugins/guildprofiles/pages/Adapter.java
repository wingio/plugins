package xyz.wingio.plugins.guildprofiles.pages;

import android.content.Context;
import android.view.*;
import android.widget.RelativeLayout;
import android.graphics.*;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Base64;
import android.app.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.DimenRes;
import androidx.fragment.app.FragmentActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.Http;
import com.aliucord.Logger;
import xyz.wingio.plugins.guildprofiles.util.*;
import com.aliucord.fragments.SettingsPage;
import com.discord.models.member.GuildMember;
import com.discord.models.user.User;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.extensions.SimpleDraweeViewExtensionsKt;
import com.discord.utilities.icon.IconUtils;
import com.discord.utilities.images.MGImages;
import com.discord.stores.*;
import com.discord.widgets.user.usersheet.WidgetUserSheet;
import com.lytefast.flexinput.R;
import com.facebook.drawee.view.SimpleDraweeView;

import kotlin.jvm.functions.Function1;
import java.util.List;

import java.io.*;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {
    private static final int layoutId = Utils.getResId("widget_user_profile_adapter_item_server", "layout");

    private final List<GuildMember> friends;
    private final SettingsPage page;
    public Logger logger = new Logger("GP");

    public Adapter(SettingsPage page, List<GuildMember> friends) {
        this.friends = friends;
        this.page = page;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var layout = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(this, (ConstraintLayout) layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        var friend = friends.get(position);
        var friendUser = StoreStream.getUsers().getUsers().get(friend.getUserId());
        var color = Integer.valueOf(ColorCompat.getThemedColor(holder.itemView.getContext(), R.b.colorBackgroundPrimary));
        
        AvatarUtils avUtil = new AvatarUtils(friendUser);
        if (friendUser.getAvatar() != null) {
            Utils.threadPool.execute(() -> {
                byte[] decodedString = Base64.decode(avUtil.toBase64(), Base64.DEFAULT);
                Bitmap bitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Utils.mainThread.post(() -> {
                    holder.icon.setImageBitmap(AvatarUtils.makeCircle(bitMap));
                });
            });
            holder.iconText.setVisibility(View.GONE);
        } else {
            holder.icon.setVisibility(View.GONE);
        }

        holder.name.setText(friendUser.getUsername());
        if(holder.nick != null) holder.nick.setVisibility(View.GONE);
    }

    public void onClick(Context ctx, int position) {
        var friend = friends.get(position);
        try {
            final FragmentActivity activity = (FragmentActivity) ctx;
            WidgetUserSheet.Companion.show(friend.getUserId(), activity.getSupportFragmentManager());
        } catch (Throwable e) {  logger.error("Error opening user sheet", e);}
    }
}
