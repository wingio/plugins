package xyz.wingio.plugins;

import android.content.Context;
import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.*;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.graphics.Color;

import androidx.fragment.app.FragmentManager;
import androidx.core.graphics.ColorUtils;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;

import com.aliucord.Constants;
import com.aliucord.Logger;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.*;
import com.aliucord.widgets.BottomSheet;
import com.aliucord.views.Button;
import com.aliucord.views.Divider;

import com.discord.api.role.GuildRole;
import com.discord.widgets.roles.RolesListView$updateView$$inlined$forEach$lambda$1;
import com.discord.widgets.roles.RolesListView;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.textprocessing.node.RoleMentionNode;
import com.discord.utilities.guilds.RoleUtils;

import com.facebook.drawee.view.SimpleDraweeView;

import com.lytefast.flexinput.R;

import java.util.*;

// This class is never used so your IDE will likely complain. Let's make it shut up!
@SuppressWarnings("unused")
@AliucordPlugin
public class RCM extends Plugin {
    private static FragmentManager cachedFragment = Utils.appActivity.getSupportFragmentManager();
    private static final int p = DimenUtils.dpToPx(16);
    public static class RoleBottomSheet extends BottomSheet {
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            setPadding(p);
            Context ctx = view.getContext();
            var args = getArguments();
            var themedColor = ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal);
            boolean hasColor = args.getString("roleColor", "000000") != "default";

            LinearLayout infoView = new LinearLayout(ctx);
            infoView.setOrientation(LinearLayout.HORIZONTAL);
            infoView.setVerticalGravity(Gravity.CENTER_VERTICAL);
            infoView.setPadding(0, 0, 0, p);

            SimpleDraweeView icon = new SimpleDraweeView(ctx);
            icon.setLayoutParams(new LinearLayout.LayoutParams(DimenUtils.dpToPx(48), DimenUtils.dpToPx(48)));
            if(args.getBoolean("hasIcon", false)) {  icon.setImageURI(String.format("https://cdn.discordapp.com/role-icons/%s/%s.png", args.getString("roleId", "0"), args.getString("icon", ""))); } else {  Drawable shield = ContextCompat.getDrawable(ctx, R.d.ic_shieldstar_24dp).mutate(); shield.setTint(hasColor ? Color.parseColor("#" + args.getString("roleColor", "000000")) : themedColor); icon.setImageDrawable(shield); }
            infoView.addView(icon);

            LinearLayout details = new LinearLayout(ctx);
            details.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMarginStart(p);
            details.setLayoutParams(params);
            
            TextView title = addText(ctx, args.getString("roleName", ""), 16, true);
            details.addView(title);
            if(args.getBoolean("isManaged", false)) details.addView(addText(ctx, "This role is managed by an integration", 12, false));
            if(args.getBoolean("isHoisted", false)) details.addView(addText(ctx, "This role is hoisted", 12, false));
            TextView color = addText(ctx, "Color: ", 12, false);
            if(hasColor && args.getBoolean("hasIcon", false)) {
                var clr = args.getString("roleColor", "000000");
                Drawable dot = ContextCompat.getDrawable(ctx, R.d.drawable_circle_white_12dp).mutate();
                dot.setTint(Color.parseColor("#" + clr));
                color.setCompoundDrawablePadding(DimenUtils.dpToPx(2));
                color.setCompoundDrawablesWithIntrinsicBounds(null, null, dot, null);
                details.addView(color);
            }
            
            infoView.addView(details);

            Button copyIdBtn = new Button(ctx);
            copyIdBtn.setText("Copy ID");
            LinearLayout.LayoutParams copyIdParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            copyIdParams.setMargins(0, p, 0, 0);
            copyIdBtn.setLayoutParams(copyIdParams);
            copyIdBtn.setOnClickListener(text -> {
                copyToClipboard(args.getString("roleId", "0"));
            });

            Button copyColorBtn = new Button(ctx);
            copyColorBtn.setText("Copy Color");
            copyColorBtn.setOnClickListener(text -> {
                copyToClipboard(args.getString("roleColor", "000000"));
            });

