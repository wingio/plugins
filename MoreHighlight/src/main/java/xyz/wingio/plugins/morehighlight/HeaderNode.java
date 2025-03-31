package xyz.wingio.plugins.morehighlight;

import androidx.core.graphics.ColorUtils;

import com.discord.simpleast.core.node.Node;
import com.discord.utilities.textprocessing.*;

import android.text.SpannableStringBuilder;
import android.text.style.*;

public class HeaderNode<MessageRenderContext> extends Node<MessageRenderContext> {
  int headerSize;

  public HeaderNode(int headerSize){
    super();
    this.headerSize = headerSize;
  }

  @Override
  public void render(SpannableStringBuilder builder, MessageRenderContext renderContext) {
    int length = builder.length();
    for (Node n:
            getChildren()) {
      n.render(builder, renderContext);
    }
    switch (headerSize) {
      case 1:
        builder.setSpan(new RelativeSizeSpan(2.0f), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        break;
      case 2:
        builder.setSpan(new RelativeSizeSpan(1.5f), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        break;
      case 3:
        builder.setSpan(new RelativeSizeSpan(1.25f), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        break;
    }
    builder.setSpan(new StyleSpan(1), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
  }
}
