package com.baking_app.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baking_app.R;
import com.baking_app.fragments.VideoFragment;
import com.baking_app.utils.AppConstants;

public class RecipeVideoActivity extends AppCompatActivity {

    private static final String TAG = RecipeVideoActivity.class.getSimpleName();
    private Bundle bundle;
    private boolean fragmentCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        if (savedInstanceState != null) {
            fragmentCreated = savedInstanceState.getBoolean(AppConstants.KEY_ROTATION_VIDEO_ACTIVITY);
        }
        if (!fragmentCreated) {
            bundle = new Bundle();
            bundle = getIntent().getExtras();
            VideoFragment videoFragment = new VideoFragment();
            videoFragment.setArguments(bundle);
            fragmentCreated = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.video_fragment, videoFragment).commit();
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AppConstants.KEY_ROTATION_VIDEO_ACTIVITY, fragmentCreated);
    }
}
