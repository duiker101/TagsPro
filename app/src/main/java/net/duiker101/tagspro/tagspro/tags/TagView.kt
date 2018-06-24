package net.duiker101.tagspro.tagspro.tags

import android.content.Context
import android.graphics.LightingColorFilter
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.widget.TextView
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.api.Tag


//class TagView(context: Context, attributes: AttributeSet?, private val listener: (tag: Tag) -> Unit) :
//        Button(ContextThemeWrapper(context, R.style.TagStyle), attributes, 0) {
class TagView(context: Context, attributes: AttributeSet?, private val listener: (tag: Tag) -> Unit) :
        TextView(ContextThemeWrapper(context, R.style.TagStyle), attributes) {

    var tag: Tag = Tag("", false)
        set(value) {
            field = value
            text = value.name
            updateBackground()
//            setBackgroundResource(R.drawable.shadow)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                elevation=2f
//            }
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
        updateBackground()
        listener(tag)
//        EventBus.getDefault().post(TagEvent(tag.name, tag.active))
    }

    fun updateBackground() {
        if (tag.active)
            background.colorFilter = LightingColorFilter(0x000000, 0x55efc4)
        else
            background.colorFilter = LightingColorFilter(0x000000, 0xeeeeee)

//        if (tag.active)
//            setTextColor(Color.BLUE)
////            setTextColor(0x55efc4)
//        else
//            setTextColor(Color.BLACK)
////            setTextColor(0xeeeeee)

    }

//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        EventBus.getDefault().register(this)
//    }
//
//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//        EventBus.getDefault().unregister(this)
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMessageEvent(event: TagEvent) {
//        if (event.tag != tag.name)
//            return
//
//        if (event.selected)
//            background.colorFilter = LightingColorFilter(0x000000, 0x55efc4)
//        else
//            background.colorFilter = LightingColorFilter(0x000000, 0xeeeeee)
//    }
}