            getLinearLayout().addView(infoView);
            getLinearLayout().addView(new Divider(ctx));
            getLinearLayout().addView(copyIdBtn);
            if(hasColor) getLinearLayout().addView(copyColorBtn);
        }

        private void copyToClipboard(String copy) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied to clipboard.", copy);
            Toast.makeText(getContext(), "Copied to clipboard.", Toast.LENGTH_SHORT).show();
            clipboard.setPrimaryClip(clip);
        }

        private TextView addText(Context ctx, String text, int size, boolean isBold) {
            TextView name = new TextView(ctx);
            name.setText(text);
            name.setTypeface(ResourcesCompat.getFont(ctx, isBold ? Constants.Fonts.whitney_bold : Constants.Fonts.whitney_medium));
            name.setTextSize(size);
            name.setTextColor(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
            name.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return name;
        }
    }

    private final Logger log = new Logger("RCM");

    @Override
    public void start(Context context) throws NoSuchMethodException {
        
        patcher.patch(RolesListView$updateView$$inlined$forEach$lambda$1.class.getDeclaredMethod("onClick", View.class), new PineInsteadFn(callFrame -> {
            try {
                GuildRole role = ((RolesListView$updateView$$inlined$forEach$lambda$1) callFrame.thisObject).$role;
                RolesListView view = ((RolesListView$updateView$$inlined$forEach$lambda$1) callFrame.thisObject).this$0;

                Bundle args = new Bundle();
                args.putString("roleColor", role.b() != 0 ? String.format("%06x", role.b()) : "default");
                args.putString("roleId", String.valueOf(role.getId()));
                args.putString("roleName", role.g());
                args.putBoolean("isManaged", role.e());
                args.putBoolean("isHoisted", role.c());
                args.putBoolean("hasIcon", role.d() != null);
                if(role.d() != null) args.putString("icon", role.d());

                var roleMenu = new RoleBottomSheet();
                roleMenu.setArguments(args);
                roleMenu.show(cachedFragment, "Role Menu");
            } catch (Exception e) {
                log.error(e);
            }
            return null;
        }));

        patcher.patch(RoleMentionNode.class.getDeclaredMethod("render", SpannableStringBuilder.class, RoleMentionNode.RenderContext.class), new PineInsteadFn(callFrame -> {
            RoleMentionNode _this = (RoleMentionNode) callFrame.thisObject;
            SpannableStringBuilder builder = (SpannableStringBuilder) callFrame.args[0];
            RoleMentionNode.RenderContext nodeRc = (RoleMentionNode.RenderContext) callFrame.args[1];

            int length = builder.length();
            builder.append("@");
            Map<Long, GuildRole> roles = nodeRc.getRoles();
            GuildRole guildRole = roles != null ? roles.get(Long.valueOf(_this.getRoleId())) : null;
            GuildRole role = roles != null ? roles.get(Long.valueOf(_this.getRoleId())) : null;
            if (guildRole != null) {
                builder.append(guildRole.g());
                ClickableSpan cs = new ClickableSpan() { @Override public void onClick(View widget) { try { Bundle args = new Bundle(); args.putString("roleColor", role.b() != 0 ? String.format("%06x", role.b()) : "default"); args.putString("roleId", String.valueOf(role.getId())); args.putString("roleName", role.g()); args.putBoolean("isManaged", role.e()); args.putBoolean("isHoisted", role.c()); args.putBoolean("hasIcon", role.d() != null); if(role.d() != null) args.putString("icon", role.d()); var roleMenu = new RoleBottomSheet(); roleMenu.setArguments(args); roleMenu.show(cachedFragment, "Role Menu"); } catch(Throwable e) { log.error(e); } } @Override public void updateDrawState(TextPaint ds) { ds.setUnderlineText(false); }};
                List<Object> listOf = Arrays.asList(cs, new StyleSpan(1), new ForegroundColorSpan(!RoleUtils.isDefaultColor(guildRole) ? ColorUtils.setAlphaComponent(guildRole.b(), 255) : ColorCompat.getThemedColor(nodeRc.getContext(), (int) R.b.theme_chat_mention_foreground)), new BackgroundColorSpan(!RoleUtils.isDefaultColor(guildRole) ? ColorUtils.setAlphaComponent(guildRole.b(), 25) : ColorCompat.getThemedColor(nodeRc.getContext(), (int) R.b.theme_chat_mention_background)));
                
                for (Object obj : listOf) {
                    builder.setSpan(obj, length, builder.length(), 33);
                }
            } else {
                builder.append("deleted-role");
            }
            return null;
        }));
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
