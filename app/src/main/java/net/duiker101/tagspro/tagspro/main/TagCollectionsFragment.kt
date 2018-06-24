package net.duiker101.tagspro.tagspro.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_tag_collection.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.api.Tag
import net.duiker101.tagspro.tagspro.api.TagCollection


class TagCollectionsFragment : Fragment() {

    val collections = ArrayList<TagCollection>()
    lateinit var adapter: RecyclerView.Adapter<*>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private lateinit var viewManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewManager = LinearLayoutManager(activity)
//        adapter = TagCollectionsAdapter(collections, { tagModified(it, true) }, { adapter.notifyDataSetChanged() })

//        collections.addAll(TagPersistance.load(activity as Context))
        for (i in 0..50) {
            with(TagCollection("test$i", "$i", true)) {
                for (j in 0..50) {
                    val t = Tag("tag $j")
                    t.media_count = i
                    this.tags.add(t)
                }
//                    this.tags.add(Tag("tag"))
                collections.add(this)
            }
        }

//        adapter = TagCollectionsAdapter(collections, { tagModified(it, false) }, { adapter.notifyDataSetChanged() })
//        adapter = NewTagCollectionsAdapter(collections)
        adapter = TagCollectionsAdapter(collections, { tagModified(it, false) }, { })
//        adapter = TagCollectionsAdapter(collections,{},{})

//        recycler.apply {
//            layoutManager = viewManager
//            this.adapter = adapter
//        }
//        viewManager.initialPrefetchItemCount = 2
//        viewManager.isItemPrefetchEnabled = true
//        recycler.setHasFixedSize(true)
//        adapter.setHasStableIds(true)

        viewManager.recycleChildrenOnDetach = true
        recycler.layoutManager = viewManager
        recycler.adapter = adapter


//        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (dy > 0 && activity?.fab?.visibility == View.VISIBLE) {
//                    activity?.fab?.hide();
//                } else if (dy < 0 && activity?.fab?.visibility != View.VISIBLE) {
//                    activity?.fab?.show()
//                }
//            }
//        })
    }

    fun tagModified(tag: Tag, notify: Boolean) {
        var i = 0
        var toUpdate = false
        collections.forEachIndexed { ci, collection ->
            collection.tags.forEachIndexed { ti, t ->
                if (t.name == tag.name) {
                    t.active = tag.active
//                    adapter.view
                    Log.w("Simone", "Notify card $ci element $ti")
                    val childAdapter = viewManager.findViewByPosition(ci)?.recycler?.adapter
                    if (childAdapter != null)
                        childAdapter.notifyItemChanged(ti)
                    else
                        toUpdate = true
//                    viewManager.findViewByPosition(ci)?.recycler?.adapter?.notifyDataSetChanged()

//                    adapter.notifyItemChanged(ci)

//                    val tagView =viewManager.findViewByPosition(ci)?.recycler?.layoutManager?.findViewByPosition(ti)
//                    if(tagView != null)
//                        (tagView as TagView).updateBackground()


                }
                if (toUpdate)
                    adapter.notifyItemChanged(i, ci)
                toUpdate = false
            }
            i++
        }

//        adapter.notifyDataSetChanged()

//        if (notify)
//            (activity as MainActivity).tagModified(tag)
    }
//
//    fun addCollection(collection: TagCollection) {
//        collections.add(collection)
////        adapter.notifyItemInserted(collections.size - 1)
//        adapter.notifyDataSetChanged()
//    }
}

