package com.baking_app.networking;

import com.baking_app.model.Recipe;
import com.baking_app.utils.AppConstants;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BakingAPI {

    @GET(AppConstants.GET_URL)
    Call<List<Recipe>> getRecipeData();
}
