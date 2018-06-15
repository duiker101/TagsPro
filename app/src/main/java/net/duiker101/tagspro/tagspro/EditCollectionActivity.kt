package net.duiker101.tagspro.tagspro

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_edit_collection.*


class EditCollectionActivity : AppCompatActivity() {
    val tags = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_collection)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tags_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tags.clear()
                tags.addAll(s.toString().split(" ").filter { it.isNotEmpty() })
                tags_text.setHelperText(getString(R.string.tags_count, tags.size))
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_collection, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}