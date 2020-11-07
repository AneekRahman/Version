package com.version.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.util.Util;
import com.version.app.Adapters.HomePostAdapter;
import com.version.app.Adapters.SimpleHomePostAdapter;
import com.version.app.Classes.MyUtils;
import com.version.app.Classes.RegularPost;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    // View delcarations
    private RecyclerView mRecyclerView;

    // Home recyclerview posts arraylist and adapter
    private List<RegularPost> mPostsList = new ArrayList<>();
    private HomePostAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private boolean wasPaused = false;

    public HomeFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);

        // Connecting home post layout views to code
        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view);

        // Setting up Recyclerview
        mAdapter = new HomePostAdapter(mPostsList, getContext());
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        setRecyclerViewListeners();

        addTestPosts();

        return rootview;

    }

    private void setRecyclerViewListeners(){

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) { }

            // THIS FUCKER makes the exoplayers' adapter autoplay the player that is most visible (in percentage)
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mRecyclerView != null){

                    LinearLayoutManager layoutManager = ((LinearLayoutManager) mRecyclerView.getLayoutManager());
                    mAdapter.checkPercentageAndStartStopPlayer(MyUtils.getPercentage(mRecyclerView, layoutManager));

                }
            }
        });

    }



    private void addTestPosts(){

        RegularPost post = new RegularPost(0,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                0, "Aneek Rahman",
                "https://pbs.twimg.com/profile_images/953631791256137730/Mq-GwMYZ.jpg",
                "Hello, im a description" , 1999999993L,1000, 2000, 40000);
        mPostsList.add(post);

        post = new RegularPost(0,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                0, "Someone Else",
                "https://pbs.twimg.com/profile_images/953631791256137730/Mq-GwMYZ.jpg",
                "Hello, im a description" , 1993999,1000, 2000, 40000);
        mPostsList.add(post);

        post = new RegularPost(0,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                0, "Ligma Balls",
                "https://pbs.twimg.com/profile_images/953631791256137730/Mq-GwMYZ.jpg",
                "Hello, im a description" , 1993999,1000, 2000, 40000);
        mPostsList.add(post);

        post = new RegularPost(0,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                0, "Kochi Dab",
                "https://pbs.twimg.com/profile_images/953631791256137730/Mq-GwMYZ.jpg",
                "Hello, im a description" , 1993999,1000, 2000, 40000);
        mPostsList.add(post);

        post = new RegularPost(0,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                0, "Vallage Naa",
                "https://pbs.twimg.com/profile_images/953631791256137730/Mq-GwMYZ.jpg",
                "Hello, im a description" , 1993999,1000, 2000, 40000);
        mPostsList.add(post);

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();
        if ((Util.SDK_INT > 23) && wasPaused) {
            mAdapter.initExoPlayerFromFragment();
            wasPaused = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23) && wasPaused) {
            mAdapter.initExoPlayerFromFragment();
            wasPaused = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            mAdapter.releaseExoPlayerFromFragment();
            wasPaused = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            mAdapter.releaseExoPlayerFromFragment();
            wasPaused = true;
        }
    }

}
