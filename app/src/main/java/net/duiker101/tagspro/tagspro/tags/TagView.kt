package net.duiker101.tagspro.tagspro.tags

import android.content.Context
import android.graphics.LightingColorFilter
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.widget.TextView
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.api.Tag
import net.duiker101.tagspro.tagspro.events.TagEvent
import org.greenrobot.eventbus.EventBus


class TagView(context: Context, attributes: AttributeSet?) :
        TextView(ContextThemeWrapper(context, R.style.TagStyle), attributes) {

    var tag: Tag = Tag("", false)
        set(value) {
            field = value
            text = value.name
            updateBackground()
        }

    init {
        isClickable = true
        isEnabled = true
        setOnClickListener {
            toggle(!tag.active)
        }
    }

    fun toggle(on: Boolean) {
        tag.active = on
        EventBus.getDefault().post(TagEvent(tag))
        updateBackground()
//        listener(tag)
    }

    fun updateBackground() {
        if (tag.active)
            background.colorFilter = LightingColorFilter(0x000000, 0x55efc4)
        else
            background.colorFilter = LightingColorFilter(0x000000, 0xeeeeee)
    }
}

