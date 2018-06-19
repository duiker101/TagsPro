package net.duiker101.tagspro.tagspro

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*


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

class TagsAdapter(private val defaultMsg: String, private val listener: (tag: Tag) -> Unit) : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    var tags = ArrayList<Tag>()

    companion object {
        const val TYPE_DEFAULT = 0
        const val TYPE_TAG = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == TYPE_DEFAULT) {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.listitem_default_text, parent, false))
        } else
            ViewHolder(TagView(parent.context, null, listener))
    }

    override fun getItemCount(): Int {
        return if (tags.size == 0) 1 else tags.size
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && tags.size == 0) TYPE_DEFAULT else TYPE_TAG
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_TAG)
            (holder.view as TagView).tag = tags[position]
        else
            (holder.view as TextView).text = defaultMsg
    }
}
