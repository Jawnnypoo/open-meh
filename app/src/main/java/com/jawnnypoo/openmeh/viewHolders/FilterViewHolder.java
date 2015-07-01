package com.jawnnypoo.openmeh.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.data.Filter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jawn on 6/30/2015.
 */
public class FilterViewHolder extends RecyclerView.ViewHolder{

    public static FilterViewHolder newInstance(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);
        return new FilterViewHolder(view);
    }

    @Bind(R.id.filter_checkbox)
    public CheckBox filterCheckbox;

    public FilterViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(Filter filter) {
        filterCheckbox.setText(filter.getFilter());
        filterCheckbox.setChecked(filter.isApplied());
    }
}
