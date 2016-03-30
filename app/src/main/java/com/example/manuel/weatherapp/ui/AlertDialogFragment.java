package com.example.manuel.weatherapp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Manuel on 18/03/2016.
 */
public class AlertDialogFragment extends android.app.DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context ctx = getActivity();

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ctx);
        builder.setTitle("OOOpps, Sorry");
        builder.setMessage("Pleaseee, try again");
        builder.setPositiveButton("OK", null);

        android.support.v7.app.AlertDialog dialog = builder.create();
        return dialog;
    }
}
