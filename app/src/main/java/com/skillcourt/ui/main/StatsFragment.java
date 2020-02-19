package com.skillcourt.ui.main;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.skillcourt.R;
import com.skillcourt.structures.PlayerData;
import com.skillcourt.structures.StatsListAdapter;

import java.util.ArrayList;

/**
 * Created by Joshua Mclendon on 2/6/18.
 * Updated for database Hairon Martin 2/9/2020
 */
public class StatsFragment extends BaseFragment {

    SwipeMenuListView user_stats_list;
    ArrayList<PlayerData> statsDataItemsList;
    StatsListAdapter adapter;

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
        user_stats_list = getActivity().findViewById(R.id.dataListView);

        Cursor res = mainActivity.myDB.getAllData();
        if(res.getCount() == 0)
        {
            Toast.makeText(getActivity(), "No PlayerData Has Been Added!", Toast.LENGTH_SHORT).show();
        }

        StringBuffer buffer = new StringBuffer();
        statsDataItemsList = new ArrayList<>();

        while(res.moveToNext())
        {
            String idStats = res.getString(0);
            String dateStats = res.getString(1);
            String gTimeStats = res.getString(2);
            String scoreStats = res.getString(3);
            String hitStats = res.getString(4);

            PlayerData stats = new PlayerData(idStats,dateStats,gTimeStats,scoreStats,hitStats);
            statsDataItemsList.add(stats);

            /*statsDataItemsList.add(" " +res.getString(0) + " " +
                    res.getString(1) + " " +
                    res.getString(2) + " " +
                    res.getString(3) + " " +
                    res.getString(4) + "\n");


            statsDataItemsList.add("ID: " +res.getString(0) + " " +
                    "DATE: " +res.getString(1) + " " +
                    "TIME: " +res.getString(2) + " " +
                    "Score: " +res.getString(3) + " " +
                    "HIT: " +res.getString(4) + "\n");
             */
        }

        adapter = new StatsListAdapter(getActivity(), R.layout.adapter_view_layout, statsDataItemsList);

        user_stats_list.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        mainActivity.getApplicationContext());
                // set item background

                deleteItem.setBackground(R.drawable.deletebutton_bg);
                // set item width
                deleteItem.setWidth(200);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_garbage);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        user_stats_list.setMenuCreator(creator);

        user_stats_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch (index) {
                    case 0:
                        //String content = statsDataItemsList.get(position);

                        PlayerData content = statsDataItemsList.get(position);

                        //String[] elements = content.split(" ");
                        //String deleteID = elements[1];

                        String deleteID = content.getId();
                        //Delete from database
                        Integer deletedRows = mainActivity.myDB.deleteData(deleteID);

                        adapter.myRemove(position);
                        //adapter.notifyDataSetChanged();

                        if(deletedRows > 0)
                        {
                            Toast.makeText(getActivity(), "Player's Data Deleted!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Player's Data Not Deleted!", Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
                return false;
            }
        });

    }
}
