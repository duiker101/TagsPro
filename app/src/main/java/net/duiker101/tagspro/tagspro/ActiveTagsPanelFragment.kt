package net.duiker101.tagspro.tagspro

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_active_tags_bar.*
import net.duiker101.tagspro.tagspro.NewMainActivity.Companion.REQUEST_CREATE_COLLECTION
import net.duiker101.tagspro.tagspro.api.Tag
import net.duiker101.tagspro.tagspro.events.TagEvent
import net.duiker101.tagspro.tagspro.tags.TagsAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ActiveTagsPanelFragment : Fragment() {

    val activeTags = ArrayList<Tag>()
    private lateinit var activeTagsAdapter: TagsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_active_tags_bar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activeTagsAdapter = TagsAdapter(getString(R.string.default_no_tag_in_group), activeTags)

        val layoutManager = FlexboxLayoutManager(activity)
        layoutManager.flexDirection = FlexDirection.ROW
        recycler.layoutManager = layoutManager
        recycler.adapter = activeTagsAdapter

        action_copy.setOnClickListener {
            copyActiveTags()
        }

        action_shuffle.setOnClickListener {
            activeTags.shuffle()
            activeTagsAdapter.notifyDataSetChanged()
        }

        action_deselect.setOnClickListener {
            ArrayList(activeTags).forEach {
                it.active = false
                EventBus.getDefault().post(TagEvent(it))
            }
            activeTagsAdapter.notifyDataSetChanged()
        }

        action_save.setOnClickListener {
            val intent = Intent(context, EditCollectionActivity::class.java)

            val result = StringBuilder()
            activeTags.forEach {
                result.append("${it.name} ")
            }

            intent.putExtra("hashtags", result.toString())

            activity?.startActivityForResult(intent, REQUEST_CREATE_COLLECTION)
        }

        active_tags_text.text = getString(R.string.active_tags_count, activeTags.size)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnMessageEvent(event: TagEvent) {
        val tag = event.tag
        if (tag.active) {
            if (activeTags.count { it.name == tag.name } == 0) {
                if (activeTags.size == 0)
                    (activity as NewMainActivity).setBottomBarState(BottomSheetBehavior.STATE_EXPANDED)
                activeTags.add(tag)
            }
        } else {
            activeTags.removeAll { it.name == tag.name }
        }

        active_tags_text.text = getString(R.string.active_tags_count, activeTags.size)
        activeTagsAdapter.notifyDataSetChanged()
    }

    private fun copyActiveTags() {
        if (activeTags.size == 0) {
            Snackbar.make(bottomSheet, getString(R.string.no_tags_selected), Snackbar.LENGTH_SHORT).show()
            return
        }

        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val copy = ArrayList<Tag>(activeTags)
        val result = StringBuilder()

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (preferences.getBoolean("shuffle_on_copy", false))
            copy.shuffle()

        val dots = preferences.getInt("dots_before_tags", 0)
        for (x in 0..(dots - 1))
            result.appendln(".")

        val onePerLine = preferences.getBoolean("one_tag_per_line", false)
        val spaceBetween = preferences.getBoolean("enable_space", true)

        copy.forEach {
            if (onePerLine)
                result.appendln(it.name)
            else if (spaceBetween)
                result.append("${it.name} ")
            else
                result.append(it.name)
        }

        val clip = ClipData.newPlainText("hashtags", result.toString())
        clipboard.primaryClip = clip

        val snack = Snackbar.make(bottomSheet, getString(R.string.copy_successful, activeTags.size), Snackbar.LENGTH_SHORT)
        snack.setAction(R.string.open_instagram) {
            val launchIntent = activity?.packageManager?.getLaunchIntentForPackage("com.instagram.android")
            if (launchIntent != null) {
                startActivity(launchIntent)//null pointer check in case package name was not found
            }
        }
        // to have the snackbar on top
//            val view = snack.view
//            val params = view.layoutParams as FrameLayout.LayoutParams
//            params.gravity = Gravity.TOP
//            view.layoutParams = params
        snack.show()
    }
}