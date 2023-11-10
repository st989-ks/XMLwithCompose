package ru.ekr.xml_with_compose.main

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import ru.ekr.xml_with_compose.R
import ru.ekr.xml_with_compose.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).also { building ->
            setContentView(building.root)
            onBackPressedDispatcher.addCallback(this) {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    this@MainActivity.finish()
                }

            }
            supportFragmentManager.commit {
                replace(R.id.container_fragment, FragmentMain())
            }
        }
    }
}
