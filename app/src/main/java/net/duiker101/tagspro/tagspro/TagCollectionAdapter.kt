package net.duiker101.tagspro.tagspro


import android.app.Activity
import android.content.Intent
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import net.duiker101.tagspro.tagspro.MainActivity.Companion.REQUEST_EDIT_COLLECTION

class TagCollectionsAdapter(private val collections: ArrayList<TagCollection>,
                            private val listener: (tag: Tag) -> Unit,
                            private val notifyListener: () -> Unit)
    : RecyclerView.Adapter<TagCollectionsAdapter.ViewHolder>() {

    class ViewHolder(val view: View,
                     val title: TextView,
                     val expandButton: ImageButton,
                     val recycler: RecyclerView,
                     val selectButton: ImageButton,
                     val shuffleButton: ImageButton,
                     val deselectButton: ImageButton,
                     val overflow: ImageButton,
                     val adapter: TagsAdapter) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TagCollectionsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_tag_collection, parent, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = FlexboxLayoutManager(parent.context)
        layoutManager.flexDirection = FlexDirection.ROW
        recycler.layoutManager = layoutManager
        val adapter = TagsAdapter(parent.context.getString(R.string.default_no_tag_in_group), listener)
        recycler.adapter = adapter

        // TODO clone collectino button
        return ViewHolder(view,
                view.findViewById(R.id.title_text),
                view.findViewById(R.id.action_toggle_expand),
                recycler,
                view.findViewById(R.id.action_select),
                view.findViewById(R.id.action_shuffle),
                view.findViewById(R.id.action_deselect),
                view.findViewById(R.id.action_overflow),
                adapter)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val collection = collections[position]
        val tags = collection.tags

        holder.adapter.tags.clear()
        holder.adapter.tags.addAll(tags)
        holder.adapter.notifyDataSetChanged()

        holder.title.text = collection.name
        if (tags.size == 0) {
            holder.selectButton.visibility = View.GONE
            holder.deselectButton.visibility = View.GONE
        } else {
            holder.selectButton.visibility = View.VISIBLE
            holder.deselectButton.visibility = View.VISIBLE
        }

        holder.selectButton.setOnClickListener {
            tags.forEach {
                it.active = true
                listener(it)
            }
            holder.adapter.notifyDataSetChanged()
        }

        holder.shuffleButton.setOnClickListener {
            tags.forEach {
                it.active = ((Math.random() * 1000).toInt() + 1) % 2 == 0
                listener(it)
            }
            holder.adapter.notifyDataSetChanged()
        }

        holder.deselectButton.setOnClickListener {
            tags.forEach {
                it.active = false
                listener(it)
            }
            holder.adapter.notifyDataSetChanged()
        }

        if (collection.expanded)
            holder.recycler.visibility = View.VISIBLE
        else
            holder.recycler.visibility = View.GONE

        holder.expandButton.setOnClickListener {
            collection.expanded = !collection.expanded
            TagPersistance.save(holder.view.context, collections)
            notifyItemChanged(position, collection)
//            if (holder.recycler.visibility == View.VISIBLE)
//                holder.recycler.visibility = View.GONE
//            else
//                holder.recycler.visibility = View.VISIBLE
        }

        val context = holder.view.context
        holder.overflow.setOnClickListener {
            val popup = PopupMenu(context, holder.overflow)

            popup.menuInflater.inflate(R.menu.menu_collection, popup.menu)
            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_delete) {
                    collections.filter { it.id == collection.id }.forEach {
                        it.tags.forEach {
                            it.active = false
                            listener(it)
                        }
                    }
                    collections.removeAll { it.id == collection.id }
                    TagPersistance.save(context, collections)
                    notifyListener()
                }
//                if (it.itemId == R.id.action_delete_unselected) {
//                    collections.filter { it.id == collection.id }.forEach {
//                        it.tags.forEach {
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

                    val intent = Intent(context, EditCollectionActivity::class.java)
                    intent.putExtra("id", collection.id)
                    intent.putExtra("title", collection.name)
                    intent.putExtra("tags", result.toString())

                    (context as Activity).startActivityForResult(intent, REQUEST_EDIT_COLLECTION)
                }
                true
            }

            popup.show()
        }
    }

    override fun getItemCount() = collections.size
}

