package com.baking_app.fragments;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baking_app.R;
import com.baking_app.activities.RecipeVideoActivity;
import com.baking_app.adapters.VideoAdapter;
import com.baking_app.model.Ingredient;
import com.baking_app.model.Recipe;
import com.baking_app.model.Step;
import com.baking_app.utils.OnClick;
import com.baking_app.utils.AppConstants;
import com.baking_app.widget.RecipeWidgetProvider;
import com.google.gson.Gson;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeDetailFragment extends Fragment implements OnClick {

    private static final String TAG = RecipeDetailFragment.class.getSimpleName();

    private RecyclerView mStepRv;
    private Gson gson;
    private VideoAdapter mVideoAdapter;
    private FloatingActionButton mWidgetAddButton;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<Step> mStepList;
    private ArrayList<Ingredient> mIngredientList;
    private boolean twoPane;
    private Parcelable mListState;
    private RelativeLayout mRlIngridients;



    public RecipeDetailFragment() {
        }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        mStepList = bundle.getParcelableArrayList(AppConstants.KEY_STEPS_JSON);
        mIngredientList = bundle.getParcelableArrayList(AppConstants.KEY_INGREDIENTS_JSON);
        twoPane = bundle.getBoolean(AppConstants.KEY_PANE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        initUI(view);



        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(AppConstants.RECYCLER_VIEW_STATE);
        }

        mVideoAdapter = new VideoAdapter(getActivity(), mStepList);
        mVideoAdapter.setOnClick(this);
        mStepRv.setAdapter(mVideoAdapter);
        gson=new Gson();

        mWidgetAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

                Recipe recipe = gson.fromJson(sharedPreferences.getString(AppConstants.WIDGET_RESULT, null), Recipe.class);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());

                Bundle bundle = new Bundle();

                int appWidgetId = bundle.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
                RecipeWidgetProvider.updateAppWidget(getActivity(), appWidgetManager, appWidgetId, recipe.getName(),
                        recipe.getIngredients());

                Toast.makeText(getActivity(), "Added " + recipe.getName() + " to Widget.", Toast.LENGTH_SHORT).show();


            }
        });

        mRlIngridients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_ingridient_list, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                TextView ingredientsText= dialogView.findViewById(R.id.ingredients_list_text_view);
                StringBuffer stringBuffer = new StringBuffer();

                for (Ingredient ingredient : mIngredientList) {
                    stringBuffer.append("\u2022 " + ingredient.getQuantity() + " " +
                            ingredient.getIngredient() + " " + ingredient.getMeasure() + "\n");
                }

                ingredientsText.setText(stringBuffer.toString());

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.setCancelable(true);
                alertDialog.show();
            }
        });

        return view;
    }

    private void initUI(View view) {

        mStepRv =view.findViewById(R.id.step_recycler_view);
        mWidgetAddButton =view.findViewById(R.id.fab_widget);
        mRlIngridients=view.findViewById(R.id.rl_ingredients);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mStepRv.setLayoutManager(mLinearLayoutManager);



    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListState != null) {
            mLinearLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelable(AppConstants.RECYCLER_VIEW_STATE, mLinearLayoutManager.onSaveInstanceState());

    }

    @Override
    public void onClick(Context context, Integer id, String description, String url, String thumbnailUrl) {
        if (twoPane) {
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstants.KEY_STEPS_ID, id);
            bundle.putString(AppConstants.KEY_STEPS_DESC, description);
            bundle.putString(AppConstants.KEY_STEPS_URL, url);
            bundle.putBoolean(AppConstants.KEY_PANE_VID, twoPane);
            bundle.putString(AppConstants.THUMBNAIL_IMAGE, thumbnailUrl);
            VideoFragment videoFragment = new VideoFragment();
            videoFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.video_container_tab, videoFragment).commit();
        } else {
            Intent intent = new Intent(context, RecipeVideoActivity.class);
            intent.putExtra(AppConstants.KEY_STEPS_ID, id);
            intent.putExtra(AppConstants.KEY_STEPS_DESC, description);
            intent.putExtra(AppConstants.KEY_STEPS_URL, url);
            intent.putExtra(AppConstants.THUMBNAIL_IMAGE, thumbnailUrl);
            context.startActivity(intent);
        }
    }

}
