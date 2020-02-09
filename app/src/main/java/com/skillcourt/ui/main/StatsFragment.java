package com.skillcourt.ui.main;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skillcourt.R;

/**
 * Created by Joshua Mclendon on 2/6/18.
 * Updated for database Hairon Martin 2/9/2020
 */
public class StatsFragment extends BaseFragment {

    TextView user_stats;

    public StatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity.setTitle("Statistics");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user_stats = getActivity().findViewById(R.id.userStatsData);

        Cursor res = mainActivity.myDB.getAllData();
        if(res.getCount() == 0)
        {
            Toast.makeText(getActivity(), "No Data Has Been Added!", Toast.LENGTH_SHORT).show();
        }

        StringBuffer buffer = new StringBuffer();

        while(res.moveToNext())
        {
            buffer.append("ID: " +res.getString(0) + " ");
            buffer.append("DATE: " +res.getString(1) + " ");
            buffer.append("TIME: " +res.getString(2) + " ");
            buffer.append("Score: " +res.getString(3) + " ");
            buffer.append("HIT: " +res.getString(4) + "\n");
        }

        user_stats.setText(buffer.toString());

    }
}
