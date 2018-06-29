package net.duiker101.tagspro.tagspro.tags

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.api.Tag

/**
 * Adapter TagsView to display a collection of tags
 * if the data is empty we show a TextView that shows the defaultMsg member
 */
class TagsAdapter(
        private val defaultMsg: String,
        val tags: ArrayList<Tag>
) : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    /**
     * constants for the type of views to display
     */
    companion object {
        const val TYPE_DEFAULT = 234
        const val TYPE_TAG = 235
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == TYPE_DEFAULT) {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.listitem_default_text, parent, false))
        } else ViewHolder(TagView(parent.context, null))
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
