package net.duiker101.tagspro.tagspro.main

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import net.duiker101.tagspro.tagspro.MainActivity
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.tags.Tag
import net.duiker101.tagspro.tagspro.tags.TagCollection
import net.duiker101.tagspro.tagspro.tags.TagPersistance


class TagCollectionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    val collections = ArrayList<TagCollection>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_main, container, false)
        viewManager = LinearLayoutManager(activity)
        viewAdapter = TagCollectionsAdapter(collections, { tagModified(it, true) }, { viewAdapter.notifyDataSetChanged() })

        collections.addAll(TagPersistance.load(activity as Context))

        recyclerView = rootView.findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && activity?.fab?.visibility == View.VISIBLE) {
                    activity?.fab?.hide();
                } else if (dy < 0 && activity?.fab?.visibility != View.VISIBLE) {
                    activity?.fab?.show()
                }
            }
        })
                //RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView:RecyclerView, dx:Int,  dy:Int) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy > 0 && mFloatingActionButton.getVisibility() == View.VISIBLE) {
//                    mFloatingActionButton.hide();
//                } else if (dy < 0 && mFloatingActionButton.getVisibility() != View.VISIBLE) {
//                    mFloatingActionButton.show();
//                }
//            }
//        });

        return rootView
    }

    fun tagModified(tag: Tag, notify: Boolean) {
        var i = 0
        collections.forEach {
            it.tags.forEach {
                if (it.name == tag.name) {
                    it.active = tag.active
                    viewAdapter.notifyItemChanged(i, it)
                }
            }
            i++
        }

        if (notify)
            (activity as MainActivity).tagModified(tag)
    }

    fun addCollection(collection: TagCollection) {
        collections.add(collection)
//        viewAdapter.notifyItemInserted(collections.size - 1)
        viewAdapter.notifyDataSetChanged()
    }
}

