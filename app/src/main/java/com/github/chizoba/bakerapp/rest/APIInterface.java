package com.github.chizoba.bakerapp.rest;

import com.github.chizoba.bakerapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Chizoba on 6/4/2017.
 */

public interface APIInterface {
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getBakingRecipes();
}
