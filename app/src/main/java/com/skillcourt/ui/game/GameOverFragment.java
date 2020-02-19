package com.skillcourt.ui.game;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.skillcourt.R;
import com.skillcourt.services.ConnectionService;
import com.skillcourt.ui.main.HomeFragment;
import com.skillcourt.ui.main.MainActivity;
import com.skillcourt.ui.main.NonBottomNavigationFragments;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import at.grabner.circleprogress.CircleProgressView;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by Joshua Mclendon on 2/7/18.
 */
public class GameOverFragment extends NonBottomNavigationFragments {

    private static final String TAG = GameOverFragment.class.getSimpleName();
    private TextView mScore, mHits, mMode, mTime;
    private Button mPlayAgainButton, mNewGameButton, mHomeButton;
    private int testHit;
    private int testMiss;
    private String mGameMode;
    private long mGameTime;
    private int hit, miss, totalPoints;
    private CircleProgressView mCircleView;
    public ConnectionService mConnectionService;
    public boolean mBounded = false;


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
            mTime.setText(bundle.getString("GAME_TIME_STRING"));
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
        String dateOutput = dateFormat.format(calendar.getTime());

        //Add data to the database *******************************************************
        System.out.println("mGameTime " +mGameTime + " mTime " + mTime.getText());
        addData(dateOutput, ""+mTime.getText(), ""+totalPoints, ""+(testHit + "/" + (testHit + testMiss)));
        //Add data to the database *******************************************************

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

    public void addData(String date, String time, String score, String hit)
    {
        boolean isInserted = mainActivity.myDB.insertData(date,time,score,hit);

        if(isInserted == true)
        {
            Toast.makeText(getActivity(), "PlayerData Added!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getActivity(), "PlayerData NOT Added!", Toast.LENGTH_SHORT).show();
        }
    }
}