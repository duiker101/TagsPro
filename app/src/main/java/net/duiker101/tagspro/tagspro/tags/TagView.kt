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


/**
 * View to display a Tag, it's basically a button but really it's a TextView with the button style since
 * actually using a Button it's slow due to some type of animation that the button has when it gets
 * added to the view in the RecyclerView
 */
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

    /**
     * Set the state of the model and update the view. Beware this triggers an event in the bus
     */
    fun toggle(on: Boolean) {
        tag.active = on
        EventBus.getDefault().post(TagEvent(tag))
        updateBackground()
    }

    /**
     * Update he background color based on the model state
     */
    fun updateBackground() {
        if (tag.active) {
            background.colorFilter = LightingColorFilter(0x000000, context.resources.getColor(R.color.tag_background_active))
        } else {
            background.colorFilter = LightingColorFilter(0x000000, context.resources.getColor(R.color.tag_background_empty))
        }
    }
}

