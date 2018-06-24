package net.duiker101.tagspro.tagspro

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.duiker101.tagspro.tagspro.main.TagCollectionsFragment
import net.duiker101.tagspro.tagspro.search.SearchTagsFragment

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
    }
}

class NewMainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    val collectionsFragment = TagCollectionsFragment()
    val searchFragment = SearchTagsFragment()

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
