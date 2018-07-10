package net.duiker101.tagspro.tagspro.main


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.content_tag_collection.view.*
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.api.TagCollection

/**
 * Adapter to show the cards that contain the Tags
 */
class TagCollectionsAdapter(
        context: Context,
        val collections: ArrayList<TagCollection>,
        private val defaultMsg: String
        ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * this viewPool contains the TagViews that can be shared between the cards
     */
    private val viewPool = RecyclerView.RecycledViewPool()

    companion object {
        const val TYPE_DEFAULT = 614
        const val TYPE_COLLECTION = 615
    }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_COLLECTION) {
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
        } else {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.listitem_default_text, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_COLLECTION) {
            val collection = collections[position]
            (holder as CollectionHolder).bind(collection, position, this)
        } else if (holder.itemViewType == TYPE_DEFAULT) {
            ((holder as ViewHolder).view as TextView).text = defaultMsg
        }
    }

    override fun getItemCount(): Int {
        return if (collections.size == 0) 1 else collections.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && collections.size == 0) TYPE_DEFAULT else TYPE_COLLECTION
    }
}

