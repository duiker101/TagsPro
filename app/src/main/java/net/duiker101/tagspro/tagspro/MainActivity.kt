package net.duiker101.tagspro.tagspro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main_toolbar.*
import net.duiker101.tagspro.tagspro.api.Tag
import net.duiker101.tagspro.tagspro.api.TagCollection
import net.duiker101.tagspro.tagspro.api.TagPersistance
import net.duiker101.tagspro.tagspro.events.TagEvent
import net.duiker101.tagspro.tagspro.main.TagCollectionsFragment
import net.duiker101.tagspro.tagspro.search.SearchTagsFragment
import org.greenrobot.eventbus.EventBus
import java.util.*


/**
 * Entry point of the app
 */
class MainActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: MainPagerAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    /**
     * Constants for invoking the edit tags screen
     */
    companion object {
        const val REQUEST_CREATE_COLLECTION = 0
        const val REQUEST_EDIT_COLLECTION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this, "ca-app-pub-2480387246992720~6540467205")

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        tabLayout.addTab(tabLayout.newTab().setText("Tags"))
        tabLayout.addTab(tabLayout.newTab().setText("Explore"))
        tabLayout.setupWithViewPager(pager)

        fab.setOnClickListener {
            startActivityForResult(Intent(this, EditCollectionActivity::class.java), REQUEST_CREATE_COLLECTION)
        }

        pagerAdapter = MainPagerAdapter(supportFragmentManager, getString(R.string.tags_collection_default_msg), "")
        pager.adapter = pagerAdapter
        pager.addOnPageChangeListener(BasePagerListener { position ->
            // if we select the search hide the fab and show the search
            if (position == 1) {
                setFabVisibility(View.GONE)
                search_layout.visibility = View.VISIBLE
                if (search_text.query.isEmpty()) {
                    // if the search is empty we want to focus on it, this will bring up the keyboard
                    // so just reduce the activeTagsBar to not clutter the screen
                    search_text.isIconified = false
                    search_text.requestFocus()
                    search_text.requestFocusFromTouch()
                    setBottomBarState(BottomSheetBehavior.STATE_COLLAPSED)
                }
            } else {
                search_layout.visibility = View.GONE
                setFabVisibility(View.VISIBLE)
            }
        })

        // TODO changing this changes the filter and highlights different text in the tags
        search_text.setOnQueryTextListener(BaseQueryListener {
            pagerAdapter.searchFragment.search(it)
        })

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_wrapper)

        if (resources.getBoolean(R.bool.is_pro)) {
            adView.visibility = View.GONE
        } else {
            adView.loadAd(AdRequest.Builder().build())
        }


//        TutoShowcase.from(this)
//                .setContentView(R.layout.tutorial)
//                .on(R.id.) //a view in actionbar
//                .addCircle()
//                .withBorder()
//                .onClick(View.OnClickListener {
//                    //custom action
//                })
//                .on(R.id.swipable)
//                .displaySwipableRight()
//                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            val tags = data.getStringArrayListExtra("hashtags")
            val title = data.getStringExtra("title")

            val id = data.getStringExtra("id")

            if (requestCode == REQUEST_CREATE_COLLECTION) {
                createCollection(title, tags)
            }

            if (requestCode == REQUEST_EDIT_COLLECTION) {
                editCollection(id, title, tags)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        } else if (item.itemId == R.id.action_collapse) {
            pagerAdapter.collectionsFragment.collapseAll()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Create a collection in the persistance and notify the main collections fragment of it
     */
    private fun createCollection(title: String, tags: ArrayList<String>) {
        val collection = TagCollection(title, UUID.randomUUID().toString())
        tags.forEach { collection.tags.add(Tag(it, false)) }
        pagerAdapter.collectionsFragment.addCollection(collection)

        TagPersistance.save(this, pagerAdapter.collectionsFragment.collections)
    }

    /**
     * update a collection in the persistance and notify the main collections frag that it changed
     */
    fun editCollection(collectionId: String, title: String, tags: ArrayList<String>) {
        // if we don't collapse here there can be some problem with the bar when we come from the other
        // activity. Because the keyboard there will push the bar up
        setBottomBarState(BottomSheetBehavior.STATE_COLLAPSED)

        // select the collection with this id
        val collection = pagerAdapter.collectionsFragment.collections.first { it.id == collectionId }

        collection.name = title

        // TODO remove this?
        // this part deselects all the tags in the collection, do we really want to to this?
        collection.tags.forEach {
            it.active = false
            EventBus.getDefault().post(TagEvent(it))
        }
        collection.tags.clear()

        // add all of them back, non-selected
        tags.forEach { collection.tags.add(Tag(it, false)) }
        TagPersistance.save(this, pagerAdapter.collectionsFragment.collections)

        pagerAdapter.collectionsFragment.updateCollectionsSelection()
    }

    fun setFabVisibility(visibility: Int) {
        if (visibility == View.GONE && fab.visibility == View.VISIBLE) {
            fab.hide()
        } else if (visibility == View.VISIBLE && fab.visibility != View.VISIBLE) {
            fab.show()
        }
    }

    fun setProgressVisibility(visibility: Int) {
        progress.visibility = visibility

    }

    fun setBottomBarState(state: Int) {
        bottomSheetBehavior.state = state
    }

    fun dismissKeyboard() {

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(search_text.windowToken, 0)
    }

    fun getActiveTags(): ArrayList<Tag> {
        return (activeTagsFragment as ActiveTagsPanelFragment).activeTags
    }
}

class MainPagerAdapter(fm: FragmentManager, tagsMsg: String, searchMsg: String) : FragmentStatePagerAdapter(fm) {
    val collectionsFragment = TagCollectionsFragment.newInstance(tagsMsg)
    val searchFragment = SearchTagsFragment.newInstance(searchMsg)

    override fun getCount(): Int = 2

    override fun getItem(i: Int): Fragment {
        if (i == 0)
            return collectionsFragment
        return searchFragment
    }

    override fun getPageTitle(position: Int): CharSequence {
        if (position == 0)
            return "My Tags"
        else if (position == 1)
            return "Search"
        return ""
    }

}
