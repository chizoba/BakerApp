package com.github.chizoba.bakerapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chizoba.bakerapp.R;
import com.github.chizoba.bakerapp.model.Ingredient;
import com.github.chizoba.bakerapp.model.Recipe;
import com.github.chizoba.bakerapp.model.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Chizoba on 6/9/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecipeAdapter";

    // recipes data
    private List<Object> items = new ArrayList<>();

    // values to specify what view type is passed to this adapter
    private final int INGREDIENT = 0, STEP = 1;

    /**
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private ListItemClickListener mOnClickListener;

    public RecipeAdapter() {

    }

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolderIngredient extends RecyclerView.ViewHolder {
        private final TextView ingredientName, ingredientQuantity, ingredientMeasure;

        public ViewHolderIngredient(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int clickedPosition = getAdapterPosition();
//                    mOnClickListener.onListItemClick(clickedPosition);
//                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
//                }
//            });
            ingredientName = (TextView) v.findViewById(R.id.ingredient_name);
            ingredientQuantity = (TextView) v.findViewById(R.id.ingredient_quantity);
            ingredientMeasure = (TextView) v.findViewById(R.id.ingredient_measure);

        }

        public TextView getIngredientName() {
            return ingredientName;
        }

        public TextView getIngredientQuantity() {
            return ingredientQuantity;
        }

        public TextView getIngredientMeasure() {
            return ingredientMeasure;
        }

    }

    public class ViewHolderStep extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolderStep(View v) {
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
            textView = (TextView) v.findViewById(R.id.tv_step_number);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    /**
     * Initialize the dataset of the Adapter initializes the dataset of the Adapter and the specification
     * for the ListItemClickListener.
//     *  @param dataSet  Number of items to display in list
     * @param listener Listener*/
    public RecipeAdapter(ListItemClickListener listener) {
//        items.addAll(dataSet.getSteps()); //dataSet.getSteps();
        mOnClickListener = listener;
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Ingredient) {
            return INGREDIENT;
        } else if (items.get(position) instanceof Step) {
            return STEP;
        }
        return -1;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case INGREDIENT:
                View v1 = inflater.inflate(R.layout.ingredient_item, viewGroup, false);
                viewHolder = new ViewHolderIngredient(v1);
                break;
            case STEP:
                View v2 = inflater.inflate(R.layout.step_item, viewGroup, false);
                viewHolder = new ViewHolderStep(v2);
                break;
            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
//                viewHolder = new RecyclerViewSimpleTextViewHolder(v);
                break;
        }
        return viewHolder;
//        View v = LayoutInflater.from(viewGroup.getContext())
//                .inflate(R.layout.step_item, viewGroup, false);
//
//        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case INGREDIENT:
                ViewHolderIngredient vh1 = (ViewHolderIngredient) viewHolder;
                configureViewHolderIngredient(vh1, position);
                break;
            case STEP:
                ViewHolderStep vh2 = (ViewHolderStep) viewHolder;
                configureViewHolderStep(vh2, position);
                break;
            default:
//                RecyclerViewSimpleTextViewHolder vh = (RecyclerViewSimpleTextViewHolder) viewHolder;
//                configureDefaultViewHolder(vh, position);
                break;
        }
//        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
//        viewHolder.getTextView().setText("Step #" + String.valueOf(items.get(position).getId()+1));
    }

//    private void configureDefaultViewHolder(RecyclerViewSimpleTextViewHolder vh, int position) {
//        vh.getLabel().setText((CharSequence) items.get(position));
//    }

    private void configureViewHolderIngredient(ViewHolderIngredient vh1, int position) {
        Ingredient ingredient = (Ingredient) items.get(position);
        if (ingredient != null) {
            vh1.getIngredientName().setText(ingredient.getIngredient());
            vh1.getIngredientQuantity().setText("Quantity: " + String.valueOf(((Ingredient) items.get(position)).getQuantity()));
            vh1.getIngredientMeasure().setText("Measure: " + ingredient.getMeasure());
        }
    }

    private void configureViewHolderStep(ViewHolderStep vh2, int position) {
        vh2.getTextView().setText("Step " + String.valueOf(((Step) items.get(position)).getId()+1) + ": " + ((Step) items.get(position)).getShortDescription());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }


    public void refill(Recipe recipes, int type) {
        if (items != null) {
            items.clear();
        }
        if(type == INGREDIENT){
            items.addAll(recipes.getIngredients());
            notifyDataSetChanged();
        } else if(type == STEP) {
            items.addAll(recipes.getSteps());
            notifyDataSetChanged();
        } else {
//            Toast.makeText(, "No data", Toast.LENGTH_SHORT).show();
        }
    }
}
