package net.duiker101.tagspro.tagspro

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(R.layout.fragment_settings)
//        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
                .add(android.R.id.content, SettingsFragment())
                .commit()
    }
}

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        addPreferencesFromResource(R.xml.preferences)
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }


}