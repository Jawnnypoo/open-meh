package com.jawnnypoo.openmeh.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.view.ActionPage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.commit451.easel.Easel;
import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.shared.model.Theme;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Shows an {@link android.support.wearable.view.ActionPage}
 */
public abstract class ActionPageFragment extends Fragment {

    protected abstract Theme getTheme();
    protected abstract void onActionClicked();
    protected abstract void setTheThings(ActionPage actionPage);

    @Bind(R.id.action_page)
    ActionPage mActionPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_action_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setTheThings(mActionPage);
        Theme theme = getTheme();
        int accentColor = theme.getAccentColor();
        int darkerAccentColor = Easel.getDarkerColor(accentColor);
        mActionPage.setColor(accentColor);
        mActionPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionClicked();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
