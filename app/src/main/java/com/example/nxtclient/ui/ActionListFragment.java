package com.example.nxtclient.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nxtclient.model.NxtAction;
import com.example.nxtclient.util.NxtActionAdapter;

/**
 * Created by shakhand on 2016/6/30.
 */

public  class ActionListFragment extends Fragment implements AdapterView.OnItemClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstance)
    {
        return inflater.inflate(R.layout.action_list_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance)
    {
        ListView listView = (ListView)view.findViewById(R.id.listView);

        // create resource
        mActions = new NxtAction[]{
                new NxtAction("Get Balance", new GetBalanceFragment()),
                new NxtAction("Send money", new SendMoneyFragment()),
                new NxtAction("Send Message", new SendMessageFragment())
        };

        NxtActionAdapter adapter = new NxtActionAdapter(getContext(), mActions);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView)view;
        Toast.makeText(view.getContext(),textView.getText(), Toast.LENGTH_SHORT).show();

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, mActions[position].mFragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    private NxtAction[] mActions;
}