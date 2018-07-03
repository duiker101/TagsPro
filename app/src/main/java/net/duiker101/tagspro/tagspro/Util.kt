package net.duiker101.tagspro.tagspro

import android.support.v4.view.ViewPager
import android.support.v7.widget.SearchView


/**
 * Base class so I don't have to redefine this stuff in the Main Activity for now
 * Remove the listener and just override to implement the other methods to use them
 */
open class BasePagerListener(private val listener: (Int) -> Unit) : ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        listener(position)
    }
}

/**
 * Same as BasePageListener but for SearchView
 */
open class BaseQueryListener(private val listener: (String) -> Unit) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null && !query.isEmpty()) {
            listener(query)
            return true
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }
}

class TagsText : ArrayList<String>() {
    fun update(text: String) {
        clear()
        addAll(text.split(" ").filter { it.isNotEmpty() })

    }

    fun hashed(): ArrayList<String> {
        return ArrayList(this.map {
            if (it.indexOf("#") < 0 && it.indexOf("@") < 0) "#$it" else it
        })
    }

//    fun hashedText():String{
//        this.map {
//            if (it.indexOf("#") < 0 && it.indexOf("@") < 0) "#$it" else it
//        }.flatMap {  }
//    }
}