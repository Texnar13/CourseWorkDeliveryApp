package com.texnar13.deliveryapp.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.texnar13.deliveryapp.R;

public class ExpeditionsFragment extends Fragment {


    // Required empty public constructor
    public ExpeditionsFragment() {
    }

    // factory method
    public static ExpeditionsFragment newInstance(String param1) {
        ExpeditionsFragment fragment = new ExpeditionsFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expeditions, container, false);
    }
}