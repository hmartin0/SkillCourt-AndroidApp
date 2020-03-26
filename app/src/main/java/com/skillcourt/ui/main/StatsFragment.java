package com.skillcourt.ui.main;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    private SwipeMenuListView user_stats_listView;
    private SwipeMenuListView player_stats_listView;
    private LinearLayout s_Head_Linear_Layout;
    private LinearLayout p_Head_Linear_Layout;
    private LinearLayout graph_Linear_layout;
    private TextView sessTextHeader;
    private ArrayList<PlayerData> playerDataItemsList;
    private ArrayList<PlayerData> graphHitPlayerDataList;
    private ArrayList<SessionData> sessionDataItemsList;
    private StatsListAdapter playerAdapter;
    private SessionListAdapter sessionAdapter;
    private String myDeleteID;
    private int sessDeletePosition;
    private Boolean noteClicked = true;
    private Boolean graphClicked = true;
    private ImageButton graphButton;

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
        s_Head_Linear_Layout = getActivity().findViewById(R.id.sessHeadingLinearLayout);
        p_Head_Linear_Layout = getActivity().findViewById(R.id.playerHeadingLinearLayout);
        graph_Linear_layout = getActivity().findViewById(R.id.graphLinearLayout);
        sessTextHeader = getActivity().findViewById(R.id.sessionTextHeader);
        graphButton = getActivity().findViewById(R.id.buttonGraph);


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
                deleteTrashIcon(menu, 0);
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
                        deleteSectionDialog(deleteID, position);

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

                user_stats_listView.setVisibility(View.GONE);
                player_stats_listView.setVisibility(View.VISIBLE);
                s_Head_Linear_Layout.setVisibility(View.GONE);
                p_Head_Linear_Layout.setVisibility(View.VISIBLE);

                sessTextHeader.setText("Session: " +getClickSessionID);
                playerDataItemsList = new ArrayList<>();
                playerDataItemsList = getSavedPlayerData(getClickSessionID, "");
                playerAdapter = new StatsListAdapter(Objects.requireNonNull(getActivity()), R.layout.adapter_view_layout, playerDataItemsList);

                player_stats_listView.setAdapter(playerAdapter);

                showGraph(getClickSessionID);

                // Swipe to delete menu
                SwipeMenuCreator creator = new SwipeMenuCreator() {

                    @Override
                    public void create(SwipeMenu menu) {
                        deleteTrashIcon(menu , 1);
                    }
                };

                player_stats_listView.setMenuCreator(creator);

                player_stats_listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                        switch (index) {
                            case 0:
                                updateNotesCase(position);
                                break;
                            case 1:
                                    PlayerData content = playerDataItemsList.get(position);

                                    String deleteID = content.getId();
                                    //Delete from database
                                    Integer deletedRows = mainActivity.myDB.deleteData(deleteID);
                                    playerAdapter.myRemove(position);

                                    if(deletedRows > 0) {
                                        Toast.makeText(getActivity(), "Player's Data Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "Player's Data Not Deleted!", Toast.LENGTH_SHORT).show();
                                    }

                                break;
                        }
                        return false;
                    }
                });

                player_stats_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView noteTextView = view.findViewById(R.id.noteTextView);

                        if(noteClicked){
                            noteTextView.setMaxLines(Integer.MAX_VALUE);
                            noteClicked = false;
                        }
                        else {
                            noteTextView.setMaxLines(1);
                            noteClicked = true;
                        }
                    }
                });
            }
        });
    }


    public void deleteTrashIcon(SwipeMenu menu, int layout)
    {
        if(layout == 1)
        {
            SwipeMenuItem editNotes = new SwipeMenuItem(
                    mainActivity.getApplicationContext());

            editNotes.setBackground(R.drawable.notebutton_bg);

            editNotes.setWidth(200);
            editNotes.setIcon(R.drawable.ic_edit_notes);

            menu.addMenuItem(editNotes);
        }

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

    public ArrayList<PlayerData> getSavedPlayerData(String getClSessionID, String GameTypeSet)
    {
        Cursor playerRes;

        if(GameTypeSet.equals("H"))
        {
            playerRes = mainActivity.myDB.getPlayerSessionGameTypeDATA(getClSessionID,GameTypeSet);
        }
        else
        {
            //Cursor playerRes = mainActivity.myDB.getAllPlayerData();
            playerRes = mainActivity.myDB.getPlayerSessionDATA(getClSessionID);
        }

        ArrayList<PlayerData> tempList = new ArrayList<>();

        if(playerRes.getCount() == 0)
        {
            if(GameTypeSet.equals("H"))
            {
                Toast.makeText(getActivity(), "No HIT Game Type Data Added", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity(), "No Save Data Has Been Added For Session! " + getClSessionID, Toast.LENGTH_SHORT).show();
            }
        }

        while(playerRes.moveToNext())
        {
            String idStats = playerRes.getString(0);
            String dateStats = playerRes.getString(1);
            String gTimeStats = playerRes.getString(2);
            String scoreStats = playerRes.getString(3);
            String hitStats = playerRes.getString(4);
            String sessionPlayerIDStats = playerRes.getString(5);
            String noteStats = playerRes.getString(6);
            String gameType = playerRes.getString(7);

            PlayerData stats = new PlayerData(idStats,dateStats,gTimeStats,scoreStats,hitStats, sessionPlayerIDStats, noteStats, gameType);
            tempList.add(stats);

        }

        return tempList;

    }

    public void deleteSectionDialog(String deleteID, int position)
    {
        myDeleteID = deleteID;
        sessDeletePosition = position;

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle("WARNING! Deleting Session: " + myDeleteID );
        myAlertDialog.setMessage("This will also DELETE all data in this Session!");
        myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {

                Integer data = mainActivity.myDB.deletePlayerInSessionData(myDeleteID);
                Integer deletedRows = mainActivity.myDB.deleteSessionData(myDeleteID);
                sessionAdapter.myRemove(sessDeletePosition);

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

    public void updateNotesCase(int position)
    {
        final PlayerData playerContent = playerDataItemsList.get(position);
        String myNotes = playerContent.getNotes();

        View pView =(LayoutInflater.from(getActivity()).inflate(R.layout.prompts_note, null));
        final EditText userNotes = pView.findViewById(R.id.editNotesEditText);
        userNotes.setText(myNotes);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(pView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String newNotes = userNotes.getText().toString();

                                Boolean updated = mainActivity.myDB.updatePlayerNotes(playerContent.getId(),
                                        playerContent.getDate(),
                                        playerContent.getGTime(),
                                        playerContent.getScore(),
                                        playerContent.getHits(),
                                        playerContent.getSessioPlayerID(),
                                        newNotes,
                                        playerContent.getGameType());

                                playerContent.setNotes(newNotes);
                                playerAdapter.updateData();

                                if(updated == true)
                                {
                                    Toast.makeText(getActivity(), "Notes Updated", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getActivity(), "Notes Not Updated", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }


    public void showGraph(String clickedSessionID)
    {
        final String mySessionID = clickedSessionID;

        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get all hit game type of all games from this session for graph data.
                graphHitPlayerDataList = new ArrayList<>();
                graphHitPlayerDataList = getSavedPlayerData(mySessionID, "H");
                ArrayList<String> datesList = new ArrayList<>();
                ArrayList<String> hitsList = new ArrayList<>();

                for(int i = 0; i < graphHitPlayerDataList.size(); i++ )
                {
                    datesList.add(graphHitPlayerDataList.get(i).getDate());
                    hitsList.add(graphHitPlayerDataList.get(i).getHits());
                }

                if(graphHitPlayerDataList.size() != 0)
                {
                    if(graphClicked){

                        graph_Linear_layout.setVisibility(View.VISIBLE);
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        GraphStats graphFragment = new GraphStats();
                        fragmentTransaction.add(R.id.graphLinearLayout,graphFragment).commit();

                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("dateData", datesList);
                        bundle.putStringArrayList("hitData", hitsList);
                        graphFragment.setArguments(bundle);

                        graphClicked = false;
                    }
                    else {
                        graph_Linear_layout.setVisibility(View.GONE);
                        graphClicked = true;
                    }
                }
            }
        });
    }
}
