package com.example.final_project_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.final_project_android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val favouriteFragment = FavouriteFragment()
        val profileFragment = ProfileFragment()

        switchFragments(homeFragment)

        binding.bottomNavMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.ic_home -> {
                    switchFragments(homeFragment)
                }

                R.id.ic_fav -> {
                    switchFragments(favouriteFragment)
                }

                R.id.ic_account -> {
                    switchFragments(profileFragment)
                }
            }
            true
        }
    }

    private fun switchFragments(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            commit()
        }
    }
}