package com.android.smartprix.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.smartprix.R;
import com.android.smartprix.application.ImageDownloaderTask;
import com.android.smartprix.application.ItemListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductFragment extends Fragment {


    public ProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fv = inflater.inflate(R.layout.fragment_product, container, false);
        try {
            JSONObject requestResult = new JSONObject(getArguments().getString("results"));

            TextView productName = (TextView)fv.findViewById(R.id.product_name);
            productName.setText(requestResult.getString("name"));

            ImageView productImage = (ImageView) fv.findViewById(R.id.product_image);
            new ImageDownloaderTask(productImage).execute(requestResult.getString("img_url"));

            JSONArray prices = requestResult.getJSONArray("prices");

            TextView bestPrice = (TextView)fv.findViewById(R.id.product_best_price);
            bestPrice.setText("Best Price : Rs "+prices.getJSONObject(0).getString("price"));

            TextView storeCount = (TextView)fv.findViewById(R.id.stores);
            storeCount.setText("Available at "+prices.length()+" stores");

            ListView storesList = (ListView) fv.findViewById(R.id.stores_list);
            storesList.setAdapter(new ItemListAdapter(getActivity(),prices,true));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fv;
    }

}
