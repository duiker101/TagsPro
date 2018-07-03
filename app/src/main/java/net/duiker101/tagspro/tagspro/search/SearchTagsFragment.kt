package net.duiker101.tagspro.tagspro.search

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.duiker101.tagspro.tagspro.MainActivity
import net.duiker101.tagspro.tagspro.R
import net.duiker101.tagspro.tagspro.api.InstgramApi
import net.duiker101.tagspro.tagspro.api.Tag
import net.duiker101.tagspro.tagspro.api.TagCollection
import net.duiker101.tagspro.tagspro.main.TagCollectionsFragment
import java.util.*
import kotlin.collections.HashMap


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
        if (text.isEmpty() || text.length < 3)
            return

        (activity as MainActivity).dismissKeyboard()

        val subscription = InstgramApi.search(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    // TODO error
                }
                .subscribe {
                    displayResults(it.hashtags.map { it.hashtag })
                }
    }

    val categories = arrayListOf(0, 100, 1000, 10000, 50000, 100000, 500000, 1000000, 10000000, Int.MAX_VALUE)
//    val categories = arrayListOf(
//            Pair(0, 100),
//            Pair(100, 1000),
//            Pair(1000, 10000),
//            Pair(10000, 50000),
//            Pair(50000, 100000),
//            Pair(100000, 500000),
//            Pair(500000, 1000000),
//            Pair(1000000, 10000000),
//            Pair(10000000, Int.MAX_VALUE)
//    )

    fun displayResults(result: List<Tag>) {
        collections.clear()

        // init the map array
        val map = HashMap<Int, ArrayList<Tag>>()
        categories.forEach {
            map.put(it, ArrayList())
        }


        result.forEach {
            // this is important because the results of the search don't have a #
            it.name= "#${it.name}"
            map[getKeyForCount(it.media_count)]?.add(it)
        }

        map.map {
            val count = convertValueToStr(it.key)
            val title = getString(R.string.shares_count, count)
            val collection = TagCollection(title , UUID.randomUUID().toString())
            collection.order = it.key
            collection.tags.addAll(it.value)
            collections.add(collection)
        }

        collections.removeAll { it.tags.size <= 0 }
        collections.sortBy { -it.order }

        updateCollectionsSelection()

    }

    fun convertValueToStr(value: Int): String {
        var str = "" + value
        str = str.reversed()
        str = str.replace("000000", "M")
        str = str.replace("000", "K")
        str = str.reversed()
        return str
    }

    fun getKeyForCount(count: Int): Int {
        for (i in 0 until categories.size) {
            if (count > categories[i])
                continue
            else
                return categories[i - 1]
        }

        return 0
    }
}