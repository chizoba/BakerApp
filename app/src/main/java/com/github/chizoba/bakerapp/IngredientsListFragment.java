package com.github.chizoba.bakerapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chizoba.bakerapp.adapter.RecipeAdapter;
import com.github.chizoba.bakerapp.model.Recipe;

public class IngredientsListFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private Recipe mParam1;

    public IngredientsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment IngredientsListFragment.
     */
    public static IngredientsListFragment newInstance(Recipe param1) {
        IngredientsListFragment fragment = new IngredientsListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingredients_list, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_ingredients);
//        enable optimizations if the items are static and will not change for significantly smoother scrolling:
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecipeAdapter mAdapter = new RecipeAdapter();
        mAdapter.refill(mParam1, 0);
        mRecyclerView.setAdapter(mAdapter);

//        This decorator displays dividers between each item within the list as illustrated below:
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        return view;
    }
}
