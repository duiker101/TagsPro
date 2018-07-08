package net.duiker101.tagspro.tagspro

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceFragmentCompat
import net.duiker101.tagspro.tagspro.SettingsActivity.Companion.WRITE_PERMISSION_REQUEST_CODE
import net.duiker101.tagspro.tagspro.api.TagPersistance
import net.duiker101.tagspro.tagspro.events.ReloadEvent
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileInputStream


class SettingsActivity : AppCompatActivity() {

    private val fragment = SettingsFragment()

    companion object {
        const val WRITE_REQUEST_CODE = 43
        const val WRITE_PERMISSION_REQUEST_CODE = 44
        const val OPEN_REQUEST_CODE = 45
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment)
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    export(TagPersistance.getFile(this), data.data)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Snackbar.make(findViewById(android.R.id.content), "Error exporting", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        if (requestCode == OPEN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    import(data.data, TagPersistance.getFile(this))
                    EventBus.getDefault().post(ReloadEvent())
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Snackbar.make(findViewById(android.R.id.content), "Error exporting", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            WRITE_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (fragment.requestExport)
                        createFile()
                    else if (fragment.requestImport)
                        openFile()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // Create a file with the requested MIME type.
        intent.type = "text/javascript"
        intent.putExtra(Intent.EXTRA_TITLE, "tags.json")
        startActivityForResult(intent, WRITE_REQUEST_CODE)
    }

    fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/javascript"
        startActivityForResult(intent, OPEN_REQUEST_CODE)
    }

    //    fun readFile(uri:Uri){
//
//    }
//
//    private fun copy(src: File, dst: Uri) {
//        try {
//            val inStream = FileInputStream(src)
//            val input = inStream.bufferedReader().use { it.readText() }
//            inStream.close()
//
//            val outputStream = contentResolver.openOutputStream(dst)
//            val bw = BufferedWriter(OutputStreamWriter(outputStream))
//            bw.write(input)
//            bw.flush()
//            bw.close()
//        } catch (ex: Exception) {
//            // TODO error
//            ex.printStackTrace()
//        }
//    }
    private fun export(src: File, dst: Uri) {
        val inStream = FileInputStream(src)
        val input = inStream.bufferedReader().use { it.readText() }
        IOUtils.write(input, contentResolver.openOutputStream(dst))
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.export_successs), Snackbar.LENGTH_SHORT).show()
    }

    private fun import(src: Uri, dst: File) {
        val input = contentResolver.openInputStream(src).bufferedReader().use { it.readText() }
        FileUtils.writeStringToFile(dst, input)
        EventBus.getDefault().post(ReloadEvent())
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.import_success), Snackbar.LENGTH_SHORT).show()
    }
}

class SettingsFragment : PreferenceFragmentCompat() {
    var requestImport = false
    var requestExport = false

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference("preference_export").setOnPreferenceClickListener {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestExport = true
                requestImport = false
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_PERMISSION_REQUEST_CODE)
            } else
                (activity as SettingsActivity).createFile()
            true
        }

        findPreference("preference_import").setOnPreferenceClickListener {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestImport = true
                requestExport = false
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_PERMISSION_REQUEST_CODE)
            } else
                (activity as SettingsActivity).openFile()
            true
        }
    }
}
