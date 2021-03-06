package net.duiker101.tagspro.tagspro.main

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.content_tag_collection.view.*
import net.duiker101.tagspro.tagspro.EditCollectionActivity
import net.duiker101.tagspro.tagspro.MainActivity
import net.duiker101.tagspro.tagspro.MainActivity.Companion.REQUEST_EDIT_COLLECTION
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.TagsText
import net.duiker101.tagspro.tagspro.api.ExpansionPersistance
import net.duiker101.tagspro.tagspro.api.TagCollection
import net.duiker101.tagspro.tagspro.api.TagPersistance
import net.duiker101.tagspro.tagspro.events.TagEvent
import net.duiker101.tagspro.tagspro.tags.TagsAdapter
import org.greenrobot.eventbus.EventBus

class CollectionHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(collection: TagCollection, position: Int, adapter: TagCollectionsAdapter) {
        val tags = collection.tags

        view.recycler.swapAdapter(TagsAdapter("", tags), true)

        view.title_text.text = collection.name

        // TODO clone collection button
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

        view.action_deselect.setOnClickListener {
            tags.forEach {
                it.active = false
                EventBus.getDefault().post(TagEvent(it))
            }
        }

        view.action_add.setOnClickListener {
            //            tags.forEach {
//                it.active = false
//                EventBus.getDefault().post(TagEvent(it))
//            }
//            val t = EditText(view.context)
            val builder = AlertDialog.Builder(view.context).create()
            val alertView = LayoutInflater.from(view.context).inflate(R.layout.content_alert_quickadd, null)
            val text = alertView.findViewById<EditText>(R.id.edit_text)
            val tagsText = TagsText()
            text.setText(tags.map { it.name }.joinToString(" ") )
            tagsText.update(text.text.toString())

            text.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    tagsText.update(s.toString())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            builder.setView(alertView)
            builder.setTitle("Quick add tags")
            builder.setButton(AlertDialog.BUTTON_POSITIVE, "Save") { dialog, which ->
                (view.context as MainActivity).editCollection(collection.id, collection.name, tagsText.hashed())

            }
//            builder.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ -> }
            builder.show()
        }

        val expanded = ExpansionPersistance.isExpanded(view.context, collection.id)
        if (expanded)
            view.recycler.visibility = View.VISIBLE
        else
            view.recycler.visibility = View.GONE
//
        view.action_toggle_expand.setOnClickListener {
            ExpansionPersistance.setExpansion(view.context, collection.id, !expanded)
//            collection.expanded = !collection.expanded
//            TagPersistance.save(view.context, collections)
            adapter.notifyItemChanged(position, collection)
        }
//
        view.action_overflow.setOnClickListener {
            val popup = PopupMenu(view.context, view.action_overflow)

            popup.menuInflater.inflate(R.menu.menu_collection, popup.menu)
            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_delete) {
                    adapter.collections.filter { it.id == collection.id }.forEach {
                        it.tags.forEach {
                            it.active = false
                        }
                    }

                    adapter.collections.removeAll { it.id == collection.id }
                    TagPersistance.save(view.context, adapter.collections)
                    adapter.notifyDataSetChanged()
                }
//                if (it.itemId == R.id.action_delete_unselected) {
//                    collections.filter { it.id == collection.id }.forEach {
//                        it.hashtags.forEach {
//                            it.active = false
//                            listener(it)
//                        }
//                    }
////                    collections.removeAll { it.id == collection.id }
//                    TagPersistance.save(context, collections)
////                    notifyListener()
//                }
                if (it.itemId == R.id.action_edit) {
                    val result = StringBuilder()
                    tags.forEach {
                        result.append("${it.name} ")
                    }

                    val intent = Intent(view.context, EditCollectionActivity::class.java)
                    intent.putExtra("id", collection.id)
                    intent.putExtra("title", collection.name)
                    intent.putExtra("hashtags", result.toString())

                    (view.context as Activity).startActivityForResult(intent, REQUEST_EDIT_COLLECTION)
                }
                true
            }

            popup.show()
        }
    }
}

class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
