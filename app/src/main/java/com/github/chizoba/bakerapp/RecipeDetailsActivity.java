package com.github.chizoba.bakerapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
        setSupportActionBar(menuToolbar);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            mRecipe = getIntent().getParcelableExtra(Intent.EXTRA_TEXT);
            menuToolbar.setTitle(mRecipe.getName());
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
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
        }

        // you didn't trigger any option. let the superclass handle this action
        return super.onOptionsItemSelected(item);
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
