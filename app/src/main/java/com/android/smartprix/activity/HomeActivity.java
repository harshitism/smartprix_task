package com.android.smartprix.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.smartprix.R;
import com.android.smartprix.application.AsyncCommunicator;
import com.android.smartprix.application.ItemListAdapter;
import com.android.smartprix.application.NetworkAsyncTask;
import com.android.smartprix.application.URL;
import com.android.smartprix.fragment.HomeFragment;
import com.android.smartprix.fragment.ProductFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle ;
    private JSONArray categoryList;
    private String category="";
    private String query="";
    private int start = 0;
    private List<String> listItems;
    private int STATE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<small>SmartPrix</small>"));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);

        }
        listItems = new ArrayList<>();
        listItems.add("HOME");
        try {
             categoryList = new JSONArray(getIntent().getStringExtra("request_result")) ;
                for(int i =0;i<categoryList.length();i++)
                {
                    listItems.add(categoryList.getString(i).toUpperCase());
                }

        } catch (JSONException e) {
            e.printStackTrace();
        }





        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        start=0;
        loadItem(category,"",start);

        final EditText searchQuery = (EditText)findViewById(R.id.search_query);
        Button searchButton = (Button)findViewById(R.id.search_button) ;
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(searchQuery.getText().length()>0)
                {

                    query = searchQuery.getText().toString();
                    query = query.replace(" ","");
                    start=0;
                    loadItem(category,query,start);
                }
            }
        });


        Button previous = (Button)findViewById(R.id.previous) ;
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start>=10)
                {
                    start-=10;
                    loadItem(category,query,start);
                }
                else
                {
                    Toast.makeText(HomeActivity.this,"No Previous Available",Toast.LENGTH_SHORT).show();
                }

            }
        });
        Button next = (Button)findViewById(R.id.next) ;
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    start+=10;
                    loadItem(category,query,start);
            }
        });




    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    /** Swaps fragments in the main content view */
    private void selectItem(int position) {

        if(position==0)
        {
            category="";
        }
        else
        {
            category = listItems.get(position);
        }
        query="";
        start=0;
        loadItem(category,query,start);
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        int id = item.getItemId();
        if (id == android.R.id.home) {

            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }


        }

        return super.onOptionsItemSelected(item);
    }



    public void loadItem (String category,String query,int start)
    {
        STATE=0;

        Fragment homeFragment = new HomeFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, homeFragment,"Home");
        fragmentTransaction.commit();

        TextView categoryView = (TextView) findViewById(R.id.category);
        categoryView.setText(category);

        NetworkAsyncTask getItems = null;
        try {
            getItems = new NetworkAsyncTask(this,null, "Loading Items", URL.searchQuery+"&category="+URLEncoder.encode(category,"UTF-8")+"&q="+ URLEncoder.encode(query,"UTF-8")+"&start="+start, new AsyncCommunicator() {
                @Override
                public void AsyncEvent(JSONObject value, String taskid) {
                    try {
                        if(value.getString("request_status").equals("SUCCESS"))
                        {

                            final JSONObject request= value.getJSONObject("request_result");

                            final JSONArray results = request.getJSONArray("results");
                            if(results.length()>0)
                            {
                                ListView itemList = (ListView) fragmentManager.findFragmentByTag("Home").getView().findViewById(R.id.item_list);
                                itemList.setAdapter(new ItemListAdapter(HomeActivity.this,results,false));
                                itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                                        try {
                                            JSONObject clickedItem = results.getJSONObject(position);
                                            NetworkAsyncTask getProductDetails = new NetworkAsyncTask(HomeActivity.this, null, "Loading Product", URL.productDetails + clickedItem.getString("id"), new AsyncCommunicator() {
                                                @Override
                                                public void AsyncEvent(JSONObject value, String taskid) {
                                                    try {
                                                        if(value.getString("request_status").equals("SUCCESS"))
                                                        {
                                                            JSONObject requestResult = value.getJSONObject("request_result");
                                                            STATE=1;

                                                            Fragment productFragment = new ProductFragment();
                                                            Bundle bundle = new Bundle();
                                                            bundle.putString("results",requestResult.toString());
                                                            productFragment.setArguments(bundle);
                                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                            fragmentTransaction.replace(R.id.main_fragment, productFragment,"Product");
                                                            fragmentTransaction.commit();







                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            getProductDetails.execute();


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(HomeActivity.this,"No Data Found",Toast.LENGTH_LONG).show();
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getItems.execute();

    }

    @Override
    public void onBackPressed() {
        if(STATE==1)
        {
            category="";
            query="";
            start=0;
            loadItem(category,query,start);
            STATE=0;
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
            alertDialogBuilder.setTitle("Exit Application");
            alertDialogBuilder.setMessage("Are You Sure to Exit ? ");
            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.create().show();
        }
    }
}
