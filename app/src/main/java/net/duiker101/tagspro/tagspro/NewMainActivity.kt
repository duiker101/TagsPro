package net.duiker101.tagspro.tagspro

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import net.duiker101.tagspro.tagspro.main.TagCollectionsFragment

class NewMainActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: NewMainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        tabLayout.addTab(tabLayout.newTab().setText("Tags"))
        tabLayout.addTab(tabLayout.newTab().setText("Explore"))
        tabLayout.setupWithViewPager(pager)

        pagerAdapter = NewMainPagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 1) {
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
