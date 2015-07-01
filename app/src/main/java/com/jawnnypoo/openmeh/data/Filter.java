package com.jawnnypoo.openmeh.data;

/**
 * Created by Jawn on 6/30/2015.
 */
public class Filter {
    String filter;
    boolean applied;

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
