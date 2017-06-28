package com.github.chizoba.bakerapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.github.chizoba.bakerapp.model.Recipe;
import com.github.chizoba.bakerapp.rest.APIClient;
import com.github.chizoba.bakerapp.rest.APIInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chizoba on 6/27/2017.
 */

public class RecipeWidgetService extends IntentService {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public static ArrayList<Recipe> listItemList = new ArrayList<>();

    public RecipeWidgetService() {
        super("RecipeWidgetService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

        APIInterface mAPIService =
                APIClient.getClient().create(APIInterface.class);

        Call<List<Recipe>> call = mAPIService.getBakingRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                List<Recipe> recipes = response.body();
                listItemList.addAll(recipes);
                populateWidget();
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
            }

        });
    }

    /**
     * Method which sends broadcast to ListRemoteViewsFactory
     * so that widget is notified to do necessary action
     * and here action == ListRemoteViewsFactory.DATA_FETCHED
     */
    private void populateWidget() {

        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(RecipeAppWidget.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        sendBroadcast(widgetUpdateIntent);

    }
}
