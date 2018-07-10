package net.duiker101.tagspro.tagspro.api

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


object TagPersistance {
    fun getFile(context: Context): File {
        val path = context.filesDir
        return File(path, "hashtags.json")
    }

    fun load(context: Context): ArrayList<TagCollection> {
        val file = getFile(context)
        if (!file.exists())
            return ArrayList()

        val input = FileInputStream(file).bufferedReader().use { it.readText() }
        val type = object : TypeToken<ArrayList<TagCollection>>() {}.type
        return Gson().fromJson(input, type)
    }

    fun save(context: Context, collections: ArrayList<TagCollection>) {
        val file = getFile(context)
        val json = Gson().toJson(collections)
        FileOutputStream(file).use {
            it.write(json.toByteArray())
        }
    }
}

object ExpansionPersistance {

    /**
     * We keep this as a singleton because booleans are not mutable so if we just passed the value
     * to the collectionViewHolder we would not be able to easily change it there
     */
    private val expansions = HashMap<String, Boolean>()
    private var loaded = false

    fun isExpanded(context: Context, id: String): Boolean {
        if (!loaded)
            expansions.putAll(load(context))

        if (expansions.contains(id)) {
            val expansion = expansions[id]
            if (expansion != null)
                return expansion
        }

        // default is expanded
        return true
    }

    fun setExpansion(context: Context, id: String, expanded: Boolean) {
        if (!loaded)
            expansions.putAll(load(context))

        expansions[id] = expanded
        save(context, expansions)
    }


    private fun getFile(context: Context): File {
        val path = context.filesDir
        return File(path, "expansion.json")
    }

    private fun load(context: Context): HashMap<String, Boolean> {
        val file = getFile(context)
        if (!file.exists())
            return HashMap()

        loaded = true
        val input = FileInputStream(file).bufferedReader().use { it.readText() }
        val type = object : TypeToken<HashMap<String, Boolean>>() {}.type
        return Gson().fromJson(input, type)
    }

    private fun save(context: Context, expansions: HashMap<String, Boolean>) {
        val file = getFile(context)
        val json = Gson().toJson(expansions)
        FileOutputStream(file).use {
            it.write(json.toByteArray())
        }
    }

    fun collapseAll(context: Context) {
        expansions.keys.forEach { expansions[it] = false }
        save(context, expansions)
    }
}