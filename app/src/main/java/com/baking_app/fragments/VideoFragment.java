package com.baking_app.fragments;


import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baking_app.R;
import com.baking_app.utils.AppConstants;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment implements ExoPlayer.EventListener {


    public static final String TAG = VideoFragment.class.getSimpleName();
    private TextView mStepDesc;
    private SimpleExoPlayerView mSimpleExoPlayerView;
    private ImageView mPlaceHolderImage;
    private SimpleExoPlayer mSimpleExoPlayer;
    private long mPositionPlayer;
    private boolean mPlayWhenReady;
    private String mDescription, mUrl, mThumbnailImage;
    private boolean pane;
    private MediaSessionCompat mMediaSessionCompat;
    private PlaybackStateCompat.Builder mPlaybackBuilder;
    private Uri mVideoUri;


    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mDescription = bundle.getString(AppConstants.KEY_STEPS_DESC);
            mUrl = bundle.getString(AppConstants.KEY_STEPS_URL);
            pane = bundle.getBoolean(AppConstants.KEY_PANE_VID);
            mThumbnailImage = bundle.getString(AppConstants.THUMBNAIL_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);

        initUI(rootView);

        if (savedInstanceState != null) {
            int placeHolderVisibility = savedInstanceState.getInt(AppConstants.KEY_VISIBILITY_PLACEHOLDER);
            mPlaceHolderImage.setVisibility(placeHolderVisibility);
            int visibilityExo = savedInstanceState.getInt(AppConstants.KEY_VISIBILITY_EXO_PLAYER);
            mSimpleExoPlayerView.setVisibility(visibilityExo);
            //get play when ready boolean
            mPlayWhenReady = savedInstanceState.getBoolean(AppConstants.KEY_PLAY_WHEN_READY);
        }


        if (mUrl != null) {
            if (mUrl.equals("")) {
                mSimpleExoPlayerView.setVisibility(View.GONE);
                mPlaceHolderImage.setVisibility(View.VISIBLE);
                if (!mThumbnailImage.equals("")) {
                    Picasso.with(getActivity()).load(mThumbnailImage).into(mPlaceHolderImage);
                }
                else {
                    Toast.makeText(getContext(), "No Video Available", Toast.LENGTH_SHORT).show();
                }
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mStepDesc.setText(mDescription);
                } else {
                    hideUI();
                    mSimpleExoPlayerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    mSimpleExoPlayerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
            } else {
                if (savedInstanceState != null) {
                    //resuming by seeking to the last position
                    mPositionPlayer = savedInstanceState.getLong(AppConstants.MEDIA_POS);
                }
                mPlaceHolderImage.setVisibility(View.GONE);
                initializeMedia();
                initializePlayer(Uri.parse(mUrl));
                mVideoUri = Uri.parse(mUrl);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mStepDesc.setText(mDescription);
                } else {
                    hideUI();
                    mSimpleExoPlayerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    mSimpleExoPlayerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
            }
        } else {
            mSimpleExoPlayerView.setVisibility(View.GONE);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mStepDesc.setText(mDescription);
            } else {
                hideUI();
                mSimpleExoPlayerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                mSimpleExoPlayerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        }
        return rootView;
    }

    private void initUI(View rootView) {
        mPlaceHolderImage =rootView.findViewById(R.id.placeholder_no_video_image);
        mSimpleExoPlayerView =rootView.findViewById(R.id.video_view_recipe);
        mStepDesc =rootView.findViewById(R.id.step_description_text_view);

    }

    private void initializeMedia() {
        mMediaSessionCompat = new MediaSessionCompat(getActivity(), TAG);
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSessionCompat.setMediaButtonReceiver(null);
        mPlaybackBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSessionCompat.setPlaybackState(mPlaybackBuilder.build());
        mMediaSessionCompat.setCallback(new SessionCallBacks());
        mMediaSessionCompat.setActive(true);
    }

    private void hideUI() {
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            //Use Google's "LeanBack" mode to get fullscreen in landscape
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    private void initializePlayer(Uri mediaUri) {
        if (mSimpleExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance
                    (getActivity(), trackSelector, loadControl);
            mSimpleExoPlayerView.setPlayer(mSimpleExoPlayer);
            mSimpleExoPlayer.addListener(this);
            String userAgent = Util.getUserAgent(getActivity(),
                    getActivity().getString(R.string.application_name_exo_player));
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                    new DefaultDataSourceFactory(getActivity(), userAgent),
                    new DefaultExtractorsFactory(), null, null);
            mSimpleExoPlayer.prepare(mediaSource);
            mSimpleExoPlayer.setPlayWhenReady(true);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(AppConstants.KEY_VISIBILITY_EXO_PLAYER, mSimpleExoPlayerView.getVisibility());
        outState.putInt(AppConstants.KEY_VISIBILITY_PLACEHOLDER, mPlaceHolderImage.getVisibility());
        //Saving current Position before rotation
        outState.putLong(AppConstants.MEDIA_POS, mPositionPlayer);
        //for preserving state of exoplayer
        outState.putBoolean(AppConstants.KEY_PLAY_WHEN_READY, mPlayWhenReady);
    }

    private void releasePlayer() {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.stop();
            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;
        }
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if (mMediaSessionCompat != null) {
            mMediaSessionCompat.setActive(false);
        }
    }

    @Override
    public void onPause() {
        //releasing in Pause and saving current position for resuming
        super.onPause();
        if (mSimpleExoPlayer != null) {
            mPositionPlayer = mSimpleExoPlayer.getCurrentPosition();
            //getting play when ready so that player can be properly store state on rotation
            mPlayWhenReady = mSimpleExoPlayer.getPlayWhenReady();
            mSimpleExoPlayer.stop();
            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSimpleExoPlayer != null) {
            //resuming properly
            mSimpleExoPlayer.setPlayWhenReady(mPlayWhenReady);
            mSimpleExoPlayer.seekTo(mPositionPlayer);
        } else {
            //Correctly initialize and play properly fromm seekTo function
            initializeMedia();
            initializePlayer(mVideoUri);
            mSimpleExoPlayer.setPlayWhenReady(mPlayWhenReady);
            mSimpleExoPlayer.seekTo(mPositionPlayer);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
            mPlaybackBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mSimpleExoPlayer.getCurrentPosition(), 1f);
        } else if (playbackState == ExoPlayer.STATE_READY) {
            mPlaybackBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mSimpleExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSessionCompat.setPlaybackState(mPlaybackBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }


    private class SessionCallBacks extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            super.onPlay();
            mSimpleExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            super.onPause();
            mSimpleExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            mSimpleExoPlayer.seekTo(0);
        }
    }

}