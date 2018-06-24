package net.duiker101.tagspro.tagspro.tags

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import net.duiker101.tagspro.tagspro.api.Tag

class TagsAdapter(
        private val defaultMsg: String,
        val tags: ArrayList<Tag>,
        private val listener: (tag: Tag) -> Unit)
    : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    companion object {
        const val TYPE_DEFAULT = 0
        const val TYPE_TAG = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return if (viewType == TYPE_DEFAULT) {
//            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.listitem_default_text, parent, false))
//        } else
        val t = TagView(parent.context, null, listener)
        Log.w("Simone", "init tag view " + System.identityHashCode(t))
        return ViewHolder(t)
    }

    override fun getItemCount(): Int {
//        return if (tags.size == 0) 1 else tags.size
        return tags.size
    }


    override fun getItemViewType(position: Int): Int {
        return 0
//        return if (position == 0 && tags.size == 0) TYPE_DEFAULT else TYPE_TAG
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = tags[position]
        Log.w("Simone", "bind ${tag.name} in card ${tag.media_count} " + System.identityHashCode(holder.view))
//        if (holder.itemViewType == TYPE_TAG)
        (holder.view as TagView).tag = tags[position]
//        else
//            (holder.view as TextView).text = defaultMsg
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
//        super.onBindViewHolder(holder, position, payloads)
//        val tag = tags[position]
//        Log.w("Simone", "payload bind ${tag.name} in card ${tag.media_count} " + System.identityHashCode(holder.view))
//    }
}
