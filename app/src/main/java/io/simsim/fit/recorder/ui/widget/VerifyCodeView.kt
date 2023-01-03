package io.simsim.fit.recorder.ui.widget

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.updatePadding
import kotlin.math.roundToInt

class VerifyCodeView @JvmOverloads constructor(
    ctx: Context,
    attributeSet: AttributeSet? = null,
    defaultStyle: Int = 0
) : LinearLayout(
    ctx, attributeSet, defaultStyle
) {
    // verify code count, default 4
    var codeCount: Int = 4
        set(value) {
            field = value
            invalidate()
        }

    private val codeEtList = List(codeCount) { index ->
        EditText(ctx).apply {
            setLines(1)
            setSingleLine()
            inputType = EditorInfo.TYPE_CLASS_NUMBER
            val padding = ((measuredWidth - codeCount * width) / (codeCount + 1f)).roundToInt()
            updatePadding(
                left = padding,
                right = padding
            )
        }
    }

    init {
        orientation = HORIZONTAL
        codeEtList.forEachIndexed(this::setFilter)
        codeEtList.forEach(this::addView)
        dividerPadding
    }

    private fun setFilter(index: Int, et: EditText) {
        et.filters = arrayOf(
            object : InputFilter {
                override fun filter(
                    source: CharSequence?,
                    start: Int,
                    end: Int,
                    dest: Spanned?,
                    dstart: Int,
                    dend: Int
                ): CharSequence? {
                    var keep: Int = 1 - (dest!!.length - (dend - dstart))
                    return if (keep <= 0) {
                        ""
                    } else if (keep >= end - start) {
                        clearFocus()
                        codeEtList[(index + 1).coerceAtMost(codeEtList.lastIndex)].run {
                            requestFocus()
                            isCursorVisible = true
                        }
                        null // keep original
                    } else {
                        keep += start
                        if (Character.isHighSurrogate(source!![keep - 1])) {
                            --keep
                            if (keep == start) {
                                return ""
                            }
                        }
                        source.subSequence(start, keep)
                    }
                }
            }
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            context.resources.displayMetrics.widthPixels,
            MeasureSpec.getSize(heightMeasureSpec)
        )
    }
}