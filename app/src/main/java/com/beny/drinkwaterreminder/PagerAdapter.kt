package com.beny.drinkwaterreminder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val fragments = listOf(
        HomeFragment.newInstance(),
        SettingsFragment.newInstance()
    )

    private val fragmentTitles = listOf(
        "Home",
        "Settings"
    )

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitles[position]
    }
} 