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
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_active_tags_bar.*
import net.duiker101.tagspro.tagspro.api.Tag
import net.duiker101.tagspro.tagspro.api.TagCollection
import net.duiker101.tagspro.tagspro.api.TagPersistance
import net.duiker101.tagspro.tagspro.main.TagCollectionsFragment
import net.duiker101.tagspro.tagspro.search.SearchTagsFragment
import net.duiker101.tagspro.tagspro.tags.TagsAdapter
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mAdapter: MainPagerAdapter
    private lateinit var pager: ViewPager
    private val activeTags = ArrayList<Tag>()
    private lateinit var activeTagsAdapter: TagsAdapter
    private lateinit var activeTagsText: TextView
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

        fab.setOnClickListener {
            startActivityForResult(Intent(this, EditCollectionActivity::class.java), REQUEST_CREATE_COLLECTION)
        }

//        activeTagsText = findViewById(R.id.active_tags_text)
//        activeTagsText.text = getString(R.string.active_tags_count, 0)

        mAdapter = MainPagerAdapter(supportFragmentManager)
        pager.adapter = mAdapter
        tabLayout.setupWithViewPager(pager)

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 1) {
                    search_text.visibility = View.VISIBLE
                    search_text.isIconified = false
                    search_text.requestFocus()
                    search_text.requestFocusFromTouch()
                } else {
                    search_text.visibility = View.GONE
                }
            }
        })

        search_text.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query !=null){
                    mAdapter.searchFragment.search(query)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        activeTagsAdapter = TagsAdapter(getString(R.string.default_no_active_tags),activeTags)

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW
        recycler.layoutManager = layoutManager
        recycler.adapter = activeTagsAdapter
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            val tags = data.getStringArrayListExtra("hashtags")
            val title = data.getStringExtra("title")
            if (requestCode == REQUEST_CREATE_COLLECTION) {
                val collection = TagCollection(title, UUID.randomUUID().toString())
                tags.forEach { collection.tags.add(Tag(it, false)) }
                // TODO
//                mAdapter.collectionsFrag.addCollection(collection)

                TagPersistance.save(this, mAdapter.collectionsFrag.collections)
            }
            if (requestCode == REQUEST_EDIT_COLLECTION) {
                // if we don't collapse here there can be some problem with the bar
                val id = data.getStringExtra("id")
                val collection = mAdapter.collectionsFrag.collections.first { it.id == id }
                collection.name = title
                collection.tags.forEach {
                    it.active = false
                    tagModified(it)
                }
                collection.tags.clear()

                tags.forEach { collection.tags.add(Tag(it, false)) }
                TagPersistance.save(this, mAdapter.collectionsFrag.collections)
                // TODO
//                mAdapter.collectionsFrag.viewAdapter.notifyDataSetChanged()

//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }


    fun tagModified(tag: Tag) {
        if (tag.active) {
            if (activeTags.size == 0 && bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            if (activeTags.count { it.name == tag.name } == 0) {
                activeTags.add(tag)
            }
        } else {
            activeTags.removeAll { it.name == tag.name }
            if (activeTags.size == 0 && bottomSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        activeTagsText.text = getString(R.string.active_tags_count, activeTags.size)
        activeTagsAdapter.notifyDataSetChanged()
        // TODO
//        mAdapter.collectionsFrag.updateTag(tag, false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> {
//
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
        if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    val collectionsFrag = TagCollectionsFragment()
    //    val exploreTagsFragment = ExploreTagsFragment()
    val searchFragment = SearchTagsFragment()

    override fun getCount(): Int = 2

    override fun getItem(i: Int): Fragment {
        if (i == 0)
            return collectionsFrag
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
