package com.ufkoku.demo_app.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.ui.fragments.base.IFragmentManager;
import com.ufkoku.demo_app.ui.fragments.static_list.StaticListFragment;


public class FragmentsActivity extends AppCompatActivity implements IFragmentManager {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
        findViewById(R.id.savable).setOnClickListener(view -> setFragment(new StaticListFragment.Builder(false).build()));
        findViewById(R.id.retainable).setOnClickListener(view -> setFragment(new StaticListFragment.Builder(true).build()));
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
