package com.skillcourt.ui.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skillcourt.R;

/**
 * Created by Joshua Mclendon on 2/6/18.
 */
public class SequencesFragment extends BaseFragment {


    public SequencesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity.setTitle("Sequences");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sequences, container, false);
    }

}
