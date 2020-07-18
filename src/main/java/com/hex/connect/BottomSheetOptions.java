package com.hex.connect;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetOptions extends BottomSheetDialogFragment {
    bottomSheetListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_options,container,false);

        LinearLayout btn1 = view.findViewById(R.id.btn1);
        LinearLayout btn2 = view.findViewById(R.id.btn2);
        LinearLayout btn3 = view.findViewById(R.id.btn3);
        LinearLayout btn4 = view.findViewById(R.id.btn4);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTextClicked(1);
                dismiss();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTextClicked(2);
                dismiss();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTextClicked(3);
                dismiss();
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTextClicked(4);
                dismiss();
            }
        });
        return view;
    }

    public interface bottomSheetListener{
        void onTextClicked(int i);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (bottomSheetListener) context;
    }
}
