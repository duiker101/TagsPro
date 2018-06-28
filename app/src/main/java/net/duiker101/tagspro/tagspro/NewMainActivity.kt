package net.duiker101.tagspro.tagspro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import net.duiker101.tagspro.tagspro.api.Tag
import net.duiker101.tagspro.tagspro.api.TagCollection
import net.duiker101.tagspro.tagspro.api.TagPersistance
import net.duiker101.tagspro.tagspro.main.TagCollectionsFragment
import java.util.*

class NewMainActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: NewMainPagerAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    companion object {
        const val REQUEST_CREATE_COLLECTION = 0
        const val REQUEST_EDIT_COLLECTION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        tabLayout.addTab(tabLayout.newTab().setText("Tags"))
        tabLayout.addTab(tabLayout.newTab().setText("Explore"))
        tabLayout.setupWithViewPager(pager)

        fab.setOnClickListener {
            startActivityForResult(Intent(this, EditCollectionActivity::class.java), REQUEST_CREATE_COLLECTION)
        }

        pagerAdapter = NewMainPagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 1 && search_text.query.isEmpty()) {
                    fab.visibility = View.GONE
                    search_text.visibility = View.VISIBLE
                    search_text.isIconified = false
                    search_text.requestFocus()
                    search_text.requestFocusFromTouch()
                } else {
                    search_text.visibility = View.GONE
                    fab.visibility = View.VISIBLE
                }
            }
        })

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_wrapper)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            val tags = data.getStringArrayListExtra("hashtags")
            val title = data.getStringExtra("title")
            if (requestCode == REQUEST_CREATE_COLLECTION) {
                val collection = TagCollection(title, UUID.randomUUID().toString())
                tags.forEach { collection.tags.add(Tag(it, false)) }
                pagerAdapter.collectionsFragment.addCollection(collection)

                TagPersistance.save(this, pagerAdapter.collectionsFragment.collections)
            }

//            if (requestCode == REQUEST_EDIT_COLLECTION) {
//                // if we don't collapse here there can be some problem with the bar
//                val id = data.getStringExtra("id")
//                val collection = pagerAdapter.collectionsFragment.collections.first { it.id == id }
//                collection.name = title
//                collection.tags.forEach {
//                    it.active = false
//                    tagModified(it)
//                }
//                collection.tags.clear()
//
//                tags.forEach { collection.tags.add(Tag(it, false)) }
//                TagPersistance.save(this, mAdapter.collectionsFrag.collections)
//                // TODO
////                mAdapter.collectionsFrag.viewAdapter.notifyDataSetChanged()
//
////                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            }
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
        }
        return super.onOptionsItemSelected(item)
    }

    fun setBottomBarState(state: Int) {
        bottomSheetBehavior.state = state
    }
}

class NewMainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    val collectionsFragment = TagCollectionsFragment()
    val searchFragment = TagCollectionsFragment()

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
