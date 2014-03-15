package views.activities;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.uber.googleimage.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import controllers.NetworkController;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import models.GoogleImage;

/**
 * Created by koush on 6/4/13.
 */
public class GoogleImageSearchVolleyActivity extends Activity {
    private MyAdapter mAdapter;
	EditText searchText;
	private RequestQueue mQueue;
	private boolean mInError = false;


    // Adapter to populate and imageview from an url contained in the array adapter
    private class MyAdapter extends ArrayAdapter<String> {
        public MyAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // see if we need to load more to get 40, otherwise populate the adapter
            if (position > getCount() - 4)
                loadMore();

            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.google_image, null);

            // find the image view
            final ImageView iv = (ImageView) convertView.findViewById(R.id.image);

            Picasso.with(getApplicationContext())
            .load(getItem(position))
            .centerCrop()
            .resize(256,  256)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .into(iv);


            return convertView;
        }
    }

    
    private void loadMore() {        
        JsonObjectRequest myReq = new JsonObjectRequest(Method.GET,
        		String.format("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&" 
        				+ "q=%s&start=%d&imgsz=medium", Uri.encode(searchText.getText().toString()), mAdapter.getCount()),
                        null,
                        createMyReqSuccessListener(),
                        createMyReqErrorListener());
        Log.i("REQUEST", myReq.toString());

        mQueue.add(myReq);
        
    	
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_image_search);
        
        mQueue = Volley.newRequestQueue(this);

        final Button search = (Button) findViewById(R.id.search);
        searchText = (EditText) findViewById(R.id.search_text);
        
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        GridView view = (GridView) findViewById(R.id.results);
        view.setNumColumns(3);
        mAdapter = new MyAdapter(this);
        view.setAdapter(mAdapter);
        view.setOnScrollListener(new EndlessScrollListener());

        //search();
    }

    private void search() {
        mAdapter.clear();
        loadMore();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
    }
 
    
    private Response.Listener<JSONObject> createMyReqSuccessListener() {
    	
        return new Response.Listener<JSONObject>() {
        	
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject feed = response.getJSONObject("responseData");
                    JSONArray entries = feed.getJSONArray("results");
                    JSONObject entry;
                    for (int i = 0; i < entries.length(); i++) {
                        entry = entries.getJSONObject(i);
                        
                        String url = null;
                        url = entry.getString("url");
                        
                        //mEntries.add(new GoogleImage(url));
                        mAdapter.add(url);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    showErrorDialog(e);
                }
            }
        };
        
    }


    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorDialog(error);
            }
        };
    }
    
    private void showErrorDialog(Exception e) {
        mInError  = true;
        e.printStackTrace();
      
    }
    
    /**
     * Detects when user is close to the end of the current page and starts loading the next page
     * so the user will not have to wait (that much) for the next entries.
     * 
     * @author Ognyan Bankov (ognyan.bankov@bulpros.com)
     */
    public class EndlessScrollListener implements OnScrollListener {
        // how many entries earlier to start loading next page
        private int visibleThreshold = 9;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }
        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.
                loadMore();
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            
        }
        
        
        public int getCurrentPage() {
            return currentPage;
        }
      
    }
    
}