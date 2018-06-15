package net.duiker101.tagspro.tagspro

import android.content.Context
import android.graphics.LightingColorFilter
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.widget.Button


class TagView(context: Context, attributes: AttributeSet?, private val listener: (tag: Tag) -> Unit) :
        Button(ContextThemeWrapper(context, R.style.TagStyle), attributes, 0) {

    var tag: Tag = Tag("", false)
        set(value) {
            field = value
//            text = context.getString(R.string.tag_name, value.name)
            text = value.name
            updateBackground()
        }

    init {
        setOnClickListener {
            toggle(!tag.active)
        }
    }

    fun toggle(on: Boolean) {
        tag.active = on
        updateBackground()
        listener(tag)
    }

    private fun updateBackground() {
        if (tag.active)
            background.colorFilter = LightingColorFilter(0x000000, 0x55efc4)
        else
            background.colorFilter = LightingColorFilter(0x000000, 0xeeeeee)
    }
}