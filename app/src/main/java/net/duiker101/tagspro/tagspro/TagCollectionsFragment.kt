package net.duiker101.tagspro.tagspro

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager

class TagCollectionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_main, container, false)
        viewManager = LinearLayoutManager(activity)

        val collections = ArrayList<TagCollection>()
        collections.apply {
            val collection = TagCollection("Collection1")
            collection.add(Tag("Tag1", false))
            collection.add(Tag("Tag2", true))
            collection.add(Tag("Tag3", false))
            add(collection)
        }
        collections.apply {
            val collection = TagCollection("Collection2")
            collection.add(Tag("Tag1", true))
            collection.add(Tag("Tag2", false))
            collection.add(Tag("Tag3", true))
            add(collection)
        }

        viewAdapter = TagCollectionsAdapter(collections)

        recyclerView = rootView.findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        return rootView
    }

}

class TagCollectionsAdapter(private val collections: ArrayList<TagCollection>) : RecyclerView.Adapter<TagCollectionsAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TagCollectionsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_tag_collection, parent, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = FlexboxLayoutManager(parent.context)
        layoutManager.flexDirection = FlexDirection.ROW
        recycler.layoutManager = layoutManager
        val adapter = TagsAdapter()
        recycler.adapter = adapter

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount() = collections.size
}

class TagsAdapter : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {
    class ViewHolder(val view: TagView) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TagView(parent.context, null))
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.text = "Test $position"
    }
}
