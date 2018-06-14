package net.duiker101.tagspro.tagspro

import android.content.Context
import android.graphics.ColorFilter
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.widget.Button
import android.graphics.LightingColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter


class TagView(context: Context, attributes: AttributeSet?) :
        Button(ContextThemeWrapper(context, R.style.TagStyle), attributes, 0) {
    private var active: Boolean = false

    init {
        toggle()
        setOnClickListener {
            active = !active
            toggle()
        }
    }

    private fun toggle() {
        if (active)
            background.colorFilter = LightingColorFilter(0x000000, 0x55efc4)
        else
            background.colorFilter = LightingColorFilter(0x000000, 0xeeeeee)
    }
}