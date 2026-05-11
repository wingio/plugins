package xyz.wingio.plugins.morehighlight.node

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.text.style.StyleSpan
import com.aliucord.utils.DimenUtils.dp
import com.discord.simpleast.core.node.Node
import com.discord.utilities.color.ColorCompat
import com.lytefast.flexinput.R

class BulletPointNode<MessageRenderContext>(private val context: Context): Node.a<MessageRenderContext>() {

    override fun render(builder: SpannableStringBuilder, renderContext: MessageRenderContext) {
        val length = builder.length
        super.render(builder, renderContext)

        val greyColor = ColorCompat.getThemedColor(context, R.b.colorTextMuted)
        val gapWidth = 8.dp
        val span = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) BulletSpan(gapWidth, greyColor,  /* bulletRadius = */ 6) else BulletSpan(gapWidth, greyColor)

        builder.setSpan(span, length, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(StyleSpan(Typeface.NORMAL), length, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

}