package com.ufkoku.demo_app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.ui.activity.retainable.paging_list.PagingActivity;
import com.ufkoku.demo_app.ui.activity.retainable.static_list.RetainableActivity;
import com.ufkoku.demo_app.ui.activity.savable.SavableActivity;
import com.ufkoku.demo_app.ui.fragments.FragmentsActivity;
import com.ufkoku.demo_app.ui.fragments.retainable.paging_list.PagingFragment;

public class StartActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        findViewById(R.id.retainable).setOnClickListener(view -> {
            Intent intent = new Intent(StartActivity.this, RetainableActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.savable).setOnClickListener(view -> {
            Intent intent = new Intent(StartActivity.this, SavableActivity.class);
            startActivity(intent);
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
