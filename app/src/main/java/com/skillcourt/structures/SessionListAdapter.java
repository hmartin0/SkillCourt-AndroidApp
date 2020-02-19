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

public class SessionListAdapter extends ArrayAdapter<SessionData> {
    private Context mContext;
    private int mResource;
    private ArrayList<SessionData> myList;

    static class ViewHolder{

        TextView sHIdTextView;
        TextView sHDateTextView;
    }

    public SessionListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<SessionData> objects) {
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
        String sID = getItem(position).getSessionID();
        String sDate = getItem(position).getSessionDate();

        SessionData sessionData = new SessionData(sID,sDate);
        ViewHolder holder;

        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);

            holder = new ViewHolder();
            holder.sHIdTextView = convertView.findViewById(R.id.idSessionTextView);
            holder.sHDateTextView = convertView.findViewById(R.id.dateSessionTextView);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.sHIdTextView.setText(sessionData.getSessionID());
        holder.sHDateTextView.setText(sessionData.getSessionDate());

        return convertView;
    }
}
