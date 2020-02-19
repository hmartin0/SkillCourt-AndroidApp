package com.skillcourt.structures;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skillcourt.R;

import java.util.ArrayList;

public class StatsListAdapter extends ArrayAdapter<PlayerData> {

    private Context mContext;
    private int mResource;
    private ArrayList<PlayerData> myList;

    static class ViewHolder{

        TextView hId;
        TextView hDate;
        TextView hGTime;
        TextView hScore;
        TextView hHit;
    }


    public StatsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<PlayerData> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        myList = objects;
    }


    public void myRemove(int i)
    {
        myList.remove(i);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String id = getItem(position).getId();
        String date = getItem(position).getDate();
        String time = getItem(position).getGTime();
        String score = getItem(position).getScore();
        String hit = getItem(position).getHits();

        PlayerData playerData = new PlayerData(id,date,time,score,hit);

        ViewHolder holder;

        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            holder = new ViewHolder();
            holder.hId = convertView.findViewById(R.id.idTextView);
            holder.hDate = convertView.findViewById(R.id.dateTextView);
            holder.hGTime = convertView.findViewById(R.id.gTimeTextView);
            holder.hScore = convertView.findViewById(R.id.scoreTextView);
            holder.hHit = convertView.findViewById(R.id.hitTextView);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.hId.setText(playerData.getId());
        holder.hDate.setText(playerData.getDate());
        holder.hGTime.setText(playerData.getGTime());
        holder.hScore.setText(playerData.getScore());
        holder.hHit.setText(playerData.getHits());

        return convertView;

    }
}
