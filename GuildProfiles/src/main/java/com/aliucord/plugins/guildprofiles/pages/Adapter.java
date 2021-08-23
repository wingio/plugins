package com.aliucord.plugins.guildprofiles.pages;

import android.content.Context;
import android.view.*;
import android.widget.RelativeLayout;
import android.graphics.*;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.DimenRes;

import com.aliucord.Utils;
import com.aliucord.Http;
import com.aliucord.Logger;
import com.discord.models.member.GuildMember;
import com.discord.models.user.User;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.extensions.SimpleDraweeViewExtensionsKt;
import com.discord.utilities.icon.IconUtils;
import com.discord.utilities.images.MGImages;
import com.discord.stores.*;
import com.lytefast.flexinput.R;
import d0.z.d.m;
import com.facebook.drawee.view.SimpleDraweeView;

import kotlin.jvm.functions.Function1;
import java.util.List;
import java.lang.*;
import java.io.*;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {
    private static final int layoutId = Utils.getResId("widget_user_profile_adapter_item_server", "layout");

    private final List<GuildMember> friends;
    private final MutualFriendsPage page;

    public Adapter(MutualFriendsPage page, List<GuildMember> friends) {
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
        return new ViewHolder(this, (RelativeLayout) layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        var friend = friends.get(position);
        var friendUser = StoreStream.getUsers().getUsers().get(friend.getUserId());
        var color = Integer.valueOf(ColorCompat.getThemedColor(holder.itemView.getContext(), R.b.colorBackgroundPrimary));
        Logger logger = new Logger("GP");
        logger.debug(friendUser.getAvatar());
        if (friendUser.getAvatar() != null) {
            Utils.threadPool.execute(() -> {
                var imgUrl = "https://cdn.discordapp.com/avatars/" + friendUser.getId() + "/" + friendUser.getAvatar() + ".png";
                logger.debug(imgUrl);
                var avUri = imageToDataUri(imgUrl);
                byte[] decodedString = Base64.decode(avUri, Base64.DEFAULT);
                Bitmap bitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Utils.mainThread.post(() -> {
                    holder.icon.setImageBitmap(getCircularBitmap(bitMap));
                });
            });
            holder.iconText.setVisibility(View.GONE);
        } else {
            holder.icon.setVisibility(View.GONE);
        }

        holder.name.setText(friendUser.getUsername());
        holder.nick.setVisibility(View.GONE);
    }

    public final void setAvatar(SimpleDraweeView simpleDraweeView, User user, boolean z2, @DimenRes int i, GuildMember guildMember) {
        //m.checkNotNullParameter(simpleDraweeView, "$this$setAvatar");
        int dimensionPixelSize = 1024;
        int iconResId = Utils.getResId("uikit_icon_url", "id");
        String forGuildMemberOrUser = IconUtils.INSTANCE.getForGuildMemberOrUser(user, guildMember, dimensionPixelSize > 0 ? Integer.valueOf(IconUtils.getMediaProxySize(dimensionPixelSize)) : null);
        if (!m.areEqual(forGuildMemberOrUser, simpleDraweeView.getTag(iconResId))) {
            simpleDraweeView.setTag(iconResId, forGuildMemberOrUser);
            IconUtils.setIcon$default(simpleDraweeView, forGuildMemberOrUser, i, (Function1) null, (MGImages.ChangeDetector) null, 24, (Object) null);
        }
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
    Bitmap output;

    if (bitmap.getWidth() > bitmap.getHeight()) {
        output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
    } else {
        output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
    }

    Canvas canvas = new Canvas(output);

    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

    float r = 0;

    if (bitmap.getWidth() > bitmap.getHeight()) {
        r = bitmap.getHeight() / 2;
    } else {
        r = bitmap.getWidth() / 2;
    }

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawCircle(r, r, r, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);
    return output;
}

    private String imageToDataUri(String url) {
        try {
            var res = new Http.Request(url).execute();
            try (var baos = new ByteArrayOutputStream()) {
                res.pipe(baos);
                var b64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                return String.format("%s", b64);
            }
        } catch (IOException ex) {return null; }
    }

    public void onClick(Context ctx, int position) {
        
    }
}
