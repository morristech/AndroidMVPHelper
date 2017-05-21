package com.ufkoku.demo_app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.ui.activity.paging_list.PagingActivity;
import com.ufkoku.demo_app.ui.activity.static_list.StaticListActivity;
import com.ufkoku.demo_app.ui.fragments.FragmentsActivity;
import com.ufkoku.demo_app.ui.fragments.paging_list.PagingFragment;

public class StartActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        findViewById(R.id.retainable).setOnClickListener(view -> {
            startActivity(new StaticListActivity.Builder(true).build(StartActivity.this));
        });
        findViewById(R.id.savable).setOnClickListener(view -> {
            startActivity(new StaticListActivity.Builder(false).build(StartActivity.this));
        });
        findViewById(R.id.pagingActivity).setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, PagingActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.fragments).setOnClickListener(view -> {
            Intent intent = new Intent(StartActivity.this, FragmentsActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.pagingFragment).setOnClickListener(view -> getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new PagingFragment(), "PagingFragment")
                .addToBackStack("PagingFragment")
                .commit());
    }
}
