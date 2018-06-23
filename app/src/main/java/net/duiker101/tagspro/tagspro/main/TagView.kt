package net.duiker101.tagspro.tagspro.main

import android.content.Context
import android.graphics.LightingColorFilter
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.tags.Tag


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

class TagsAdapter(private val defaultMsg: String, private val listener: (tag: Tag) -> Unit) : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    var tags = ArrayList<Tag>()

    companion object {
        const val TYPE_DEFAULT = 0
        const val TYPE_TAG = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == TYPE_DEFAULT) {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.listitem_default_text, parent, false))
        } else
            ViewHolder(TagView(parent.context, null, listener))
    }

    override fun getItemCount(): Int {
        return if (tags.size == 0) 1 else tags.size
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && tags.size == 0) TYPE_DEFAULT else TYPE_TAG
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_TAG)
            (holder.view as TagView).tag = tags[position]
        else
            (holder.view as TextView).text = defaultMsg
    }
}
