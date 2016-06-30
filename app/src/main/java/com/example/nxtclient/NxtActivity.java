package com.example.nxtclient;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nxtclient.ui.ActionListFragment;
import com.example.nxtclient.ui.R;

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


