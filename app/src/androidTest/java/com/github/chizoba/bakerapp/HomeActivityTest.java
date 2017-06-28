package com.github.chizoba.bakerapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Chizoba on 6/25/2017.
 */
@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    public static final String RECIPE_NAME = "Brownies";

    @Rule
    public ActivityTestRule<HomeActivity> homeActivityActivityTestRule = new ActivityTestRule<HomeActivity>(HomeActivity.class);

    private IdlingResource mIdlingResource;


    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    public void registerIdlingResource() {
        mIdlingResource = homeActivityActivityTestRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        Espresso.registerIdlingResources(mIdlingResource);
    }

    /**
     * Clicks on a List item and checks it opens up the RecipeDetailsActivity with the correct details.
     */
    @Test
    public void clickListViewItem_OpensRecipeDetailsActivity() {

        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific
        // recycler view item and clicks it.
        onView(withId(R.id.rv_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        // Checks that the HomeActivity opens with the correct recipe name displayed
        onView(withId(R.id.toolbar_title)).check(matches(withText(RECIPE_NAME)));

    }

    // Remember to unregister resources when not needed to avoid malfunction.
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
