package com.baking_app.fragments;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baking_app.R;
import com.baking_app.activities.MainActivity;
import com.baking_app.adapters.RecipeAdapter;
import com.baking_app.model.Recipe;
import com.baking_app.networking.BakingAPI;
import com.baking_app.networking.RestClient;
import com.baking_app.IdlingResource.SimpleIdlingResource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainFragment extends Fragment {



    private RecyclerView mRvRecipe;
    private SimpleIdlingResource mIdlingResource;
    private List<Recipe> mRecipeList;
    private boolean isTwoPane;
    private RecipeAdapter mRecipeAdapter;


    public MainFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        mRvRecipe =rootview.findViewById(R.id.rv_recipe);

        mIdlingResource = (SimpleIdlingResource) ((MainActivity) getActivity()).getIdlingResource();

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }

        if (isNetworkConnected()) {
            getRecipeData();
        } else {
            Snackbar.make(rootview, getActivity().getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
        }

        return rootview;
    }



    private void getRecipeData() {


        Retrofit retrofit=RestClient.retrofitService();
        BakingAPI bakingAPI=retrofit.create(BakingAPI.class);
        Call<List<Recipe>> reciepeCall=bakingAPI.getRecipeData();

        reciepeCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    mRecipeList =response.body();
                    mRecipeAdapter = new RecipeAdapter(getContext(), mRecipeList);
                    isTwoPane = MainActivity.isTwoPane();
                    if (isTwoPane) {

                        GridLayoutManager gridLayoutManager = new
                                GridLayoutManager(getActivity(), 3);
                        mRvRecipe.setLayoutManager(gridLayoutManager);
                        mRvRecipe.setAdapter(mRecipeAdapter);
                        mIdlingResource.setIdleState(true);
                    } else {
                        LinearLayoutManager linearLayoutManager = new
                                LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        mRvRecipe.setLayoutManager(linearLayoutManager);
                        mRvRecipe.setAdapter(mRecipeAdapter);
                        mIdlingResource.setIdleState(true);
                    }

                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, Throwable t) {
                Toast.makeText(getContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }



}
