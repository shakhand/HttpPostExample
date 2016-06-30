package com.example.nxtclient.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nxtclient.model.NxtAction;
import com.example.nxtclient.ui.R;

/**
 * Created by shakhand on 2016/6/30.
 */
public class NxtActionAdapter extends ArrayAdapter<NxtAction> {

    public NxtActionAdapter(Context context, NxtAction[] objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        NxtAction action = getItem(position);

        if( convertView == null )
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.action_list_item, parent, false);
        }
        TextView actionName = (TextView)convertView.findViewById(R.id.actionName);
        actionName.setText(action.mName);

        return convertView;
    }
}
