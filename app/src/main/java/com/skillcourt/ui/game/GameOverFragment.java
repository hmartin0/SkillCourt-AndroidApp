package com.skillcourt.ui.game;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skillcourt.R;
import com.skillcourt.services.ConnectionService;
import com.skillcourt.structures.SessionData;
import com.skillcourt.structures.SessionListAdapter;
import com.skillcourt.ui.main.HomeFragment;
import com.skillcourt.ui.main.MainActivity;
import com.skillcourt.ui.main.NonBottomNavigationFragments;
import com.skillcourt.ui.main.StatsFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import at.grabner.circleprogress.CircleProgressView;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by Joshua Mclendon on 2/7/18.
 */
public class GameOverFragment extends NonBottomNavigationFragments {

    private static final String TAG = GameOverFragment.class.getSimpleName();
    private TextView mScore, mHits, mMode, mTime, promptTitleText;
    private Button mPlayAgainButton, mNewGameButton, mHomeButton, saveDataButton;
    private int testHit;
    private int testMiss;
    private String mGameMode;
    private long mGameTime;
    private String mGameTimeString;
    private int hit, miss, totalPoints;
    private ArrayList<SessionData> sessionList;
    SessionListAdapter adapter;
    private int positionID;
    private CircleProgressView mCircleView;
    public ConnectionService mConnectionService;
    public boolean mBounded = false;
    public String dateOutput;


    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Binding service disconnected");
            mBounded = false;
            mConnectionService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Binding service connected");
            mBounded = true;
            ConnectionService.LocalBinder mLocalBinder = (ConnectionService.LocalBinder) service;
            mConnectionService = mLocalBinder.getService();
            // Pads connected will be updated via broadcast connectionReceiver when a pad connects
            // This is just for initial launch of this activity

        }
    };

    public GameOverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBounded) {
            getActivity().unbindService(mConnection);
            mBounded = false;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(getActivity(), ConnectionService.class);

        if (!mBounded) {
            getActivity().startService(intent);
            getActivity().bindService(intent, mConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity.setTitle("Game Over");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_over, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity.getSupportActionBar().show();
        final Bundle bundle = getArguments();
        mGameMode = bundle.getString("GAME_MODE");
        mGameTime = bundle.getLong("GAME_TIME", 30);
        mGameTimeString = bundle.getString("GAME_TIME_STRING");
        testHit = bundle.getInt("HIT_COUNT", 0);
        testMiss = bundle.getInt("MISS_COUNT", 0);
        totalPoints = bundle.getInt("SCORE", 0);

        hit = (int) ((double) testHit / (testHit + testMiss) * 100);
        miss = (int) ((double) testMiss / (testHit + testMiss) * 100);
        Log.i("GameOverActivity", "Hit: " + hit + "%" + " Miss: " + miss + "%");

        mCircleView = view.findViewById(R.id.game_over_progress);
        mScore = view.findViewById(R.id.game_over_score);
        mHits = view.findViewById(R.id.game_over_hits);
        mMode = view.findViewById(R.id.game_over_mode);
        mTime = view.findViewById(R.id.game_over_time);
        mPlayAgainButton = view.findViewById(R.id.game_over_play_again_btn);
        mNewGameButton = view.findViewById(R.id.game_over_new_game_btn);
        mHomeButton = view.findViewById(R.id.game_over_home_btn);
        saveDataButton = view.findViewById(R.id.save_data_button);
        promptTitleText = view.findViewById(R.id.promptTextViewTitle);

        if (mGameMode == null) {
            mGameMode = "Random";
        }
        mMode.setText(mGameMode);
        mMode.setAllCaps(true);
        mHits.setText("Hits: " + testHit + " / " + (testHit + testMiss));
        if (totalPoints < 0) {
            totalPoints = 0;
        }
        mScore.setText("Score: " + totalPoints);

        if (mGameTime != 0) {
            setGameTimeText(mGameTime);
        } else {
            mTime.setText(mGameTimeString);
        }


        if (hit == 0) {
            setProgressBarColor(Color.parseColor("#B3B3B3"));
        } else if (hit > 0 && hit <= 20) {
            setProgressBarColor(Color.RED);
        } else if (hit > 20 && hit <= 40) {
            setProgressBarColor(Color.parseColor("#f48342"));
        } else if (hit > 40 && hit <= 60) {
            setProgressBarColor(Color.parseColor("#f4ce42"));
        } else if (hit > 60 && hit < 80) {
            setProgressBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        mCircleView.setValueAnimated(hit);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
        dateOutput = dateFormat.format(calendar.getTime());


        // Save data to database
        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSession();
            }
        });


        mPlayAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new StartGameFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                mainActivity.startBottomNavFragment(fragment, fragmentTransaction);
            }
        });

        mNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                if (mConnectionService.getPadsConnected() > 1) {
                    fragment = new GameModeFragment();
                } else {
                    fragment = new CreateGameFragment();
                }

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                mainActivity.startFragmentWithBackButton(fragment, fragmentTransaction, false);
            }
        });

        mHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                mainActivity.startBottomNavFragment(fragment, fragmentTransaction);
            }
        });
    }

    private void setProgressBarColor(int color) {
        mCircleView.setTextColor(color);
        mCircleView.setBarColor(color);
        mCircleView.setUnitColor(color);
    }

    private void setGameTimeText(long seconds) {
        if (seconds >= 60) {
            int minute = (int) seconds / 60;
            seconds = seconds % 60;
            if (seconds < 10) {
                mTime.setText(minute + ":0" + seconds);
            } else {
                mTime.setText(minute + ":" + seconds);
            }
        } else {
            if (seconds < 10) {
                mTime.setText("00:0" + seconds);
            } else {
                mTime.setText("00:" + seconds);
            }
        }
    }

    /*********************************************************************************************/

    public ArrayList<SessionData> getSessionData()
    {
        Cursor res = mainActivity.myDB.getAllSessionData();
        ArrayList<SessionData> tempList = new ArrayList<>();

        if(res.getCount() == 0)
        {
            Toast.makeText(getActivity(), "Create a New Session!", Toast.LENGTH_SHORT).show();
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


    private void saveSession()
    {
        View view =(LayoutInflater.from(getActivity()).inflate(R.layout.prompts, null));

        final ListView gamePromptListView = view.findViewById(R.id.prompListView);
        final EditText userInput = view.findViewById(R.id.editTextDialogUserInput);
        final EditText noteInput = view.findViewById(R.id.notesTextUserInput);
        sessionList = new ArrayList<>();
        sessionList = getSessionData();

        adapter = new SessionListAdapter(getActivity(), R.layout.adapter_session_view_layout, sessionList);
        gamePromptListView.setAdapter(adapter);

        gamePromptListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                SessionData content = sessionList.get(i);
                positionID = Integer.parseInt(content.getSessionID());
                userInput.setText(Integer.toString(positionID));
            }
        });


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(view);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                String value = userInput.getText().toString();
                                String note = noteInput.getText().toString();
                                int sessionID = Integer.parseInt(value);

                                Cursor resSessionID = mainActivity.myDB.getAllSessionID(value);
                                if(resSessionID.getCount() == 0)
                                {
                                    addSessionData(sessionID, dateOutput);
                                }
                                //Add data stats to the database *******************************************************
                                addPlayerData(dateOutput,
                                        ""+mTime.getText(),
                                        ""+totalPoints,
                                        ""+(testHit + "/" + (testHit + testMiss)),
                                        sessionID,
                                        note);

                                saveDataButton.setEnabled(false);
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


    public void addSessionData(Integer session_ID, String session_date)
    {
        boolean isInserted = mainActivity.myDB.insertSessionData(session_ID,session_date);

        if(isInserted == true)
        {
            Toast.makeText(getActivity(), "Session Added!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getActivity(), "Session NOT Added!", Toast.LENGTH_SHORT).show();
        }
    }


    public void addPlayerData(String date, String time, String score, String hit, Integer session_ID, String notes)
    {
        boolean isInserted = mainActivity.myDB.insertData(date,time,score,hit,session_ID, notes);

        if(isInserted == true)
        {
            Toast.makeText(getActivity(), "Player Data Added!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getActivity(), "Player Data NOT Added!", Toast.LENGTH_SHORT).show();
        }
    }
}