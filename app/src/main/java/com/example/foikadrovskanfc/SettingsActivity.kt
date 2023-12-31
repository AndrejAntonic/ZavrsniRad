package com.example.foikadrovskanfc

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.foikadrovskanfc.fragments.SettingsFragment

const val RESULT_LANG_CHANGED = AppCompatActivity.RESULT_FIRST_USER
class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var darkModeSelected: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val window = window
        val statusBarColor = ContextCompat.getColor(this, R.color.red)
        changeStatusBarColor(window, statusBarColor)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        darkModeSelected = sharedPreferences.getBoolean("preference_dark_mode", false)
        switchDarkMode(darkModeSelected)


        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_settings, SettingsFragment())
            .commit()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when(key) {
            "preference_dark_mode" -> {
                darkModeSelected = sharedPreferences?.getBoolean(key, false) ?: false
                switchDarkMode(darkModeSelected)
                //TODO -> Switch to dark mode when pressed
            }
            "preference_language" -> notifyLanguageChangedAndClose()
            //TODO -> Language doesn't change in main activity and app needs to be restared for it to change language in other activities
        }
    }

    private fun changeStatusBarColor(window: Window, color: Int){
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }
    private fun notifyLanguageChangedAndClose() {
        setResult(
            RESULT_LANG_CHANGED)
        finish()
    }
    companion object {
        fun switchDarkMode(isDarkModeSelected: Boolean?) {
            if (isDarkModeSelected == true) {
                AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.
                    MODE_NIGHT_YES)
            } else {
                AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.
                    MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}