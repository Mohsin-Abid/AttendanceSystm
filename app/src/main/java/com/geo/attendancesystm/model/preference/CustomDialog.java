package com.geo.attendancesystm.model.preference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.geo.attendancesystm.R;

public class CustomDialog {
    Context context;
    AlertDialog dialog;

    public CustomDialog(Context context) {
        this.context = context;
    }

    public void showCustomDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View customLayout= LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout,null);

        builder.setView(customLayout);

        final TextView yesBtn = customLayout.findViewById(R.id.yes);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog
                = builder.create();
        dialog.show();
    }
}
