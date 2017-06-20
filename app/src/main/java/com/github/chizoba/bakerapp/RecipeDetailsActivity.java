package com.github.chizoba.bakerapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.github.chizoba.bakerapp.model.Recipe;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

public class RecipeDetailsActivity extends AppCompatActivity
        implements MasterRecipesDetailsFragment.OnRecipeClickListener,
        StepFragment.OnFragmentInteractionListener {

    private Recipe mRecipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_recipe_details);

        if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            mRecipe = getIntent().getParcelableExtra(Intent.EXTRA_TEXT);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.master_list_container, new MasterRecipesDetailsFragment().newInstance(mRecipe))
                    .commit();
        }

    }

    @Override
    public void onIngredientsSelected(View view, Recipe recipe) {
        if (view.getId() == R.id.card_view_ingredients) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.master_list_container, new IngredientsListFragment().newInstance(mRecipe))
                    .addToBackStack(IngredientsListFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public void onStepSelected(int position) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.master_list_container, new StepFragment().newInstance(mRecipe.getSteps().get(position)))
                .addToBackStack(StepFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
