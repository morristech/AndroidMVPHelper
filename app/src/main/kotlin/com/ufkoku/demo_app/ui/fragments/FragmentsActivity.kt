package com.ufkoku.demo_app.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

import com.ufkoku.demo_app.R
import com.ufkoku.demo_app.ui.fragments.base.IFragmentManager
import com.ufkoku.demo_app.ui.fragments.static_list.StaticListFragment
import kotlinx.android.synthetic.main.activity_fragments.*


class FragmentsActivity : AppCompatActivity(), IFragmentManager {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragments)
        savable.setOnClickListener({ setFragment(StaticListFragment.Builder(false).build()) })
        retainable.setOnClickListener({ setFragment(StaticListFragment.Builder(true).build()) })
    }

    override fun setFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
    }

}
