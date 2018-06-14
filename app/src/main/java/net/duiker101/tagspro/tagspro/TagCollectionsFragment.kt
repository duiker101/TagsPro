package net.duiker101.tagspro.tagspro

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TagCollectionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val collections = ArrayList<TagCollection>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_main, container, false)
        viewManager = LinearLayoutManager(activity)

        collections.apply {
            val collection = TagCollection("Collection1")
            collection.add(Tag("Tag1", false))
            collection.add(Tag("Tag2", false))
            collection.add(Tag("Tag3", false))
            add(collection)
        }
        collections.apply {
            val collection = TagCollection("Collection2")
            collection.add(Tag("Tag3", false))
            collection.add(Tag("Tag5", false))
            collection.add(Tag("Tag6", false))
            add(collection)
        }
        collections.apply {
            val collection = TagCollection("Collection2")
            collection.add(Tag("Tag7", false))
            collection.add(Tag("Tag8", false))
            collection.add(Tag("Tag9", false))
            add(collection)
        }
        collections.apply {
            val collection = TagCollection("Collection1")
            collection.add(Tag("Tag11", false))
            collection.add(Tag("Tag12", false))
            collection.add(Tag("Tag13", false))
            add(collection)
        }
        collections.apply {
            val collection = TagCollection("Collection2")
            collection.add(Tag("Tag13", false))
            collection.add(Tag("Tag15", false))
            collection.add(Tag("Tag16", false))
            add(collection)
        }
        collections.apply {
            val collection = TagCollection("Collection2")
            collection.add(Tag("Tag17", false))
            collection.add(Tag("Tag18", false))
            collection.add(Tag("Tag19", false))
            add(collection)
        }

        viewAdapter = TagCollectionsAdapter(collections)

        recyclerView = rootView.findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        return rootView
    }


    // Event bus system, when a tag gets added/remove loop all the collections to activate/deactivate that tag
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(event: TagEvent) {
        var i = 0
        collections.forEach {
            it.forEach {
                if (it.name == event.tag.name) {
                    viewAdapter.notifyItemChanged(i, it)
                    it.active = event.tag.active
                }
            }
            i++
        }
    }
}

class TagCollectionsAdapter(private val collections: ArrayList<TagCollection>) : RecyclerView.Adapter<TagCollectionsAdapter.ViewHolder>() {

    class ViewHolder(val view: View, val selectButton: ImageButton, val deselectButton: ImageButton, val adapter: TagsAdapter) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TagCollectionsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_tag_collection, parent, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = FlexboxLayoutManager(parent.context)
        layoutManager.flexDirection = FlexDirection.ROW
        recycler.layoutManager = layoutManager
        val adapter = TagsAdapter()
        recycler.adapter = adapter

        val selectButton = view.findViewById<ImageButton>(R.id.action_select)
        val deselectButton = view.findViewById<ImageButton>(R.id.action_deselect)

        return ViewHolder(view, selectButton, deselectButton, adapter)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.adapter.tags = collections[position]
        holder.adapter.notifyDataSetChanged()

        holder.selectButton.setOnClickListener {
            collections[position].forEach {
                it.active = true
                EventBus.getDefault().post(TagEvent(it))
            }
            holder.adapter.notifyDataSetChanged()
        }

        holder.deselectButton.setOnClickListener {
            collections[position].forEach {
                it.active = false
                EventBus.getDefault().post(TagEvent(it))
            }
            holder.adapter.notifyDataSetChanged()
        }
    }

    override fun getItemCount() = collections.size
}

class TagsAdapter : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {
    class ViewHolder(val view: TagView) : RecyclerView.ViewHolder(view)

    lateinit var tags: TagCollection

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TagView(parent.context, null))
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.tag = tags[position]
    }
}
