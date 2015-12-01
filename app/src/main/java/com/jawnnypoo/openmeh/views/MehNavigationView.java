package com.jawnnypoo.openmeh.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.activities.AboutActivity;
import com.jawnnypoo.openmeh.activities.MehActivity;
import com.jawnnypoo.openmeh.activities.NotificationActivity;
import com.jawnnypoo.openmeh.data.Theme;
import com.jawnnypoo.openmeh.util.IntentUtil;
import com.jawnnypoo.openmeh.util.NavigationManager;

import butterknife.ButterKnife;

/**
 * Custom navigation view for all the fun
 * Created by Jawn on 12/1/2015.
 */
public class MehNavigationView extends NavigationView{

    private Theme mTheme;

    private final OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_meh:
                    if (getContext() instanceof MehActivity) {

                    } else {
                        NavigationManager.navigateToMeh((Activity) getContext());
                        ((Activity) getContext()).finish();
                    }
                    //TODO close the drawer
                    //GitLabApp.bus().post(new CloseDrawerEvent());
                    return true;
                case R.id.nav_notifications:
                    if (getContext() instanceof NotificationActivity) {

                    } else {
                        NavigationManager.navigateToNotifications((Activity) getContext(), mTheme);
                        ((Activity) getContext()).finish();
                    }
                    return true;
                case R.id.nav_account:
                    int color = Color.WHITE;
                    if (mTheme != null) {
                        color = mTheme.getAccentColor();
                    }
                    IntentUtil.openUrl((Activity) getContext(), getContext().getString(R.string.url_account), color);
                    return true;
                case R.id.nav_orders:
                    int colorWhichHasToHaveDifferentNameBecauseOfSwitchStatmentsWeirdScope = Color.WHITE;
                    if (mTheme != null) {
                        colorWhichHasToHaveDifferentNameBecauseOfSwitchStatmentsWeirdScope = mTheme.getAccentColor();
                    }
                    IntentUtil.openUrl((Activity) getContext(), getContext().getString(R.string.url_orders), colorWhichHasToHaveDifferentNameBecauseOfSwitchStatmentsWeirdScope);
                    return true;
                case R.id.nav_forum:
                    int color2 = Color.WHITE;
                    if (mTheme != null) {
                        color2 = mTheme.getAccentColor();
                    }
                    IntentUtil.openUrl((Activity) getContext(), getContext().getString(R.string.url_forum), color2);
                    return true;
                case R.id.nav_about:
                    Intent aboutIntent = mTheme == null ?
                            AboutActivity.newInstance(getContext()) :
                            AboutActivity.newInstance(getContext(), mTheme);
                    getContext().startActivity(aboutIntent);
                    ((Activity)getContext()).overridePendingTransition(R.anim.fade_in, R.anim.do_nothing);
                    return true;
            }
            return false;
        }
    };

    public MehNavigationView(Context context) {
        super(context);
        init();
    }

    public MehNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MehNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        inflateMenu(R.menu.menu_drawer);
        View header = inflateHeaderView(R.layout.header_drawer);
        ButterKnife.bind(this, header);
        setSelectedNavigationItem();
    }

    private void setSelectedNavigationItem() {
        for (int i=0; i<getMenu().size(); i++) {
            MenuItem menuItem = getMenu().getItem(i);
            if (getContext() instanceof MehActivity && menuItem.getItemId() == R.id.nav_meh) {
                menuItem.setChecked(true);
                return;
            }
            if (getContext() instanceof NotificationActivity && menuItem.getItemId() == R.id.nav_notifications) {
                menuItem.setChecked(true);
                return;
            }
        }
        throw new IllegalStateException("You need to set a selected nav item for this activity");
    }

    public void setTheme(Theme theme) {
        mTheme = theme;
    }
}
