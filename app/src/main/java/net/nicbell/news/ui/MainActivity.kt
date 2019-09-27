package net.nicbell.news.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.nicbell.news.R

/**
 * Main activity, holds fragment navigation.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = ""
    }
}