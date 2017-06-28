package com.github.chizoba.bakerapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.github.chizoba.bakerapp.model.Recipe;

public class RecipeDetailsActivity extends AppCompatActivity
        implements MasterRecipesDetailsFragment.OnRecipeClickListener,
        StepFragment.OnFragmentInteractionListener {

    public static Recipe mRecipe;

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_recipe_details);

        Toolbar menuToolbar = (Toolbar) findViewById(R.id.recipe_details_toolbar);
        TextView toolbarTextView = (TextView) menuToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(menuToolbar);

        if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            mRecipe = getIntent().getParcelableExtra(Intent.EXTRA_TEXT);

            toolbarTextView.setText(mRecipe.getName());
        }

        // Determine if you're creating a two-pane or single-pane display
        if (findViewById(R.id.tablet_linear_layout) != null) {
            // This LinearLayout will only initially exist in the two-pane tablet case
            mTwoPane = true;

            if (savedInstanceState == null) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.master_list_fragment, new MasterRecipesDetailsFragment().newInstance(mRecipe))
                        .commit();

                fragmentManager.beginTransaction()
                        .replace(R.id.master_list_container, new IngredientsListFragment().newInstance(mRecipe))
                        .commit();
            }
        } else {
            // We're in single-pane mode and displaying fragments on a phone in separate activities
            mTwoPane = false;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.master_list_container, new MasterRecipesDetailsFragment().newInstance(mRecipe))
                    .commit();
        }


    }

    @Override
    public void onIngredientsSelected(View view, Recipe recipe) {

        if (mTwoPane) {
            // Create two=pane interaction
            if (view.getId() == R.id.card_view_ingredients) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_list_container, new IngredientsListFragment().newInstance(mRecipe))
                        .commit();
            }
        } else {
            if (view.getId() == R.id.card_view_ingredients) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_list_container, new IngredientsListFragment().newInstance(mRecipe))
                        .addToBackStack(IngredientsListFragment.class.getSimpleName())
                        .commit();
//            RecipeService.startActionDisplayIngredients(this, mRecipe.getIngredients());
                // this intent is essential to show the widget
                // if this intent is not included,you can't show
                // widget on homescreen
                Intent intent = new Intent();
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.EXTRA_APPWIDGET_ID);
                setResult(Activity.RESULT_OK, intent);

                // start your service
                // to fetch data from web
                Intent serviceIntent = new Intent(this, RecipeWidgetService.class);
                serviceIntent
                        .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.EXTRA_APPWIDGET_ID);
                startService(serviceIntent);
            }
        }
    }

    @Override
    public void onStepSelected(int position) {
        if (mTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.master_list_container, new StepFragment().newInstance(mRecipe.getSteps().get(position)))
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.master_list_container, new StepFragment().newInstance(mRecipe.getSteps().get(position)))
                    .addToBackStack(StepFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
