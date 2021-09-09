package com.example.android.arkanoid.FragmentMenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.arkanoid.R;

public class pausa_generica_fragment extends Fragment {
    private int tipoPausa;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pausa_generica_fragment, container, false);
    }


}