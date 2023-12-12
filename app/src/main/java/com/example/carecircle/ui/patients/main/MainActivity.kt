package com.example.carecircle.ui.patients.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alan.alansdk.AlanCallback
import com.alan.alansdk.AlanConfig
import com.alan.alansdk.button.AlanButton
import com.alan.alansdk.events.EventCommand
import com.example.carecircle.R
import com.example.carecircle.databinding.ActivityMainBinding
import com.example.carecircle.ui.patients.main.tabs.categories.CategoriesFragment
import com.example.carecircle.ui.patients.main.tabs.chat.ChatFragment
import com.example.carecircle.ui.patients.main.tabs.home.HomeFragment
import com.example.carecircle.ui.patients.main.tabs.profile.ProfileFragment
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var alanButton: AlanButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNav
            .setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        showTabFragment(HomeFragment())
                        changeBackgroundColor(R.color.home_tab_color)
                    }

                    R.id.nav_categories -> {
                        showTabFragment(CategoriesFragment())
                        changeBackgroundColor(R.color.categories_tab_color)
                    }

                    R.id.nav_chat -> {
                        showTabFragment(ChatFragment())
                        changeBackgroundColor(R.color.chat_tab_color)
                    }

                    R.id.nav_profile -> {
                        showTabFragment(ProfileFragment())
                        changeBackgroundColor(R.color.profile_tab_color)
                    }
                }
                true
            }
        binding.bottomNav.selectedItemId = R.id.nav_home
        initAlanAi()
    }

    private fun initAlanAi() {
        /// Define the project key
        val config = AlanConfig.builder()
            .setProjectId("e088f0a59289662fd22f222f500bdc4c2e956eca572e1d8b807a3e2338fdd0dc/stage")
            .build()
        alanButton = findViewById(R.id.alan_button)
        alanButton?.initWithConfig(config)

        val alanCallback: AlanCallback = object : AlanCallback() {
            /// Handle commands from Alan AI Studio
            override fun onCommand(eventCommand: EventCommand) {
                try {
                    val command = eventCommand.data
                    val commandName = command.getJSONObject("data").getString("command")
                    navigateToTabsAlan(commandName)
                } catch (e: JSONException) {
                    e.message?.let { Log.e("AlanButton", it) }
                }
            }
        }
        alanButton?.registerCallback(alanCallback);
    }

    private fun navigateToTabsAlan(commandName: String) {
        when (commandName) {
            "Go to home" -> {
                showTabFragmentAlan(HomeFragment(), R.id.nav_home)
                changeBackgroundColor(R.color.home_tab_color)
            }

            "Go to categories" -> {
                showTabFragmentAlan(CategoriesFragment(), R.id.nav_categories)
                changeBackgroundColor(R.color.categories_tab_color)
            }

            "Go to chat" -> {
                showTabFragmentAlan(ChatFragment(), R.id.nav_chat)
                changeBackgroundColor(R.color.chat_tab_color)
            }

            "Go to profile" -> {
                showTabFragmentAlan(ProfileFragment(), R.id.nav_profile)
                changeBackgroundColor(R.color.profile_tab_color)
            }
        }

    }

    private fun showTabFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showTabFragmentAlan(fragment: Fragment, itemId: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        // Update the selected item in the bottom navigation
        binding.bottomNav.selectedItemId = itemId
    }


    private fun changeBackgroundColor(colorResId: Int) {
        val color = ContextCompat.getColor(this, colorResId)
        binding.bottomNav.setBackgroundColor(color)
    }


}