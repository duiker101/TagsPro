package net.duiker101.tagspro.tagspro.main

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.content_tag_collection.view.*
import net.duiker101.tagspro.tagspro.api.TagCollection
import net.duiker101.tagspro.tagspro.events.TagEvent
import net.duiker101.tagspro.tagspro.tags.TagsAdapter
import org.greenrobot.eventbus.EventBus

class CollectionHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(collection: TagCollection, position:Int, adapter:TagCollectionsAdapter) {
        val tags = collection.tags

        view.recycler.swapAdapter(TagsAdapter("", tags), true)

        view.title_text.text = collection.name

        // TODO clone collectino button

        if (tags.size == 0) {
            view.action_select.visibility = View.GONE
            view.action_deselect.visibility = View.GONE
        } else {
            view.action_select.visibility = View.VISIBLE
            view.action_deselect.visibility = View.VISIBLE
        }
//
        view.action_select.setOnClickListener {
            tags.forEach {
                it.active = true
                EventBus.getDefault().post(TagEvent(it))
            }
        }

        view.action_shuffle.setOnClickListener {
            tags.forEach {
                it.active = ((Math.random() * 1000).toInt() + 1) % 2 == 0
                EventBus.getDefault().post(TagEvent(it))
            }
        }
//
        view.action_deselect.setOnClickListener {
            tags.forEach {
                it.active = false
                EventBus.getDefault().post(TagEvent(it))
            }
        }
//
//
        if (collection.expanded)
            view.recycler.visibility = View.VISIBLE
        else
            view.recycler.visibility = View.GONE
//
        view.action_toggle_expand.setOnClickListener {
            collection.expanded = !collection.expanded
//            TagPersistance.save(view.context, collections)
            adapter.notifyItemChanged(position, collection)
//            if (holder.recycler.visibility == View.VISIBLE)
//                holder.recycler.visibility = View.GONE
//            else
//                holder.recycler.visibility = View.VISIBLE
        }
//
//        val context = holder.view.context
//        holder.overflow.setOnClickListener {
//            val popup = PopupMenu(context, holder.overflow)
//
//            popup.menuInflater.inflate(R.menu.menu_collection, popup.menu)
//            popup.setOnMenuItemClickListener {
//                if (it.itemId == R.id.action_delete) {
//                    collections.filter { it.id == collection.id }.forEach {
//                        it.tags.forEach {
//                            it.active = false
//                            listener(it)
//                        }
//                    }
//                    collections.removeAll { it.id == collection.id }
//                    TagPersistance.save(context, collections)
//                    notifyListener()
//                }
////                if (it.itemId == R.id.action_delete_unselected) {
////                    collections.filter { it.id == collection.id }.forEach {
////                        it.hashtags.forEach {
////                            it.active = false
////                            listener(it)
////                        }
////                    }
//////                    collections.removeAll { it.id == collection.id }
////                    TagPersistance.save(context, collections)
//////                    notifyListener()
////                }
//                if (it.itemId == R.id.action_edit) {
//                    val result = StringBuilder()
//                    tags.forEach {
//                        result.append("${it.name} ")
//                    }
//
//                    val intent = Intent(context, EditCollectionActivity::class.java)
//                    intent.putExtra("id", collection.id)
//                    intent.putExtra("title", collection.name)
//                    intent.putExtra("hashtags", result.toString())
//
//                    (context as Activity).startActivityForResult(intent, REQUEST_EDIT_COLLECTION)
//                }
//                true
//            }
//
//            popup.show()
//        }
    }
}
