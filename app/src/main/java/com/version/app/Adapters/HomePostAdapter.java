package com.version.app.Adapters;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.version.app.Classes.MyUtils;
import com.version.app.Classes.RegularPost;
import com.version.app.R;

import java.util.ArrayList;
import java.util.List;

public class HomePostAdapter extends RecyclerView.Adapter<HomePostAdapter.MyViewHolder> {

    // Regular home posts list
    private List<RegularPost> postsList;
    private Context mContext;

    // Home Fragment Class constructor for accessing the showComments method

    // View holders to control exoplayers
    private MyViewHolder previousHolder, afterHolder, thirdHolder;

    // Main view holder class constructor
    public class MyViewHolder extends RecyclerView.ViewHolder {

        // A home posts' layout View declaration
        private RelativeLayout mMainHolder;
        private PlayerView mPlayerView;
        private LinearLayout mPlayPauseHolder, mFullscreenBtnHolder;
        private ImageView mPlayBtn, mPauseBtn;

        // Holders ID for previous, after and third view holder assignment management
        private int holderID, mPostUserID;

        // Exoplayer declarations
        private String vidAddress;
        private ExoPlayer mExoPlayer;
        private MediaSource mMediaSource;
        private int mExoPlayerWindowIndex = 0;
        private long mPlaybackPosition = 0;

        // Setting holder id method for management
        public void setHolderID(int holderID) {
            this.holderID = holderID;
        }

        // Getting holder id when managing
        public int getHolderID() {
            return holderID;
        }

        public void setVidAddress(String address){
            this.vidAddress = address;
        }

        private MyViewHolder(View view) {
            super(view);

            // Connecting home post layout views to code
            mMainHolder = (RelativeLayout) view.findViewById(R.id.main_holder);
            mPlayerView = (PlayerView) view.findViewById(R.id.player_view);
            mPlayPauseHolder = (LinearLayout) view.findViewById(R.id.play_pause_holder);
            mPlayBtn = (ImageView) view.findViewById(R.id.play_button);
            mPauseBtn = (ImageView) view.findViewById(R.id.pause_button);
            mFullscreenBtnHolder = (LinearLayout) view.findViewById(R.id.fullscreen_button_holder);

            // Setting up the posts click listeners
            setClickListeners();

        }

        // Exoplayer events listener
        Player.EventListener mExoPlayerEventListener = new Player.EventListener() {

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if(playWhenReady){
                    playPauseBtnToggle(true);
                }else{
                    playPauseBtnToggle(false);
                }

                // Giving Player state response to user
                switch (playbackState){

                    case ExoPlayer.STATE_READY: {
                        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                    break;
                    case ExoPlayer.STATE_BUFFERING: {
                    }
                    case ExoPlayer.STATE_ENDED: {
                        ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                    case ExoPlayer.STATE_IDLE: {
                        ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                    break;
                }

            }

            // On error keep on trying to ready the player on and on (This happens when internet connection unintentionally goes)
            @Override
            public void onPlayerError(ExoPlaybackException error) {

                mExoPlayer.prepare(mMediaSource, false, false);

            }
            // Not needed default methods
            @Override public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {}
            @Override public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}
            @Override public void onLoadingChanged(boolean isLoading) {}
            @Override public void onPositionDiscontinuity(int reason) {}
            @Override public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {}
            @Override public void onSeekProcessed() {}
            @Override public void onRepeatModeChanged(int repeatMode) {}
            @Override public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {}
        };

        // Setting a fixed height for the root view
        private void setMainHolderHeight(){

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;

            final float scale = mContext.getResources().getDisplayMetrics().density;
            int pixels = (int) ((55 + 4) * scale + 0.5f);

            int adjustedHeight = (height + MyUtils.getNavBarHeight(mContext)) - (MyUtils.getStatusBarHeight(mContext) + pixels);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, adjustedHeight);
            mMainHolder.setLayoutParams(params);

        }

        // Initializing the posts exoplayer
        private void initializeExoPlayer(){

            if(mExoPlayer == null){

                mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                        new DefaultRenderersFactory(mContext),
                        new DefaultTrackSelector(),
                        new DefaultLoadControl());

                mPlayerView.setPlayer(mExoPlayer);

                mExoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);

                mExoPlayer.seekTo(mExoPlayerWindowIndex, mPlaybackPosition);

            }

