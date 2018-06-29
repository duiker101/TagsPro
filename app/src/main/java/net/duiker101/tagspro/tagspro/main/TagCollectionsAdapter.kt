package net.duiker101.tagspro.tagspro.main


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.content_tag_collection.view.*
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.api.TagCollection

/**
 * Adapter to show the cards that contain the Tags
 * TODO empty state for the collections adapter
 */
class TagCollectionsAdapter(
        context: Context, val collections: ArrayList<TagCollection>
) : RecyclerView.Adapter<CollectionHolder>() {

    /**
     * this viewPool contains the TagViews that can be shared between the cards
     */
    private val viewPool = RecyclerView.RecycledViewPool()

    init {

        /**
         * This following lines can help by pre-filling the viewpool with views ready for use
         */
//        val tagsAdapter = TagsAdapter("", ArrayList())
//        val view = LinearLayout(context)
//        viewPool.setMaxRecycledViews(TagsAdapter.TYPE_TAG, 200)
//        for (i in 0..200)
//            viewPool.putRecycledView(tagsAdapter.createViewHolder(view, TagsAdapter.TYPE_TAG))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.listitem_tag_collection, parent, false)

        val layoutManager = FlexboxLayoutManager(parent.context).apply {
            flexDirection = FlexDirection.ROW
        }

        view.recycler.apply {
            this.layoutManager = layoutManager
        }

        view.recycler.recycledViewPool = viewPool

        return CollectionHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionHolder, position: Int) {
        val collection = collections[position]
        holder.bind(collection, position, this)
    }

    override fun getItemCount() = collections.size
}

