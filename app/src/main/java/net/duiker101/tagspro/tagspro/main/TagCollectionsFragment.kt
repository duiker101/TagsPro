package net.duiker101.tagspro.tagspro.main

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_tag_collection.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import net.duiker101.tagspro.tagspro.MainActivity
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.api.Tag
import net.duiker101.tagspro.tagspro.api.TagCollection
import net.duiker101.tagspro.tagspro.api.TagPersistance
import net.duiker101.tagspro.tagspro.events.TagEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


open class TagCollectionsFragment : Fragment() {

    val collections = ArrayList<TagCollection>()
    lateinit var adapter: RecyclerView.Adapter<*>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private lateinit var viewManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadTags()

        viewManager = LinearLayoutManager(activity)
        viewManager.recycleChildrenOnDetach = true
        recycler.layoutManager = viewManager

        adapter = TagCollectionsAdapter(context!!, collections)
        recycler.adapter = adapter
    }

    open fun loadTags() {
        collections.addAll(TagPersistance.load(activity as Context))
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnMessageEvent(event: TagEvent) {
        val tag = event.tag
        updateTag(tag)
    }

    fun updateCollectionSelection(collection: TagCollection) {
        val activeTags = (activity as MainActivity).getActiveTags()
        val activeTagsMap = activeTags.map { it.name }
        collection.tags.forEach {
            it.active = activeTagsMap.contains(it.name)
        }
    }

    fun updateCollectionsSelection() {
        val activeTags = (activity as MainActivity).getActiveTags()
        val activeTagsMap = activeTags.map { it.name }
        collections.forEach {
            it.tags.forEach {
                it.active = activeTagsMap.contains(it.name)
            }
        }
        adapter.notifyDataSetChanged()
    }

    fun updateTag(tag: Tag) {
        var i = 0
        var toUpdate = false
        collections.forEachIndexed { ci, collection ->
            collection.tags.forEachIndexed { ti, t ->
                if (t.name == tag.name) {
                    t.active = tag.active

                    val childAdapter = viewManager.findViewByPosition(ci)?.recycler?.adapter
                    if (childAdapter != null)
                        childAdapter.notifyItemChanged(ti)
                    else
                        toUpdate = true
                }

                if (toUpdate)
                    adapter.notifyItemChanged(i, ci)

                toUpdate = false
            }
            i++
        }
    }

    fun addCollection(collection: TagCollection) {
        collections.add(collection)
        updateCollectionSelection(collection)
        adapter.notifyItemInserted(collections.size - 1)
    }
}

