package net.duiker101.tagspro.tagspro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
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
                updateTags(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        if (intent.hasExtra("tags")) {

            tags_text.setText(intent.getStringExtra("tags"))
        }

        if (intent.hasExtra("title")) {
            title_text.setText(intent.getStringExtra("title"))
        }

        symbol_underscore.setOnClickListener {
            tags_text.text.insert(tags_text.selectionStart, "_")
        }
        symbol_dot.setOnClickListener {
            tags_text.text.insert(tags_text.selectionStart, ".")
        }
        symbol_at.setOnClickListener {
            tags_text.text.insert(tags_text.selectionStart, "@")
        }
        symbol_hash.setOnClickListener {
            tags_text.text.insert(tags_text.selectionStart, "#")
        }
    }

    fun updateTags(tagsText: String) {
        tags.clear()
        tags.addAll(tagsText.split(" ").filter { it.isNotEmpty() })
        tags_text.setHelperText(getString(R.string.tags_count, tags.size))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_collection, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save) {
            val result = ArrayList(tags.map {
                if (it.indexOf("#") < 0 && it.indexOf("@") < 0) "#$it" else it
            })
            val resultIntent = Intent()
            if (intent.hasExtra("id")) {
                resultIntent.putExtra("id", intent.getStringExtra("id"))
            }
            resultIntent.putStringArrayListExtra("tags", result)
            resultIntent.putExtra("title", title_text.text.toString())
            setResult(Activity.RESULT_OK, resultIntent)

            // we first need to hide the keyboard or we will mess with the bar in the previous screen
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0)

            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}