package com.example.carecircle.ui.doctors.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.carecircle.R
import com.example.carecircle.databinding.ActivityDocMainBinding
import com.example.carecircle.ui.doctors.main.tabs.chat.DocChatFragment
import com.example.carecircle.ui.doctors.main.tabs.home.DocHomeFragment
import com.example.carecircle.ui.doctors.main.tabs.list.PatientsListFragment
import com.example.carecircle.ui.doctors.main.tabs.profile.DocProfileFragment


class DocMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNav
            .setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        showTabFragment(DocHomeFragment())
                        changeBackgroundColor(R.color.home_tab_color)
                    }

                    R.id.nav_categories -> {
                        showTabFragment(PatientsListFragment())
                        changeBackgroundColor(R.color.categories_tab_color)
                    }

                    R.id.nav_chat -> {
                        showTabFragment(DocChatFragment())
                        changeBackgroundColor(R.color.chat_tab_color)
                    }

                    R.id.nav_profile -> {
                        showTabFragment(DocProfileFragment())
                        changeBackgroundColor(R.color.profile_tab_color)
                    }
                }
                true
            }
        binding.bottomNav.selectedItemId = R.id.nav_home
    }

    private fun showTabFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun changeBackgroundColor(colorResId: Int) {
        val color = ContextCompat.getColor(this, colorResId)
        binding.bottomNav.setBackgroundColor(color)
    }
}