package com.enjoy.selaras.activities

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.enjoy.selaras.R
import com.enjoy.selaras.fragment.HomeFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val window = this.window

        if (isDarkThemeOn()) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.md_theme_dark_background)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.md_theme_dark_background)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.md_theme_light_background)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.md_theme_light_background)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = true
            }
        }

        replaceFragment(HomeFragment.newInstance(), false)
    }

    private fun replaceFragment(fragment: Fragment, transition: Boolean) {
        val fragmentTransition = supportFragmentManager.beginTransaction()

        if (transition) {
            fragmentTransition.setCustomAnimations(
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left
            )
        }

        fragmentTransition.replace(R.id.frame_layout, fragment)
            .addToBackStack(fragment.javaClass.simpleName).commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val fragments = supportFragmentManager.fragments
        if (fragments.size == 0) {
            finish()
        }
    }

    private fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }
}