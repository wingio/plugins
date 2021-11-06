package xyz.wingio.plugins.showperms.widgets;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.*;

import androidx.core.graphics.ColorUtils;

import com.aliucord.utils.*;

import com.discord.models.member.GuildMember;
import com.discord.models.user.User;
import com.discord.utilities.icon.IconUtils;
import com.discord.utilities.user.UserUtils;
import com.discord.utilities.color.ColorCompat;
import com.discord.stores.*;

import com.facebook.drawee.view.SimpleDraweeView;

import com.lytefast.flexinput.R;

public class WidgetUserOverwrite extends LinearLayout {
    private GuildMember member = null;
    private int p = DimenUtils.dpToPx(16);
    private SimpleDraweeView avatar;
    private TextView name;
    private TextView nick;
    
    public WidgetUserOverwrite(Context context) {
        super(context);
        configureUI();
    }

    public void configureUI(){
        Context ctx = getContext();
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(p, p, p, p);
        
        avatar = new SimpleDraweeView(ctx);
        avatar.setLayoutParams(new LayoutParams(DimenUtils.dpToPx(40), DimenUtils.dpToPx(40)));
        avatar.setImageURI("https://cdn.discordapp.com/embed/avatars/0.png");
        avatar.setClipToOutline(true);
        var circle = new ShapeDrawable(new OvalShape());
        var paint = circle.getPaint();
        paint.setColor(android.graphics.Color.TRANSPARENT);
        avatar.setBackground(circle);
        addView(avatar);

        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.VERTICAL);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(p, 0, 0, 0);
        ll.setLayoutParams(lp);

        name = new TextView(ctx, null, 0, R.i.UiKit_TextView_Semibold);
        name.setText("UNKNOWN_USER");
        name.setTextSize(15f);
        ll.addView(name);

        nick = new TextView(ctx, null, 0, R.i.UiKit_TextAppearance_Semibold);
        nick.setText("UNKNOWN_USER");
        nick.setVisibility(GONE);
        nick.setTextSize(12f);
        nick.setTextColor(ColorUtils.setAlphaComponent(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal), 110));
        ll.addView(nick);

        addView(ll);
    }
    
    public WidgetUserOverwrite setUser(User user, Long guildId) {
        GuildMember member = StoreStream.getGuilds().getMembers().get(guildId).get(user.getId());
        avatar.setImageURI(IconUtils.getForUser(user.getId(), user.getAvatar()));
        if(member == null || member.getNick() == null || member.getNick().isEmpty()) {
            name.setText(user.getUsername() + UserUtils.INSTANCE.padDiscriminator(user.getDiscriminator()));
        } else {
            name.setText(member.getNick());
            nick.setText(user.getUsername() + UserUtils.INSTANCE.padDiscriminator(user.getDiscriminator()));
            nick.setVisibility(View.VISIBLE);
        }
        return this;
    }
}