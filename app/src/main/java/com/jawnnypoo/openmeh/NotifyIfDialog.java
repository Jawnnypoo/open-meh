package com.jawnnypoo.openmeh;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.jawnnypoo.openmeh.data.Filter;
import com.jawnnypoo.openmeh.viewHolders.FilterViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Shut up!
 * Created by Jawn on 6/22/2015.
 */
public class NotifyIfDialog extends AppCompatDialog {

    @Bind(R.id.list)
    RecyclerView list;

    public NotifyIfDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_notify_if);
        ButterKnife.bind(this);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(new FiltersAdapter(getFilters()));
    }

    private List<Filter> getFilters() {
        ArrayList<Filter> filters = new ArrayList<>();
        filters.add(new Filter("phone", true));
        filters.add(new Filter("plant", true));
        filters.add(new Filter("car", true));
        return filters;
    }

    public class FiltersAdapter extends RecyclerView.Adapter<FilterViewHolder> {

        private final View.OnClickListener itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag(R.id.list_position);
                Filter filter = getValueAt(position);
                filter.setApplied(!filter.isApplied());
                notifyItemChanged(position);
            }
        };

        private List<Filter> mValues;

        public Filter getValueAt(int position) {
            return mValues.get(position);
        }

        public FiltersAdapter(List<Filter> items) {
            mValues = items;
        }

        @Override
        public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FilterViewHolder holder = FilterViewHolder.newInstance(parent);
            holder.itemView.setOnClickListener(itemClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(final FilterViewHolder holder, int position) {
            Filter filter = getValueAt(position);
            holder.bind(filter);
            holder.itemView.setTag(R.id.list_position, position);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
