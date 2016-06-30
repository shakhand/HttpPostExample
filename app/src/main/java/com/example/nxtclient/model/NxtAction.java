package com.example.nxtclient.model;

import android.support.v4.app.Fragment;

/**
 * Created by shakhand on 2016/6/30.
 */
public class NxtAction
{
    public NxtAction(String name, Fragment fragment)
    {
        mName = name;
        mFragment = fragment;
    }
    public String mName;
    public Fragment mFragment;
}
