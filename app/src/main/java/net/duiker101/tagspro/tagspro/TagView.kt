package net.duiker101.tagspro.tagspro

import android.content.Context
import android.graphics.LightingColorFilter
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.widget.Button
import org.greenrobot.eventbus.EventBus


class TagView(context: Context, attributes: AttributeSet?) :
        Button(ContextThemeWrapper(context, R.style.TagStyle), attributes, 0) {

    var tag: Tag = Tag("", false)
        set(value) {
            field = value
            text = value.name
            updateBackground()
        }

    init {
        setOnClickListener {
            tag.active = !(tag.active)
            EventBus.getDefault().post(TagEvent(tag))
            updateBackground()
        }
    }

    private fun updateBackground() {
        if (tag.active)
            background.colorFilter = LightingColorFilter(0x000000, 0x55efc4)
        else
            background.colorFilter = LightingColorFilter(0x000000, 0xeeeeee)
    }
}