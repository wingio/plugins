package xyz.wingio.plugins.morehighlight;

import androidx.core.graphics.ColorUtils;

import com.discord.simpleast.core.node.Node;
import com.discord.utilities.textprocessing.*;

import android.text.SpannableStringBuilder;
import android.text.style.*;

public class ColorNode<MessageRenderContext> extends Node<MessageRenderContext> {
  String content;
  int color;

  public ColorNode(String content, int color){
    super();
    this.content = content;
    this.color = color;
  }

  @Override
  public void render(SpannableStringBuilder builder, MessageRenderContext renderContext) {
    int length = builder.length();
    builder.append(content);
    builder.setSpan(new ForegroundColorSpan(color), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
    builder.setSpan(new BackgroundColorSpan(ColorUtils.setAlphaComponent(color, 25)), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
    builder.setSpan(new StyleSpan(1), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
  }
}