package com.skillcourt.ui.game;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.shawnlin.numberpicker.NumberPicker;
import com.skillcourt.R;
import com.skillcourt.ui.main.NonBottomNavigationFragments;


/**
 * Created by Joshua Mclendon on 2/5/18.
 * Edited by Brandon Suarez on 12/1/19
 */
public class CreateGameFragment extends NonBottomNavigationFragments {
    private static final String TAG = CreateGameFragment.class.getSimpleName();
    private int mGamePlayers = 1;
    private int mGameMinutes = 0;
    private int mGameSeconds = 0;
    private int check = 0;
    private boolean byHits = false;
    private String mGameType = "Solo";
    private double mPadLightUpTime = 0.0;
    private double mPadLightUpTimeDelay = 0.0;
    private int mByHitsAmount = 0;

    public CreateGameFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity.setTitle("Create Game");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_game, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button mPlayGame;
        Switch byHitsSwitch = view.findViewById(R.id.byHitsSwitch);
        final NumberPicker mMinutePicker = view.findViewById(R.id.minute_picker);
        ;
        final NumberPicker mSecondPicker = view.findViewById(R.id.second_picker);
        ;
        final EditText padLightUp = view.findViewById(R.id.padLightUpTime);
        final EditText padLightUpDelay = view.findViewById(R.id.padLightUpDelayTime);
        final EditText byHitsAmount = view.findViewById(R.id.byHitsAmount);
        final TextView byHitsLabel = view.findViewById(R.id.byHitsLabel);
        final TextView minTextView = view.findViewById(R.id.minuteTextView);
        final TextView secsTextView = view.findViewById(R.id.seconds);


        mPlayGame = view.findViewById(R.id.playGameBtn);

        byHitsAmount.setVisibility(View.INVISIBLE);
        byHitsLabel.setVisibility(View.INVISIBLE);

        mMinutePicker.setMinValue(0);
        mMinutePicker.setMaxValue(59);
        mSecondPicker.setMinValue(0);
        mSecondPicker.setMaxValue(59);

        mMinutePicker.setWrapSelectorWheel(true);
        mSecondPicker.setWrapSelectorWheel(true);

        byHitsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    byHits = true;
                    mMinutePicker.setVisibility(View.INVISIBLE);
                    mSecondPicker.setVisibility(View.INVISIBLE);
                    minTextView.setVisibility(View.INVISIBLE);
                    secsTextView.setVisibility(View.INVISIBLE);
                    byHitsAmount.setVisibility(View.VISIBLE);
                    byHitsLabel.setVisibility(View.VISIBLE);
                } else {
                    byHits = false;
                    mMinutePicker.setVisibility(View.VISIBLE);
                    mSecondPicker.setVisibility(View.VISIBLE);
                    minTextView.setVisibility(View.VISIBLE);
                    secsTextView.setVisibility(View.VISIBLE);
                    byHitsAmount.setVisibility(View.INVISIBLE);
                    byHitsLabel.setVisibility(View.INVISIBLE);
                }
            }
        });

        mMinutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                mGameMinutes = newVal;
            }
        });

        mSecondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                mGameSeconds = newVal;
            }
        });


        mPlayGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Minutes: " + mGameMinutes);
                Log.i(TAG, "Seconds: " + mGameSeconds);


                String padLightUpTime =  padLightUp.getText().toString();
                String padLightUpTimeDelay =  padLightUpDelay.getText().toString();
                String byHitsAm = byHitsAmount.getText().toString();
                if(!padLightUpTime.isEmpty()){
                    try {
                        mPadLightUpTime = Double.parseDouble(padLightUpTime);
                    }catch(final NumberFormatException e){
                        Toast.makeText(getActivity(), "Your pad light up time is not valid", Toast.LENGTH_SHORT).show();
                    }
                }

                if(!padLightUpTimeDelay.isEmpty()){
                    try {
                        mPadLightUpTimeDelay = Double.parseDouble(padLightUpTimeDelay);
                    }catch(final NumberFormatException e){
                        Toast.makeText(getActivity(), "Your pad light up delay time is not valid", Toast.LENGTH_SHORT).show();
                    }
                }

                if (!byHitsAm.isEmpty()) {
                    try {
                        mByHitsAmount = Integer.parseInt(byHitsAm);
                    } catch (final NumberFormatException e) {
                        Toast.makeText(getActivity(), "Your hits amount is not valid", Toast.LENGTH_SHORT).show();
                    }
                }

                check = mainActivity.mConnectionService.getPadsConnected() % mGamePlayers;

                if (mGameMinutes == 0 && mGameSeconds == 0 && !byHits) {
                    Toast.makeText(getActivity(), "Must select an appropriate amount of time for playing", Toast.LENGTH_SHORT).show();
                } else if (check != 0) {
                    Toast.makeText(getActivity(), "Must add " + check + " more pads to play with " + mGamePlayers + " players", Toast.LENGTH_SHORT).show();
                } else if (byHits && mByHitsAmount == 0) {
                    Toast.makeText(getActivity(), "Must select an appropriate amount of hits for playing", Toast.LENGTH_SHORT).show();
                }else if (byHits && mPadLightUpTime == 0) {
                    Toast.makeText(getActivity(), "Pad Light Up Time must be greater than 0", Toast.LENGTH_SHORT).show(); //Added to avoid an infinite loop, if a Pad happens to not transmit a hit detection//
                }/*else if (mPadLightUpTimeDelay == 0) {
                    Toast.makeText(getActivity(), "Pad Light Up Delay must be greater than 0, (Recommended value of 0.5)", Toast.LENGTH_SHORT).show();   //added if minimum delay is desired to be less than 0.5//
                } */else {
                    long gameTimeFinal = (60 * mGameMinutes) + mGameSeconds;
                    Log.i(TAG, "Game time in seconds: " + gameTimeFinal);

                    Bundle bundle = getArguments();
                    if(mGamePlayers == 2 ){
                        mGameType = "Duo";
                    }
                    bundle.putString("GAME_TYPE", mGameType);
                    bundle.putLong("GAME_TIME", gameTimeFinal);
                    bundle.putDouble("PAD_LIGHT_UP_TIME", mPadLightUpTime);
                    bundle.putDouble("PAD_LIGHT_UP_TIME_DELAY", mPadLightUpTimeDelay + 0.5);  ////Adds the necessary 0.5 second delay/////
                    bundle.putInt("PLAYER_COUNT", mGamePlayers);
                    bundle.putBoolean("BY_HITS", byHits);
                    bundle.putInt("BY_HITS_AMOUNT", mByHitsAmount);
                    Fragment fragment = new StartGameFragment();
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    mainActivity.startBottomNavFragment(fragment, fragmentTransaction);
                }
            }
        });
    }
}