package com.skillcourt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.skillcourt.R;

/**
 * Created by Joshua Mclendon on 2/26/18.
 */
public class PlayerViewAdapter extends BaseAdapter{
    private static final String TAG = PlayerViewAdapter.class.getSimpleName();
    private final Context mContext;
    private int mPlayerCount;

    public PlayerViewAdapter(Context context, int playerCount){
        mContext = context;
        mPlayerCount = 0;
    }


    @Override
    public int getCount() {
        return mPlayerCount;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.layout_player_box, null);
        }
        return view;
    }
}
