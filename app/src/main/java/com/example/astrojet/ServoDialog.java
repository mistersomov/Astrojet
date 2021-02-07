package com.example.astrojet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class ServoDialog extends AppCompatDialogFragment {
    private static final int DEFAULT_VALUE = 0;
    private MaterialTextView xValue, yValue;
    private MaterialButton xMinus, xPlus, yMinus, yPlus;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.servo_layout, null);
        xValue = v.findViewById(R.id.x_value);
        yValue = v.findViewById(R.id.y_value);
        xMinus = v.findViewById(R.id.x_minus);
        xPlus = v.findViewById(R.id.x_plus);
        yMinus = v.findViewById(R.id.y_minus);
        yPlus = v.findViewById(R.id.y_plus);
        builder.setView(v)
                .setTitle("Adjusting")
                .setNeutralButton("RESET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        xValue.setText(String.valueOf(DEFAULT_VALUE));
                        yValue.setText(String.valueOf(DEFAULT_VALUE));
                    }
                });

        xMinus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        substracting(xMinus, xPlus, xValue, v);
                    }
                }
        );

        xPlus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adding(xMinus, xPlus, xValue, v);
                    }
                }
        );

        yMinus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        substracting(yMinus, yPlus, yValue, v);
                    }
                }
        );

        yPlus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adding(yMinus, yPlus, yValue, v);
                    }
                }
        );
        return builder.create();
    }

    public void substracting(
            MaterialButton negativeButton,
            MaterialButton positiveButton,
            MaterialTextView value,
            View v){
        int floor = -3;
        int convert = new Integer(value.getText().toString()).intValue();

        positiveButton.setEnabled(true);
        if (convert > floor + 1){
            convert--;

            value.setText(String.valueOf(convert));
        }else{
            value.setText(String.valueOf(floor));
            negativeButton.setEnabled(false);

        }
    }

    public void adding(
            MaterialButton negativeButton,
            MaterialButton positiveButton,
            MaterialTextView value,
            View v){
        int ceil = 3;
        int convert = new Integer(value.getText().toString()).intValue();

        negativeButton.setEnabled(true);
        if (convert < ceil - 1){
            convert++;

            value.setText(String.valueOf(convert));
        }else {
            value.setText(String.valueOf(ceil));
            positiveButton.setEnabled(false);

        }
    }
}
