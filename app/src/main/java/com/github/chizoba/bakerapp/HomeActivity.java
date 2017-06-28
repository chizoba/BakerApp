package com.github.chizoba.bakerapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.chizoba.bakerapp.IdlingResource.SimpleIdlingResource;
import com.github.chizoba.bakerapp.adapter.HomeAdapter;
import com.github.chizoba.bakerapp.model.Recipe;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements HomeAdapter.ListItemClickListener, RecipeDownloader.DelayerCallback {

    private static final int SPAN_COUNT = 2;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView mRecyclerView;
    protected HomeAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar mProgressBar;
    private TextView mErrorMessageDisplay;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected ArrayList<Recipe> mRecipes = new ArrayList<>();

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_home);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recipes);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mErrorMessageDisplay = (TextView) findViewById(R.id.text_view_error);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        // if in landscape mode, change layout manager to grid layout
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
        } else {
            setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RecipeDownloader.downloadRecipe(HomeActivity.this, HomeActivity.this, mIdlingResource);
            }
        });
        Toolbar homeToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(homeToolbar);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("RECIPE_CONTENTS")) {
                ArrayList<Recipe> recipes = savedInstanceState
                        .getParcelableArrayList("RECIPE_CONTENTS");
                mAdapter = new HomeAdapter(recipes, this);
                mRecyclerView.setAdapter(mAdapter);
            }
        } else {

//        // Set HomeAdapter as the adapter for RecyclerView.
            mAdapter = new HomeAdapter(mRecipes, this);
            mRecyclerView.setAdapter(mAdapter);
            mProgressBar.setVisibility(View.VISIBLE);

            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.INVISIBLE);

            // Get the IdlingResource instance
            getIdlingResource();
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * We call ImageDownloader.downloadImage from onStart or onResume instead of in onCreate
     * to ensure there is enougth time to register IdlingResource if the download is done
     * too early (i.e. in onCreate)
     */
    @Override
    protected void onStart() {
        super.onStart();
        RecipeDownloader.downloadRecipe(this, HomeActivity.this, mIdlingResource);
    }

    @Override
    public void onDone(ArrayList<Recipe> recipes) {
        mRecipes = recipes;
        mSwipeRefreshLayout.setRefreshing(false);
        showDataView();
        mAdapter.refill(recipes);
    }

    @Override
    public void onFailed(Throwable t) {
        Log.e("RecipeDownloader", t.toString());
        mSwipeRefreshLayout.setRefreshing(false);
        showErrorMessage();
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    // custom adapter interface method to handle on list item click
    @Override
    public void onListItemClick(int clickedItemIndex) {
        startActivity(new Intent(this, RecipeDetailsActivity.class).putExtra(Intent.EXTRA_TEXT, mRecipes.get(clickedItemIndex)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                RecipeDownloader.downloadRecipe(this, HomeActivity.this, mIdlingResource);
                return true;
        }

        // you didn't trigger any option. let the superclass handle this action
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Recipe> recipesContents = mRecipes;
        outState.putParcelableArrayList("RECIPE_CONTENTS", recipesContents);
    }
}
