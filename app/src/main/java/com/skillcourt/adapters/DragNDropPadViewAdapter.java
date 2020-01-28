package com.skillcourt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skillcourt.R;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.List;

/**
 * Created by Joshua Mclendon on 11/7/17.
 */
public class DragNDropPadViewAdapter extends BaseDynamicGridAdapter {
    private static final String TAG = DragNDropPadViewAdapter.class.getSimpleName();
    private final Context mContext;

    public DragNDropPadViewAdapter(Context context, List<?> items, int columnCount) {
        super(context, items, columnCount);
        mContext = context;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.layout_pad, null);
        }

        return view;
    }


}
