package com.github.chizoba.bakerapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeAppWidget extends AppWidgetProvider {

    public static final String DATA_FETCHED = "com.github.chizoba.bakerapp.DATA_FETCHED";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Intent serviceIntent = new Intent(context, RecipeWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            context.startService(serviceIntent);
//            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        RecipeService.startActionDisplayIngredients(context, HomeActivity.mRecipes);
    }

    /**
     * It receives the broadcast as per the action set on intent filters on
     * Manifest.xml once data is fetched from RemotePostService,it sends
     * broadcast and WidgetProvider notifies to change the data the data change
     * right now happens on ListProvider as it takes RemoteFetchService
     * listItemList as data
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(DATA_FETCHED)) {
            int appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(context);
            RemoteViews remoteViews = getIngredientListRemoteView(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    /**
     * Creates and returns the RemoteViews to be displayed in the ListView mode widget
     *
     * @param context The context
     * @return The RemoteViews for the ListView mode widget
     */
    private static RemoteViews getIngredientListRemoteView(Context context, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);
        // Set the ListWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, ListWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.widget_list_view, intent);
        // Set the PlantDetailActivity intent to launch when clicked
        Intent appIntent = new Intent(context, RecipeDetailsActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_view, appPendingIntent);
        // Handle empty gardens
        views.setEmptyView(R.id.widget_list_view, R.id.empty_view);
        return views;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

