package com.baking_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baking_app.R;
import com.baking_app.activities.RecipeInfoActivity;
import com.baking_app.model.Ingredient;
import com.baking_app.model.Recipe;
import com.baking_app.model.Step;
import com.baking_app.utils.AppConstants;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context mContext;
    private List<Recipe> mRecipeList;
    private ArrayList<Ingredient> mIngredientList;
    private ArrayList<Step> mStepList;
    private SharedPreferences mSharedPreferences;
    private Gson gson;

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.mContext = context;
        this.mRecipeList = recipeList;
        mSharedPreferences = mContext.getSharedPreferences(AppConstants.SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        gson=new Gson();
    }


    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recipe_list, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, final int position) {

        final Recipe recipe= mRecipeList.get(position);

        holder.mTvRecipeName.setText(recipe.getName());

        String imageUrl = recipe.getImage();

        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(mContext).load(imageUrl).into(holder.mIvPlaceholder);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIngredientList = recipe.getIngredients();
                mStepList = recipe.getSteps();


                Intent intent = new Intent(mContext, RecipeInfoActivity.class);
                intent.putParcelableArrayListExtra(AppConstants.KEY_INGREDIENTS, mIngredientList);
                intent.putParcelableArrayListExtra(AppConstants.KEY_STEPS, mStepList);
                mContext.startActivity(intent);

                String resultJson = gson.toJson(recipe);
                mSharedPreferences.edit().putString(AppConstants.WIDGET_RESULT, resultJson).apply();


            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }


    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView mTvRecipeName;
        AppCompatImageView mIvPlaceholder;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            mTvRecipeName =itemView.findViewById(R.id.tv_recipe_name);
            mIvPlaceholder =itemView.findViewById(R.id.placeholder_recipe);
        }
    }
}
