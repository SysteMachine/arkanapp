package com.example.android.arkanoid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class singin_fragment extends Fragment implements View.OnFocusChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singin_fragment, container, false);
        view.requestFocus();
        view.setOnFocusChangeListener(this);
        return view;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        System.out.println("Cambio il focus");
    }
}