package com.android.smartprix.application;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.smartprix.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Harshit on 27-May-16.
 */
public class ItemListAdapter extends BaseAdapter {


    private Context context;
    private JSONArray results;
    private boolean buyShow;
    private LayoutInflater inflater=null;
    public ItemListAdapter(Context context,JSONArray results,boolean buyShow) {
        this.context = context;
        this.results = results;
        this.buyShow = buyShow;
        inflater = ( LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return results.length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            final JSONObject item = results.getJSONObject(position);
            View vi = convertView;
            if(convertView==null) {
                vi = inflater.inflate(R.layout.listview_stores_list, null);
            }

            if(buyShow)
            {
                ImageView productImage = (ImageView) vi.findViewById(R.id.store_logo);
                new ImageDownloaderTask(productImage).execute(item.getString("logo"));

                TextView name = (TextView) vi.findViewById(R.id.store_name);
                name.setText(item.getString("store_name"));
                TextView price = (TextView) vi.findViewById(R.id.store_price);
                price.setText("Rs "+item.getString("price"));
                Button buy = (Button) vi.findViewById(R.id.buy_button);

                TextView other = (TextView) vi.findViewById(R.id.others);
                other.setText("Rating:"+item.getString("store_rating")+" / "+item.getString("stock")+" / Shipping:Rs"+item.getString("shipping_cost"));

                buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context,"Redirecting to Paymemt Page",Toast.LENGTH_LONG).show();
                        Intent myIntent = null;
                        try {
                            myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getString("link")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        context.startActivity(myIntent);
                    }
                });
            }
            else
            {
                ImageView productImage = (ImageView) vi.findViewById(R.id.store_logo);
                new ImageDownloaderTask(productImage).execute(item.getString("img_url"));

                TextView name = (TextView) vi.findViewById(R.id.store_name);
                name.setText(item.getString("name"));
                TextView price = (TextView) vi.findViewById(R.id.store_price);
                price.setText("Rs "+item.getString("price"));
                Button buy = (Button) vi.findViewById(R.id.buy_button);

                buy.setVisibility(View.GONE);
                TextView other = (TextView) vi.findViewById(R.id.others);
                other.setVisibility(View.GONE);


            }



            return vi;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;

    }
}