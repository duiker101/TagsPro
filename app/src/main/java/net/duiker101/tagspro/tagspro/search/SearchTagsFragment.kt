package net.duiker101.tagspro.tagspro.search

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.duiker101.tagspro.tagspro.NewMainActivity
import net.duiker101.tagspro.tagspro.api.InstgramApi
import net.duiker101.tagspro.tagspro.api.TagCollection
import net.duiker101.tagspro.tagspro.main.TagCollectionsFragment


class SearchTagsFragment : TagCollectionsFragment() {

    /**
     * We override this to stop the saved tags from loading
     */
    override fun loadTags() {
    }

    /**
     * handle the search from the SearchView in the toolbar of the MainActivity
     *
     */
    fun search(text: String) {
//        swipe_refresh.isRefreshing = true
        if (text.isEmpty() || text.length < 3)
            return

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

                    updateCollectionsSelection()
                }
    }
}