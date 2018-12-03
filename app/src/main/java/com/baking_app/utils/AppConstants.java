package com.baking_app.utils;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class AppConstants {

    public static final String BASE_URL = "http://d17h27t6h515a5.cloudfront.net/";
    public static final String GET_URL = "/topher/2017/May/59121517_baking/baking.json";

    public static final String KEY_INGREDIENTS = "ingredients";
    public static final String KEY_STEPS = "steps";
    public static final String KEY_STEPS_JSON = "stepListJson";
    public static final String KEY_INGREDIENTS_JSON = "ingredientsListJson";
    public static final String KEY_STEPS_ID = "stepId";
    public static final String KEY_STEPS_DESC = "stepDescription";
    public static final String KEY_STEPS_URL = "stepURL";
    public static final String KEY_PANE = "twoPane";
    public static final String KEY_PANE_VID = "pane";
    public static final String KEY_VISIBILITY_EXO_PLAYER = "exoPlayerVisibility";
    public static final String KEY_VISIBILITY_PLACEHOLDER = "placeHolderVisibility";
    public static final String WIDGET_RESULT = "recipeList";
    public static final String SHARED_PREFERENCES = "MyPrefs";
    public static final String MEDIA_POS = "MEDIA_POSITION";
    public static final String THUMBNAIL_IMAGE = "thumbnailUrl";
    public static final String RECYCLER_VIEW_STATE = "recyclerViewScroll";
    public static final String KEY_ROTATION_VIDEO_ACTIVITY="rotationVideo";
    public static final String KEY_ROTATION_DETAIL_ACTIVITY="rotationDetail";
    public static final String KEY_PLAY_WHEN_READY="playWhenReady";


}
