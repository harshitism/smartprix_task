package com.android.smartprix.application;

import org.json.JSONObject;

/**
 * Created by Harshit on 26-May-16.
 */
public interface AsyncCommunicator {
    public  void AsyncEvent(JSONObject value, String taskid);
}
