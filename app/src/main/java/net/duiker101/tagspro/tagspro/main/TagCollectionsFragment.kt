package net.duiker101.tagspro.tagspro.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_tag_collection.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.api.Tag
import net.duiker101.tagspro.tagspro.api.TagCollection
import net.duiker101.tagspro.tagspro.events.TagEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class TagCollectionsFragment : Fragment() {

    val collections = ArrayList<TagCollection>()
    lateinit var adapter: RecyclerView.Adapter<*>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private lateinit var viewManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        collections.addAll(TagPersistance.load(activity as Context))
        for (i in 0..50) {
            with(TagCollection("test$i", "$i", true)) {
                for (j in 0..50) {
                    val t = Tag("tag $j")
                    t.media_count = i
                    this.tags.add(t)
                }
                collections.add(this)
            }
        }

        viewManager = LinearLayoutManager(activity)
        viewManager.recycleChildrenOnDetach = true
        recycler.layoutManager = viewManager

//        adapter = TagCollectionsAdapter(collections, { updateTag(it, false) }, { adapter.notifyDataSetChanged() })
        adapter = TagCollectionsAdapter(context!!, collections)
        recycler.adapter = adapter
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
        updateTag(tag, false)
    }

    fun updateTag(tag: Tag, notify: Boolean) {
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

//    fun addCollection(collection: TagCollection) {
//        collections.add(collection)
////        adapter.notifyItemInserted(collections.size - 1)
//        adapter.notifyDataSetChanged()
//    }
}