            prepareExoPlayer();

        }

        // Preparing the exoplayer with the video address
        private void prepareExoPlayer(){

            Uri uri = Uri.parse(vidAddress);
            mMediaSource = buildMediaSource(uri);
            mExoPlayer.prepare(mMediaSource, false, false);

            setExoPlayerListeners();

        }


        // Building mediasource for exoplayer
        private MediaSource buildMediaSource(Uri uri) {
            return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("ripple-exo")).createMediaSource(uri);
        }


        // Starting the exoplayer manually
        public void startExoPlayer(){
            mExoPlayer.setPlayWhenReady(true);
        }

        // Stopping the exoplayer manually
        public void stopExoPlayer(){
            mExoPlayer.setPlayWhenReady(false);
        }

        private void playPauseBtnToggle(boolean play){

            if(play){

                mPlayBtn.setVisibility(View.GONE);
                mPauseBtn.setVisibility(View.VISIBLE);

            }else{

                mPlayBtn.setVisibility(View.VISIBLE);
                mPauseBtn.setVisibility(View.GONE);

            }

        }

        private void setClickListeners(){

            mPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playPauseBtnToggle(true);
                    startExoPlayer();
                }
            });

            mPauseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playPauseBtnToggle(false);
                    stopExoPlayer();
                }
            });

        }

        private void setExoPlayerListeners(){

            mExoPlayer.addListener(mExoPlayerEventListener);

            mPlayerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
                @Override
                public void onVisibilityChange(int visibility) {
                    if(visibility == View.VISIBLE){

                        setFadeAnim(mPlayPauseHolder, true);
                        setFadeAnim(mFullscreenBtnHolder, true);

                    }else{

                        setFadeAnim(mPlayPauseHolder, false);
                        setFadeAnim(mFullscreenBtnHolder, false);

                    }
                }
            });

        }

        private void setFadeAnim(final View v, final boolean fadeIn){

            Animation fade = new AlphaAnimation(1, 0);
            fade.setInterpolator(new FastOutSlowInInterpolator());
            if(fadeIn){
                v.setVisibility(View.VISIBLE);
                fade = new AlphaAnimation(0, 1);
                fade.setInterpolator(new AnticipateOvershootInterpolator());
            }
            fade.setDuration(400);
            fade.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation animation) {}
                @Override public void onAnimationEnd(Animation animation) {
                    if(!fadeIn){
                        v.setVisibility(View.GONE);
                    }
                }
                @Override public void onAnimationRepeat(Animation animation) {}
            });
            v.startAnimation(fade);

        }

        // Releasing the exoplayer manually ( When home fragment gets paused or stopped )
        private void releaseExoPlayer() {

            mPlaybackPosition = mExoPlayer.getCurrentPosition();
            mExoPlayerWindowIndex = mExoPlayer.getCurrentWindowIndex();
            mExoPlayer.removeListener(mExoPlayerEventListener);
            mExoPlayer.release();
            mExoPlayer = null;

        }
    }

    // Main class constructor
    public HomePostAdapter(List<RegularPost> postsList, Context context) {
        this.postsList = postsList;
        this.mContext = context;
    }

    // Inflating the post layout
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_post_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // Getting the posts instance to get that specific posts data and contents
        RegularPost post = postsList.get(position);

        holder.setVidAddress(post.getPostContentUrl());

        // Initializing exoplayer
        holder.initializeExoPlayer();

        // Setting a unique holder id for managing the previous, after and third viewholders instances
        holder.setHolderID(position);


    }

    // Default method
    @Override
    public int getItemCount() {
        return postsList.size();
    }

    // Method when a view gets attached to the recyclersview ( Needed for managing the exoplayers autoplay and pause )
    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if(previousHolder == null){
            previousHolder = holder;
            return;
        }
        if(holder.getHolderID() == previousHolder.getHolderID() + 1){
            afterHolder = holder;
        }else if(holder.getHolderID() == previousHolder.getHolderID() + 2){
            thirdHolder = holder;
        }else if(holder.getHolderID() == previousHolder.getHolderID() - 1){
            thirdHolder = afterHolder;
            afterHolder = previousHolder;
            previousHolder = holder;
        }

    }

    // Method when a view gets detached to the recyclersview ( Needed for managing the exoplayers autoplay and pause )
    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        holder.stopExoPlayer();
        if(holder.getHolderID() == previousHolder.getHolderID()){
            previousHolder = afterHolder;
            afterHolder = thirdHolder;
            thirdHolder = null;
        }

    }

    // Checks the visibility percentage of a post on user screen
    public void checkPercentageAndStartStopPlayer(ArrayList<Integer> arrayList){

        if(arrayList.size() == 2){

            Integer previousPer = arrayList.get(0);
            Integer afterPer = arrayList.get(1);

            if(previousPer < 50 && afterPer > 50){
                previousHolder.stopExoPlayer();
                afterHolder.startExoPlayer();
            }else if(previousPer > 50 && afterPer < 50){
                previousHolder.startExoPlayer();
                afterHolder.stopExoPlayer();
            }

        }else if(arrayList.size() == 3){

            previousHolder.stopExoPlayer();
            afterHolder.startExoPlayer();
            thirdHolder.stopExoPlayer();

        }

    }

    // Stops all playing exoplayers manually
    public void stopExoPlayerInAdapter(){

        if(previousHolder != null)
            previousHolder.stopExoPlayer();
        if(afterHolder != null)
            afterHolder.stopExoPlayer();
        if(thirdHolder != null)
            thirdHolder.stopExoPlayer();

    }

    // Initializes all viewholders' exoplayers manually (When home fragment gets paused or stopped)
    public void initExoPlayerFromFragment(){

        if(previousHolder != null)
            previousHolder.initializeExoPlayer();
        if(afterHolder != null)
            afterHolder.initializeExoPlayer();
        if(thirdHolder != null)
            thirdHolder.initializeExoPlayer();
    }

    // Releases all viewholders' exoplayers manually (When home fragment gets resumed or started)
    public void releaseExoPlayerFromFragment(){

        if(previousHolder != null)
            previousHolder.releaseExoPlayer();
        if(afterHolder != null)
            afterHolder.releaseExoPlayer();
        if(thirdHolder != null)
            thirdHolder.releaseExoPlayer();


    }

}