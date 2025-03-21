package xyz.wingio.plugins.morehighlight;

import com.discord.simpleast.core.node.Node;
import com.discord.utilities.textprocessing.*;
import com.discord.utilities.color.ColorCompat;

import android.text.SpannableStringBuilder;
import android.text.style.*;
import android.graphics.Color;

import com.lytefast.flexinput.R;

public class TinyTextNode<MessageRenderContext> extends Node<MessageRenderContext> {
  String content;

  public TinyTextNode(String content){
    super();
    this.content = content;
  }

  @Override
  public void render(SpannableStringBuilder builder, MessageRenderContext renderContext) {
    int length = builder.length();
    builder.append(content);
    
    // Make text smaller
    builder.setSpan(new RelativeSizeSpan(0.85f), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
    
    // Make text grey
    // Use a standard grey color instead of trying to access the theme
    int greyColor = Color.parseColor("#99AAB5"); // Discord's standard muted text color
    builder.setSpan(new ForegroundColorSpan(greyColor), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
  }
}
