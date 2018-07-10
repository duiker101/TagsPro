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
import net.duiker101.tagspro.tagspro.api.ExpansionPersistance
import net.duiker101.tagspro.tagspro.api.Tag
import net.duiker101.tagspro.tagspro.api.TagCollection
import net.duiker101.tagspro.tagspro.api.TagPersistance
import net.duiker101.tagspro.tagspro.events.ReloadEvent
import net.duiker101.tagspro.tagspro.events.TagEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


open class TagCollectionsFragment : Fragment() {

    companion object {
        fun newInstance(defaultMsg: String): TagCollectionsFragment {
            val args = Bundle()
            args.putString("msg", defaultMsg)
            val fragment = TagCollectionsFragment()
            fragment.arguments = args
            return fragment
        }
    }


    val collections = ArrayList<TagCollection>()
    lateinit var adapter: RecyclerView.Adapter<*>
    var defaultMsg = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private lateinit var viewManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments?.containsKey("msg") == true){
            defaultMsg = arguments!!.getString("msg")
        }

        loadTags()

        viewManager = LinearLayoutManager(activity)
        viewManager.recycleChildrenOnDetach = true
        recycler.layoutManager = viewManager

        adapter = TagCollectionsAdapter(context!!, collections,defaultMsg)
        recycler.adapter = adapter

        // this listeners makes hide and shows the FAB when the recycler is scrolled
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    (activity as MainActivity).setFabVisibility(View.GONE)
                } else if (dy < 0) {
                    (activity as MainActivity).setFabVisibility(View.VISIBLE)
                }
            }
        })
    }

    open fun loadTags() {
        collections.clear()
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
        updateCollectionsForTag(tag)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnMessageEvent(event: ReloadEvent) {
        loadTags()
        updateCollectionsSelection()
    }

    /**
     * Given a collection, set the status of the tags based on if they are in the activeTags
     * This does not actually select/deselect them, just update their status
     * This also does NOT update the adapter YET
     * TODO update the adapter with a status flag added as parameter
     */
    fun updateCollectionSelection(collection: TagCollection) {
        val activeTags = (activity as MainActivity).getActiveTags()
        val activeTagsMap = activeTags.map { it.name }
        collection.tags.forEach {
            it.active = activeTagsMap.contains(it.name)
        }
    }

    /**
     * set the status of the all the tags in all collections based on if they are in the activeTags
     * This does not actually select/deselect them, just update their status
     * WARNING this might be slow to re-render because it calls notifyDataSetChanged
     */
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

    /**
     * This sets the status for all tags of the same tag passed as parameter
     * This is useful because it updates just those adapters that have that specific tag
     */
    fun updateCollectionsForTag(tag: Tag) {
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
            }
            if (toUpdate)
                adapter.notifyItemChanged(i, ci)

            toUpdate = false
            i++
        }
    }

    /**
     * Add a collection to the adapter
     */
    fun addCollection(collection: TagCollection) {
        collections.add(collection)
        updateCollectionSelection(collection)
        adapter.notifyItemInserted(collections.size - 1)
    }

    fun collapseAll() {
        if (context != null) {
            ExpansionPersistance.collapseAll(context!!)
            adapter.notifyDataSetChanged()
        }
    }
}

