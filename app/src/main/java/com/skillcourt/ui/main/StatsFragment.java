package com.skillcourt.ui.main;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.skillcourt.R;
import com.skillcourt.structures.PlayerData;
import com.skillcourt.structures.SessionData;
import com.skillcourt.structures.SessionListAdapter;
import com.skillcourt.structures.StatsListAdapter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Joshua Mclendon on 2/6/18.
 * Updated for database Hairon Martin 2/9/2020
 */
public class StatsFragment extends BaseFragment {

    SwipeMenuListView user_stats_listView;
    SwipeMenuListView player_stats_listView;
    ArrayList<PlayerData> playerDataItemsList;
    ArrayList<SessionData> sessionDataItemsList;
    StatsListAdapter playerAdapter;
    SessionListAdapter sessionAdapter;

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

    public ArrayList<SessionData> getPlayerSessionData()
    {
        Cursor res = mainActivity.myDB.getAllSessionData();
        ArrayList<SessionData> tempList = new ArrayList<>();

        if(res.getCount() == 0)
        {
            Toast.makeText(getActivity(), "No Player Data Has Been Added!", Toast.LENGTH_SHORT).show();
        }

        while(res.moveToNext())
        {
            String idSessionStats = res.getString(0);
            String dateSessionStats = res.getString(1);

            SessionData stats = new SessionData(idSessionStats,dateSessionStats);
            tempList.add(stats);

        }

        return tempList;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user_stats_listView = getActivity().findViewById(R.id.dataListView);
        player_stats_listView = getActivity().findViewById(R.id.playerDataListView);

        /**************************************************************************/
        sessionDataItemsList = new ArrayList<>();
        sessionDataItemsList = getPlayerSessionData();

        sessionAdapter = new SessionListAdapter(getActivity(), R.layout.adapter_session_view_layout, sessionDataItemsList);

        user_stats_listView.setAdapter(sessionAdapter);

        /**************************************************************************/
        // Swipe to delete menu
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        mainActivity.getApplicationContext());

                deleteItem.setBackground(R.drawable.deletebutton_bg);

                deleteItem.setWidth(200);
                deleteItem.setIcon(R.drawable.ic_garbage);

                menu.addMenuItem(deleteItem);
            }
        };

        user_stats_listView.setMenuCreator(creator);

        user_stats_listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch (index) {
                    case 0:

                        SessionData content = sessionDataItemsList.get(position);

                        String deleteID = content.getSessionID();

                        //Delete from database
                        Integer deletedRows = mainActivity.myDB.deleteSessionData(deleteID);

                        sessionAdapter.myRemove(position);

                        if(deletedRows > 0)
                        {
                            Toast.makeText(getActivity(), "Session Deleted!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Session Not Deleted!", Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
                return false;
            }
        });


        /****************************************************************************/
        user_stats_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                System.out.println("POSITION " + i);
                getActivity().findViewById(R.id.dataListView).setVisibility(View.GONE);
                getActivity().findViewById(R.id.playerDataListView).setVisibility(View.VISIBLE);

                Cursor playerRes = mainActivity.myDB.getAllData();

                if(playerRes.getCount() == 0)
                {
                    Toast.makeText(getActivity(), "No Player Data Has Been Added!", Toast.LENGTH_SHORT).show();
                }

                playerDataItemsList = new ArrayList<>();

                while(playerRes.moveToNext())
                {
                    String idStats = playerRes.getString(0);
                    String dateStats = playerRes.getString(1);
                    String gTimeStats = playerRes.getString(2);
                    String scoreStats = playerRes.getString(3);
                    String hitStats = playerRes.getString(4);

                    PlayerData stats = new PlayerData(idStats,dateStats,gTimeStats,scoreStats,hitStats);
                    playerDataItemsList.add(stats);

                }

                playerAdapter = new StatsListAdapter(Objects.requireNonNull(getActivity()), R.layout.adapter_view_layout, playerDataItemsList);

                player_stats_listView.setAdapter(playerAdapter);

                // Swipe to delete menu
                SwipeMenuCreator creator = new SwipeMenuCreator() {

                    @Override
                    public void create(SwipeMenu menu) {

                        SwipeMenuItem deleteItem = new SwipeMenuItem(
                                mainActivity.getApplicationContext());

                        deleteItem.setBackground(R.drawable.deletebutton_bg);

                        deleteItem.setWidth(200);
                        deleteItem.setIcon(R.drawable.ic_garbage);

                        menu.addMenuItem(deleteItem);
                    }
                };

                player_stats_listView.setMenuCreator(creator);

                player_stats_listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                        switch (index) {
                            case 0:

                                PlayerData content = playerDataItemsList.get(position);

                                String deleteID = content.getId();
                                //Delete from database
                                Integer deletedRows = mainActivity.myDB.deleteData(deleteID);

                                playerAdapter.myRemove(position);

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
        });
    }
}


/*
  @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user_stats_listView = getActivity().findViewById(R.id.dataListView);

        Cursor res = mainActivity.myDB.getAllData();
        if(res.getCount() == 0)
        {
            Toast.makeText(getActivity(), "No PlayerData Has Been Added!", Toast.LENGTH_SHORT).show();
        }

        StringBuffer buffer = new StringBuffer();
        playerDataItemsList = new ArrayList<>();

        while(res.moveToNext())
        {
            String idStats = res.getString(0);
            String dateStats = res.getString(1);
            String gTimeStats = res.getString(2);
            String scoreStats = res.getString(3);
            String hitStats = res.getString(4);

            PlayerData stats = new PlayerData(idStats,dateStats,gTimeStats,scoreStats,hitStats);
            playerDataItemsList.add(stats);

            /*playerDataItemsList.add(" " +res.getString(0) + " " +
                    res.getString(1) + " " +
                    res.getString(2) + " " +
                    res.getString(3) + " " +
                    res.getString(4) + "\n");


            playerDataItemsList.add("ID: " +res.getString(0) + " " +
                    "DATE: " +res.getString(1) + " " +
                    "TIME: " +res.getString(2) + " " +
                    "Score: " +res.getString(3) + " " +
                    "HIT: " +res.getString(4) + "\n");
             */
        /*}

                adapter = new StatsListAdapter(getActivity(), R.layout.adapter_view_layout, playerDataItemsList);

                user_stats_listView.setAdapter(adapter);

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

        user_stats_listView.setMenuCreator(creator);

        user_stats_listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
@Override
public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

        switch (index) {
        case 0:
        //String content = playerDataItemsList.get(position);

        PlayerData content = playerDataItemsList.get(position);

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
 */