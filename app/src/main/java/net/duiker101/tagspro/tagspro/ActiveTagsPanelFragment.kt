package net.duiker101.tagspro.tagspro

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.duiker101.tagspro.tagspro.tags.TagsAdapter


class ActiveTagsPanelFragment : Fragment() {

    private lateinit var activeTagsAdapter: TagsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_active_tags_bar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        activeTagsAdapter = TagsAdapter(getString(R.string.default_no_tag_in_group), ArrayList(), {})
//
//        val layoutManager = FlexboxLayoutManager(activity)
//        layoutManager.flexDirection = FlexDirection.ROW
//        recycler.layoutManager = layoutManager
//        recycler.adapter = activeTagsAdapter
//
//        activeTagsAdapter.tags.add(Tag("test"))
//        activeTagsAdapter.tags.add(Tag("test"))
//        activeTagsAdapter.tags.add(Tag("test"))
    }
}