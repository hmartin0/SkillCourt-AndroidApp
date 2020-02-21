package com.skillcourt.ui.main;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
    String myDeleteID;

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
                deleteTrashIcon(menu);
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
                        deleteSectionDialog(deleteID);
                        sessionAdapter.myRemove(position);

                        break;
                }
                return false;
            }
        });


        /****************************************************************************/
        /*Display Player individual data after session clicked*/
        user_stats_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                SessionData content = sessionDataItemsList.get(i);
                String getClickSessionID = content.getSessionID();

                getActivity().findViewById(R.id.dataListView).setVisibility(View.GONE);
                getActivity().findViewById(R.id.playerDataListView).setVisibility(View.VISIBLE);

                //Cursor playerRes = mainActivity.myDB.getAllPlayerData();
                Cursor playerRes = mainActivity.myDB.getPlayerSessionDATA(getClickSessionID);
                playerDataItemsList = new ArrayList<>();

                if(playerRes.getCount() == 0)
                {
                    Toast.makeText(getActivity(), "No Save Data Has Been Added For this Session!", Toast.LENGTH_SHORT).show();
                }

                while(playerRes.moveToNext())
                {
                    String idStats = playerRes.getString(0);
                    String dateStats = playerRes.getString(1);
                    String gTimeStats = playerRes.getString(2);
                    String scoreStats = playerRes.getString(3);
                    String hitStats = playerRes.getString(4);
                    String sessionPlayerIDStats = playerRes.getString(5);

                    PlayerData stats = new PlayerData(idStats,dateStats,gTimeStats,scoreStats,hitStats, sessionPlayerIDStats);
                    playerDataItemsList.add(stats);

                }

                playerAdapter = new StatsListAdapter(Objects.requireNonNull(getActivity()), R.layout.adapter_view_layout, playerDataItemsList);

                player_stats_listView.setAdapter(playerAdapter);

                // Swipe to delete menu
                SwipeMenuCreator creator = new SwipeMenuCreator() {

                    @Override
                    public void create(SwipeMenu menu) {
                        deleteTrashIcon(menu);
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

    public void deleteTrashIcon(SwipeMenu menu)
    {
        SwipeMenuItem deleteItem = new SwipeMenuItem(
                mainActivity.getApplicationContext());

        deleteItem.setBackground(R.drawable.deletebutton_bg);

        deleteItem.setWidth(200);
        deleteItem.setIcon(R.drawable.ic_garbage);

        menu.addMenuItem(deleteItem);
    }

    public ArrayList<SessionData> getPlayerSessionData()
    {
        Cursor res = mainActivity.myDB.getAllSessionData();
        ArrayList<SessionData> tempList = new ArrayList<>();

        if(res.getCount() == 0)
        {
            Toast.makeText(getActivity(), "No Session Has Been Added!", Toast.LENGTH_SHORT).show();
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

    public void deleteSectionDialog(String deleteID)
    {
        myDeleteID = deleteID;

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle("WARNING! Deleting Session: " + myDeleteID );
        myAlertDialog.setMessage("This will also DELETE all data in this Session!");
        myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                Integer data = mainActivity.myDB.deletePlayerInSessionData(myDeleteID);
                Integer deletedRows = mainActivity.myDB.deleteSessionData(myDeleteID);

                if(data > 0)
                {
                    Toast.makeText(getActivity(), "All data in session deleted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "Session has no data", Toast.LENGTH_SHORT).show();
                }

                if(deletedRows > 0)
                {
                    Toast.makeText(getActivity(), "Session Deleted!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "Session Not Deleted!", Toast.LENGTH_SHORT).show();
                }

            }});
        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
            }});
        myAlertDialog.show();
    }
}
