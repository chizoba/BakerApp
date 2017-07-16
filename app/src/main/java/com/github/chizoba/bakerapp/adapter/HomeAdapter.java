package com.github.chizoba.bakerapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chizoba.bakerapp.R;
import com.github.chizoba.bakerapp.model.Recipe;
import com.github.chizoba.bakerapp.model.Step;

import java.util.ArrayList;

/**
 * Created by Chizoba on 6/2/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private static final String TAG = "HomeAdapter";

    // recipes data
    private ArrayList<Recipe> mRecipe;

    Context mContext;
    /**
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    final private ListItemClickListener mOnClickListener;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ImageView imageView;
        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    mOnClickListener.onListItemClick(clickedPosition);
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            textView = (TextView) v.findViewById(R.id.recipe_name);
            imageView = (ImageView) v.findViewById(R.id.recipe_image);
        }

        public TextView getTextView() {
            return textView;
        }
        public ImageView getImageView() {
            return imageView;
        }

    }

    /**
     * Initialize the dataset of the Adapter initializes the dataset of the Adapter and the specification
     * for the ListItemClickListener.
     *
     * @param dataSet  Number of items to display in list
     * @param listener Listener for list item clicks
     */
    public HomeAdapter(Context context, ArrayList<Recipe> dataSet, ListItemClickListener listener) {
        mContext = context;
        mRecipe = dataSet;
        mOnClickListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recipe_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTextView().setText(mRecipe.get(position).getName());
        Glide.with(mContext).load(mRecipe.get(position).getImage()).centerCrop()
                .placeholder(R.mipmap.ic_launcher_round).into(viewHolder.getImageView());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mRecipe.size();
    }

    public void refill(ArrayList<Recipe> recipes) {
        if (mRecipe != null) {
            mRecipe.clear();
        }
        mRecipe.addAll(recipes);
        notifyDataSetChanged();
    }
}
