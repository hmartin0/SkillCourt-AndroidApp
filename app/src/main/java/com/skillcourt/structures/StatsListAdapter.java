package com.skillcourt.structures;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        TextView hSessionPID;
        TextView hNotes;
        TextView hGameType;
        ImageView hGameTypeImg;
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

    public void updateData()
    {
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
        String sessionPID = getItem(position).getSessioPlayerID();
        String notes = getItem(position).getNotes();
        String gameType = getItem(position).getGameType();

        PlayerData playerData = new PlayerData(id,date,time,score,hit,sessionPID,notes,gameType);

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
            holder.hSessionPID = convertView.findViewById(R.id.sessionPlayerIDTextView);
            holder.hNotes = convertView.findViewById(R.id.noteTextView);
            holder.hGameType = convertView.findViewById(R.id.gTypeTextView);
            holder.hGameTypeImg = convertView.findViewById(R.id.gTypeImageView);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        /*******REMOVE VISIBILITY FROM ADAPTER_VIEW_LAYOUT FOR DEBUGGING PURPOSE*****/
        holder.hId.setText(playerData.getId());  // is invisible in UI
        holder.hDate.setText(playerData.getDate());
        holder.hGTime.setText(playerData.getGTime());
        holder.hScore.setText(playerData.getScore()); // is invisible in UI, start game, and end game file
        holder.hHit.setText(playerData.getHits());
        holder.hSessionPID.setText(playerData.getSessioPlayerID()); // is invisible in UI
        holder.hNotes.setText(playerData.getNotes());
        holder.hGameType.setText(playerData.getGameType()); // is invisible in UI but replaced with bottom ImageView
        if(playerData.getGameType().equals("T"))
        {
            holder.hGameTypeImg.setImageResource(R.drawable.ic_av_timer_purple_24dp);
        }
        else
        {
            holder.hGameTypeImg.setImageResource(R.drawable.ic_kicking_a_footbal_ball);
        }

        return convertView;

    }
}
