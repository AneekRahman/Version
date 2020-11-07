package com.version.app;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.version.app.Adapters.HomePostAdapter;
import com.version.app.Adapters.SimpleHomePostAdapter;
import com.version.app.Classes.MyUtils;
import com.version.app.Classes.RegularPost;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // View declarations
    private RelativeLayout mMainHolder;

    private FragmentManager mFragManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connecting home post layout views to code
        mMainHolder = (RelativeLayout) findViewById(R.id.main_holder);

        MyUtils.transpStatusBar(this);
        mMainHolder.setPadding(0, 0, 0, MyUtils.getNavBarHeight(this));

        mFragManager = getSupportFragmentManager();

        mFragManager.beginTransaction()
                .add(R.id.fragment_holder, new HomeFragment())
                .commit();


    }

}
