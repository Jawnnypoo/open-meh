package com.jawnnypoo.openmeh.data;

import org.parceler.Parcel;

/**
 * An internal way of keeping track of filters that have been applied
 * Created by Jawn on 6/30/2015.
 */
@Parcel
public class Filter {
    String filter;
    boolean applied;

    public Filter(){}

    public Filter(String filter, boolean applied) {
        this.filter = filter;
        this.applied = applied;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }
}
