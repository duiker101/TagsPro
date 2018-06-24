package net.duiker101.tagspro.tagspro.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.api.InstgramApi
import net.duiker101.tagspro.tagspro.api.TagCollection


class SearchTagsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val collections = ArrayList<TagCollection>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_search, container, false)


        // not this line maybe
//        collections.addAll(TagPersistance.load(activity as Context))

//        viewManager = LinearLayoutManager(activity)
//        viewAdapter = TagCollectionsAdapter(context!!,collections, { }, { viewAdapter.notifyDataSetChanged() })
//        recyclerView = rootView.findViewById<RecyclerView>(R.id.my_recycler_view).apply {
//            layoutManager = viewManager
//            adapter = viewAdapter
//        }

        return rootView
    }

    fun search(text: String) {
        if (text.isEmpty() || text.length < 3)
            return
        val subscription = InstgramApi.search(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    collections.clear()
                    var collection = TagCollection("Tags 1", "", true)
                    var i = 1
                    var count = 1


                    it.hashtags.forEach {
                        if (i % 10 == 0) {
                            collections.add(collection)
                            collection = TagCollection("Tags $count", "", true)
                            count++
                        }

                        collection.tags.add(it.hashtag)
                        i++
                    }

                    viewAdapter.notifyDataSetChanged()
                }
    }
}