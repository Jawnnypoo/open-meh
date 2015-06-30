package com.jawnnypoo.openmeh;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;

import butterknife.ButterKnife;

/**
 * Created by Jawn on 6/22/2015.
 */
public class NotifyIfDialog extends AppCompatDialog {

    public NotifyIfDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_about);
        ButterKnife.bind(this);
    }
}
