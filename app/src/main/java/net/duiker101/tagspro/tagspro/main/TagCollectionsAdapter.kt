package net.duiker101.tagspro.tagspro.main


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.content_tag_collection.view.*
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.api.Tag
import net.duiker101.tagspro.tagspro.api.TagCollection
import net.duiker101.tagspro.tagspro.tags.TagsAdapter

class TagCollectionsAdapter(private val context: Context, private val collections: ArrayList<TagCollection>,
                            private val listener: (tag: Tag) -> Unit,
                            private val notifyListener: () -> Unit)
    : RecyclerView.Adapter<CollectionHolder>() {

    val viewPool = RecyclerView.RecycledViewPool()

    init {
        viewPool.setMaxRecycledViews(3, 300)
        viewPool.setMaxRecycledViews(3, 300)

        val tagsAdapter = TagsAdapter("", ArrayList(), listener)
        val view = LinearLayout(context)
        for (i in 0..200)
            viewPool.putRecycledView(tagsAdapter.createViewHolder(view,3))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionHolder {

        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.listitem_tag_collection, parent, false)
        Log.w("Simone", "init collection")

        val layoutManager = FlexboxLayoutManager(parent.context).apply {
            flexDirection = FlexDirection.ROW
//            collectInitialPrefetchPositions(5, RecyclerView.LayoutManager.LayoutPrefetchRegistry())
        }

//        view.recycler.setItemViewCacheSize(100)
//        view.recycler.setHasFixedSize(true)
//        view.recycler.
        view.recycler.apply {
            this.layoutManager = layoutManager
//            this.adapter = adapter
        }

        view.recycler.recycledViewPool = viewPool

        // TODO clone collectino button
        return CollectionHolder(view, listener)
    }

    override fun onBindViewHolder(holder: CollectionHolder, position: Int) {
        val collection = collections[position]
        holder.bind(collection)
        Log.w("Simone", "bind collection $position")
    }

    override fun getItemCount() = collections.size

//    override fun getItemId(position: Int): Long {
//        return collections[position].id.toLong()
//    }
}

