package net.duiker101.tagspro.tagspro

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_active_tags_bar.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mAdapter: MainPagerAdapter
    private lateinit var mViewPager: ViewPager
    private val activeTags = ArrayList<Tag>()
    private lateinit var activeTagsAdapter: TagsAdapter
    private lateinit var activeTagsText: TextView
    private lateinit var bottomSheet: View
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    companion object {
        const val REQUEST_CREATE_COLLECTION = 0
        const val REQUEST_EDIT_COLLECTION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val tabLayout: TabLayout = findViewById(R.id.tabs)
        tabLayout.addTab(tabLayout.newTab().setText("Tags"))
        tabLayout.addTab(tabLayout.newTab().setText("Explore"))

        fab.setOnClickListener {
            startActivityForResult(Intent(this, EditCollectionActivity::class.java), REQUEST_CREATE_COLLECTION)
        }

        activeTagsText = findViewById(R.id.active_tags_text)
        activeTagsText.text = getString(R.string.active_tags, 0)

        mAdapter = MainPagerAdapter(supportFragmentManager)
        mViewPager = findViewById(R.id.pager)
        mViewPager.adapter = mAdapter
        tabLayout.setupWithViewPager(mViewPager)

        activeTagsAdapter = TagsAdapter(getString(R.string.default_no_active_tags), { tagModified(it) })

        val recycler = findViewById<RecyclerView>(R.id.selected_tags_recycler)
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW
        recycler.layoutManager = layoutManager
        recycler.adapter = activeTagsAdapter
        activeTagsAdapter.tags = activeTags
        bottomSheet = findViewById<View>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        findViewById<ImageButton>(R.id.action_copy).setOnClickListener {
            copyActiveTags()
        }

        // TODO save as new collection button

        findViewById<ImageButton>(R.id.action_shuffle).setOnClickListener {
            activeTags.shuffle()
            activeTagsAdapter.notifyDataSetChanged()
        }

        findViewById<ImageButton>(R.id.action_deselect).setOnClickListener {
            ArrayList(activeTags).forEach {
                it.active = false
                tagModified(it)
            }
            activeTagsAdapter.notifyDataSetChanged()
        }

        action_save.setOnClickListener {
            val intent = Intent(this, EditCollectionActivity::class.java)

            val result = StringBuilder()
            activeTags.forEach {
                result.append("${it.name} ")
            }

            intent.putExtra("tags", result.toString())

            startActivityForResult(intent, REQUEST_CREATE_COLLECTION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            val tags = data.getStringArrayListExtra("tags")
            val title = data.getStringExtra("title")
            if (requestCode == REQUEST_CREATE_COLLECTION) {
                val collection = TagCollection(title, UUID.randomUUID().toString())
                tags.forEach { collection.tags.add(Tag(it, false)) }
                mAdapter.collectionsFrag.addCollection(collection)

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
                mAdapter.collectionsFrag.viewAdapter.notifyDataSetChanged()

//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun copyActiveTags() {
        if (activeTags.size == 0) {
            Snackbar.make(bottomSheet, getString(R.string.no_tags_selected), Snackbar.LENGTH_SHORT).show()
            return
        }

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val result = StringBuilder()
        activeTags.forEach {
            result.append("${it.name} ")
        }
        val clip = ClipData.newPlainText("tags", result.toString())
        clipboard.primaryClip = clip

        val snack = Snackbar.make(bottomSheet, getString(R.string.copy_successful, activeTags.size), Snackbar.LENGTH_SHORT)
        snack.setAction(R.string.open_instagram, {
            val launchIntent = packageManager.getLaunchIntentForPackage("com.instagram.android")
            if (launchIntent != null) {
                startActivity(launchIntent)//null pointer check in case package name was not found
            }
        })
        // to have the snackbar on top
//            val view = snack.view
//            val params = view.layoutParams as FrameLayout.LayoutParams
//            params.gravity = Gravity.TOP
//            view.layoutParams = params
        snack.show()
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
        activeTagsText.text = getString(R.string.active_tags, activeTags.size)
        activeTagsAdapter.notifyDataSetChanged()
        mAdapter.collectionsFrag.tagModified(tag, false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    val collectionsFrag = TagCollectionsFragment()
    val exploreTagsFragment = ExploreTagsFragment()

    override fun getCount(): Int = 2

    override fun getItem(i: Int): Fragment {
        if (i == 0)
            return collectionsFrag
        return exploreTagsFragment
    }

    override fun getPageTitle(position: Int): CharSequence {
        if (position == 0)
            return "My Tags"
        else if (position == 1)
            return "Explore"
        return ""
    }
}
