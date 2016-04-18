package com.jawnnypoo.openmeh.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.model.MehWearResponse;
import com.jawnnypoo.openmeh.shared.model.Theme;
import com.jawnnypoo.openmeh.util.ImageCache;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Shows the deal
 */
public class DealFragment extends Fragment {

    private static final String ARG_MEH_RESPONSE = "meh_response";

    public static DealFragment newInstance(MehWearResponse mehResponse) {
        DealFragment dealFragment = new DealFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MEH_RESPONSE, mehResponse);
        dealFragment.setArguments(args);
        return dealFragment;
    }

    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.root_details)
    ViewGroup mRootDetails;
    @Bind(R.id.title)
    TextView mTextTitle;
    @Bind(R.id.price)
    TextView mTextPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_deal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        MehWearResponse mehResponse = getArguments().getParcelable(ARG_MEH_RESPONSE);
        bind(mehResponse);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void bind(@NonNull MehWearResponse response) {
        mTextTitle.setText(response.getTinyMehResponse().getTitle());
        mTextPrice.setText(response.getTinyMehResponse().getPriceRange());
        if (response.getImageId() != null) {
            Bitmap bitmap = ImageCache.getImage(response.getImageId());
            if (bitmap != null) {
                mImage.setImageBitmap(bitmap);
            }
        }
        bindTheme(response.getTinyMehResponse().getTheme());
    }

    private void bindTheme(Theme theme) {
        int foregroundColor = theme.getForegroundColor();
        mTextTitle.setTextColor(foregroundColor);
        mTextPrice.setTextColor(foregroundColor);
        if (theme.getForeground() == Theme.FOREGROUND_LIGHT) {
            mRootDetails.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.black_60));
        } else {
            mRootDetails.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white_60));
        }
    }
}
