package net.duiker101.tagspro.tagspro.main

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.content_tag_collection.view.*
import net.duiker101.tagspro.tagspro.api.Tag
import net.duiker101.tagspro.tagspro.api.TagCollection
import net.duiker101.tagspro.tagspro.tags.TagsAdapter

class CollectionHolder(val view: View, val listener: (tag: Tag) -> Unit) : RecyclerView.ViewHolder(view) {
    fun bind(collection: TagCollection) {
        val tags = collection.tags

//        adapter.tags.clear()
//        adapter.tags.addAll(tags)
//        adapter.notifyDataSetChanged()
//        view.recycler.adapter = TagsAdapter("", tags, {})
        view.recycler.swapAdapter(TagsAdapter("", tags, listener), true)

        view.title_text.text = collection.name
    }
}
