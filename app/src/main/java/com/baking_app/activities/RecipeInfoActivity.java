package com.baking_app.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baking_app.R;
import com.baking_app.fragments.RecipeDetailFragment;
import com.baking_app.fragments.VideoFragment;
import com.baking_app.model.Ingredient;
import com.baking_app.model.Step;
import com.baking_app.utils.AppConstants;

import java.util.ArrayList;

public class RecipeInfoActivity extends AppCompatActivity {

    private static final String TAG = RecipeInfoActivity.class.getSimpleName();
    private ArrayList<Step> mStepJson;
    private ArrayList<Ingredient> mIngredientJson;
    private boolean isRotated;
    private boolean isTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState != null) {
            isRotated = savedInstanceState.getBoolean(AppConstants.KEY_ROTATION_DETAIL_ACTIVITY);
        }

        if (findViewById(R.id.video_container_tab) != null) {
            isTwoPane = true;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.video_container_tab, new VideoFragment()).commit();
        }
        if (savedInstanceState == null) {

            mStepJson = getIntent().getParcelableArrayListExtra(AppConstants.KEY_STEPS);
            mIngredientJson = getIntent().getParcelableArrayListExtra(AppConstants.KEY_INGREDIENTS);

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(AppConstants.KEY_STEPS_JSON, mStepJson);
            bundle.putParcelableArrayList(AppConstants.KEY_INGREDIENTS_JSON, mIngredientJson);
            bundle.putBoolean(AppConstants.KEY_PANE, isTwoPane);

            Log.d(TAG, "Pane: " + isTwoPane);

            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
            recipeDetailFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment, recipeDetailFragment).commit();
            isRotated = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.getBoolean(AppConstants.KEY_ROTATION_DETAIL_ACTIVITY, isRotated);
    }

}
