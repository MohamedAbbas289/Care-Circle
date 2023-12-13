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

interface FragmentCallback {
    fun onCommandReceived(command: String)
}

class MainActivity : AppCompatActivity(), FragmentCallback {
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
        val config = AlanConfig.builder()
            .setProjectId("e088f0a59289662fd22f222f500bdc4c2e956eca572e1d8b807a3e2338fdd0dc/stage")
            .build()
        alanButton = findViewById(R.id.alan_button)
        alanButton?.initWithConfig(config)

        val alanCallback: AlanCallback = object : AlanCallback() {
            override fun onCommand(eventCommand: EventCommand) {
                try {
                    val command = eventCommand.data
                    val commandName = command.getJSONObject("data").getString("command")
                    alanCommands(commandName)
                } catch (e: JSONException) {
                    e.message?.let { Log.e("AlanButton", it) }
                }
            }
        }
        alanButton?.registerCallback(alanCallback)
    }

    private fun alanCommands(commandName: String) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

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


            else -> {
                // Pass the command to the current fragment
                if (currentFragment is FragmentCallback) {
                    currentFragment.onCommandReceived(commandName)
                }
            }
        }
    }

    private fun showTabFragment(fragment: Fragment) {
        if (fragment is HomeFragment) {
            (fragment as HomeFragment).setCallback(this)
        } else if (fragment is CategoriesFragment) {
            (fragment as CategoriesFragment).setCallback(this)
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showTabFragmentAlan(fragment: Fragment, itemId: Int) {
        if (fragment is FragmentCallback) {
            (fragment as FragmentCallback).onCommandReceived("Go to ${fragment.javaClass.simpleName}")
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        binding.bottomNav.selectedItemId = itemId
    }

    private fun changeBackgroundColor(colorResId: Int) {
        val color = ContextCompat.getColor(this, colorResId)
        binding.bottomNav.setBackgroundColor(color)
    }

    // Implement the FragmentCallback method
    override fun onCommandReceived(command: String) {
        alanCommands(command)
    }
}
