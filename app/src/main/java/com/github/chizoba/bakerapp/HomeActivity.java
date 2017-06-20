package com.github.chizoba.bakerapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.chizoba.bakerapp.adapter.HomeAdapter;
import com.github.chizoba.bakerapp.model.Recipe;
import com.github.chizoba.bakerapp.rest.APIClient;
import com.github.chizoba.bakerapp.rest.APIInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements HomeAdapter.ListItemClickListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                makeAPICall();
            }
        });

        // Set HomeAdapter as the adapter for RecyclerView.
        mAdapter = new HomeAdapter(mRecipes, this);
        mRecyclerView.setAdapter(mAdapter);
        mProgressBar.setVisibility(View.VISIBLE);

//        mRecyclerView.setAdapter(new HomeAdapter((ArrayList<Recipe>) recipes, HomeActivity.this));
        makeAPICall();
    }

    private void makeAPICall() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
//        mProgressBar.setVisibility(View.VISIBLE);

        APIInterface mAPIService =
                APIClient.getClient().create(APIInterface.class);

        Call<List<Recipe>> call = mAPIService.getBakingRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                List<Recipe> recipes = response.body();
                Log.d(TAG, "Number of movies received: " + recipes.size());

                mSwipeRefreshLayout.setRefreshing(false);
                showDataView();

//                mRecipes.addAll(recipes);
                mAdapter.refill((ArrayList<Recipe>) recipes);

            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, t.toString());
                mSwipeRefreshLayout.setRefreshing(false);
                showErrorMessage();
            }
        });
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
                makeAPICall();
                return true;
        }

        // you didn't trigger any option. let the superclass handle this action
        return super.onOptionsItemSelected(item);
    }
}
