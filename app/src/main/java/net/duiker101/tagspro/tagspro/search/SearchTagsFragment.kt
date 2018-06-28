package net.duiker101.tagspro.tagspro.search

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.duiker101.tagspro.tagspro.NewMainActivity
import net.duiker101.tagspro.tagspro.api.InstgramApi
import net.duiker101.tagspro.tagspro.api.TagCollection
import net.duiker101.tagspro.tagspro.main.TagCollectionsFragment


class SearchTagsFragment : TagCollectionsFragment() {

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        return inflater.inflate(R.layout.fragment_search, container, false)
//    }

    override fun loadTags() {
    }

    fun search(text: String) {
//        swipe_refresh.isRefreshing = true
        if (text.isEmpty() || text.length < 3)
            return

//        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(search_text.windowToken, 0)
//        search_text.isIconified = false
//        search_text.clearFocus()
        (activity as NewMainActivity).dismissKeyboard()

        val subscription = InstgramApi.search(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    //                    swipe_refresh.isRefreshing = false
                }
                .subscribe {
                    //                    swipe_refresh.isRefreshing = false
                    collections.clear()
                    var collection = TagCollection("Tags 1", "")
                    var i = 1
                    var count = 1


                    it.hashtags.forEach {
                        it.hashtag.name = "#" + it.hashtag.name
                        collection.tags.add(it.hashtag)
                        i++

                        if (i % 10 == 0) {
                            collections.add(collection)
                            collection = TagCollection("Tags $count", "")
                            count++
                        }
                    }

                    adapter.notifyDataSetChanged()
                }
    }
}