package xyz.wingio.plugins.morehighlight;

import com.discord.simpleast.core.node.Node;
import com.discord.utilities.textprocessing.*;
import com.discord.utilities.color.ColorCompat;

import android.text.SpannableStringBuilder;
import android.text.style.*;
import android.graphics.Color;

import com.lytefast.flexinput.R;

public class SubtextNode<MessageRenderContext> extends Node<MessageRenderContext> {
  String content;

  public SubtextNode(String content){
    super();
    this.content = content;
  }

  @Override
  public void render(SpannableStringBuilder builder, MessageRenderContext renderContext) {
    int length = builder.length();
    builder.append(content);
    
    builder.setSpan(new RelativeSizeSpan(0.85f), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
    
    int greyColor = Color.parseColor("#99AAB5");
    builder.setSpan(new ForegroundColorSpan(greyColor), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
  }
}
