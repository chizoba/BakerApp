package com.github.chizoba.bakerapp;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.github.chizoba.bakerapp.model.Ingredient;

import java.util.ArrayList;

import static com.github.chizoba.bakerapp.RecipeWidgetService.listItemList;

/**
 * Created by Chizoba on 6/22/2017.
 */

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    ArrayList<Ingredient> ingredients = new ArrayList<>();
    Context mContext;

    public ListRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
//        populateListItem();
    }

    @Override
    public void onDataSetChanged() {
        populateListItem();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (ingredients == null) return 0;
        return ingredients.size();


    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (ingredients == null || ingredients.size() == 0) return null;

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget);

        views.setTextViewText(R.id.widget_recipe_title, ingredients.get(position).getIngredient());

        return views;

    }

    private void populateListItem() {
        if (listItemList != null) {
            ingredients.addAll(listItemList.get(0).getIngredients());
        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
