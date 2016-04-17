package com.jawnnypoo.openmeh.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.view.ActionPage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.commit451.easel.Easel;
import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.shared.model.Theme;
import com.jawnnypoo.openmeh.util.MessageSender;

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

    private MessageSender mMessageSender;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null) {
            if (getParentFragment() instanceof MessageSender) {
                mMessageSender = (MessageSender) getParentFragment();
                return;
            }
        } else if (context instanceof MessageSender) {
            mMessageSender = (MessageSender) context;
            return;
        }
        throw new IllegalStateException(getClass().getSimpleName() + " must be attached to a parent that implements " + MessageSender.class.getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public MessageSender getMessageSender() {
        return mMessageSender;
    }
}
