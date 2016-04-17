package com.jawnnypoo.openmeh.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Shows the deal
 */
public class DealFragment extends Fragment {

    private static final String ARG_MEH_RESPONSE = "meh_response";

    public static DealFragment newInstance(TinyMehResponse mehResponse) {
         DealFragment dealFragment = new DealFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MEH_RESPONSE, Parcels.wrap(mehResponse));
        dealFragment.setArguments(args);
        return dealFragment;
    }

    @Bind(R.id.image)
    ImageView mImage;
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
        TinyMehResponse mehResponse = Parcels.unwrap(getArguments().getParcelable(ARG_MEH_RESPONSE));
        bind(mehResponse);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void bind(@NonNull TinyMehResponse response) {
        mTextTitle.setText(response.getTitle());
        mTextPrice.setText(response.getPriceRange());
    }
}
