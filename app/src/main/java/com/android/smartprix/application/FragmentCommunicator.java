package com.android.smartprix.application;

import org.json.JSONObject;

/**
 * Created by Harshit on 26-May-16.
 */
public interface FragmentCommunicator {
    public  void FragmentCallback(JSONObject value,String taskid);
}
