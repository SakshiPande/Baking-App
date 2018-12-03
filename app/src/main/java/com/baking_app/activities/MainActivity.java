package com.baking_app.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;

import com.baking_app.R;
import com.baking_app.IdlingResource.SimpleIdlingResource;

public class MainActivity extends AppCompatActivity {

    private static boolean mTwoPane;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.tv_list_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        getIdlingResource();
    }



    public static boolean isTwoPane() {
        return mTwoPane;
    }
}
