package com.example.nxtclient.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nxtclient.NxtApplication;

class NxtAction
{
    public NxtAction(String name, Fragment fragment)
    {
        mName = name;
        mFragment = fragment;
    }
    public String mName;
    public Fragment mFragment;
}

class NxtActionAdapter extends ArrayAdapter<NxtAction>{

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

public class NxtActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nxt);

        NxtApplication.url          = ((EditText)findViewById(R.id.urlEditText)).getText().toString();
        NxtApplication.myAccount    = ((EditText)findViewById(R.id.myAccountEditText)).getText().toString();
        NxtApplication.myPassword   = ((EditText)findViewById(R.id.myPasswordEditText)).getText().toString();

        // Present the fragment for action overview
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, new ActionListFragment(), "ACTION_LIST_FRAGMENT");
        ft.commit();

    }
}


