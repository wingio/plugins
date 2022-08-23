package xyz.wingio.plugins.morehighlight;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.*;

import androidx.core.graphics.ColorUtils;

import com.discord.simpleast.core.node.Node;
import com.discord.stores.*;
import com.discord.utilities.textprocessing.*;
import com.discord.utilities.spans.ClickableSpan;
import com.discord.utilities.color.ColorCompat;

import com.lytefast.flexinput.R;
import kotlin.Unit;

public final class SlashCommandNode extends Node<MessageRenderContext> {
    private String content;

    public SlashCommandNode(String content) {
        super();
        this.content = content;
    }

    @Override
    public void render(SpannableStringBuilder builder, MessageRenderContext renderContext) {
        var color = Integer.valueOf(ColorCompat.getThemedColor(renderContext.getContext(), R.b.colorTextLink));
        int length = builder.length();
        builder.append("/" + content);
        builder.setSpan(new ClickableSpan(color, false, v -> {return Unit.a;}, v-> {
            StoreStream.getChat().replaceChatText("/" + content);
            return Unit.a;
        }), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new BackgroundColorSpan(ColorUtils.setAlphaComponent(color, 25)), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(1), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}