package com.android.smartprix.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.smartprix.R;
import com.android.smartprix.application.AsyncCommunicator;
import com.android.smartprix.application.NetworkAsyncTask;
import com.android.smartprix.application.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        NetworkAsyncTask loadCategories = new NetworkAsyncTask(SplashActivity.this, null, "Loading Data", URL.getCategories, new AsyncCommunicator() {
            @Override
            public void AsyncEvent(JSONObject value, String taskid) {

                try {
                    if(value.getString("request_status").equals("SUCCESS"))
                    {
                        Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                        intent.putExtra("request_result",value.getString("request_result"));
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(SplashActivity.this,"Some error",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        loadCategories.execute();


    }
}
