package com.ufkoku.demo_app.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.ufkoku.demo_app.R
import com.ufkoku.demo_app.ui.activity.static_list.StaticListActivity
import com.ufkoku.demo_app.ui.fragments.FragmentsActivity
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_start)

        retainable.setOnClickListener({ startActivity(StaticListActivity.Builder(true).build(this@StartActivity)) })

        savable.setOnClickListener({ startActivity(StaticListActivity.Builder(false).build(this@StartActivity)) })

        fragments.setOnClickListener({
                                         val intent = Intent(this@StartActivity, FragmentsActivity::class.java)
                                         startActivity(intent)
                                     })

    }

}
