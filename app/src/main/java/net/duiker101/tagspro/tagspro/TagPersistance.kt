package net.duiker101.tagspro.tagspro

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


object TagPersistance {
    private fun getFile(context: Context): File {
        val path = context.filesDir
        return File(path, "tags.json")
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