package com.jawnnypoo.bleh;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jawnnypoo.bleh.data.Deal;
import com.jawnnypoo.bleh.data.Theme;
import com.jawnnypoo.bleh.service.MehClient;
import com.jawnnypoo.bleh.service.MehResponse;
import com.jawnnypoo.bleh.util.ColorUtil;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.deal_image_view_pager)
    ViewPager imageViewPager;

    @InjectView(R.id.deal_title)
    TextView title;

    @InjectView(R.id.deal_description)
    TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        MehClient.instance().getMeh(new Callback<MehResponse>() {
            @Override
            public void success(MehResponse mehResponse, Response response) {
                bindDeal(mehResponse.getDeal());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void bindDeal(Deal deal) {
        title.setText(deal.getTitle());
        description.setText(deal.getFeatures());
        imageViewPager.setAdapter(new ImageAdapter(this, deal.getPhotos()));
        bindTheme(deal.getTheme());
    }

    private void bindTheme(Theme theme) {
        int accentColor = Color.parseColor(theme.getAccentColor());
        int backgroundColor = Color.parseColor(theme.getBackgroundColor());
        title.setTextColor(accentColor);
        toolbar.setBackgroundColor(accentColor);
        ColorUtil.setStatusBarAndNavBarColor(getWindow(), ColorUtil.getDarkerColor(accentColor));
        getWindow().getDecorView().setBackgroundColor(backgroundColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class ImageAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<String> mData = new ArrayList<>();

        public ImageAdapter(Context context, Collection<String> data) {
            mContext = context;
            mData.addAll(data);
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_deal_image, collection, false);
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
            Glide.with(mContext)
                    .load(mData.get(position))
                    .into(imageView);

            collection.addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
