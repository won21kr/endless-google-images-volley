package views.activities;

import adapters.ImageAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;

import com.activeandroid.ActiveAndroid;
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

import models.Image;
import models.Query;

/**
 * Created by koush on 6/4/13.
 */
public class GoogleImageSearchVolleyActivity extends Activity {
    public ImageAdapter mAdapter;
	EditText searchText;
	private boolean mInError = false;
	private Button search;
	private Button historyButton;
	private GridView mGridView;
    
    private void loadMore() {   
    	Log.i("LOAD", "LOAD CALLED: Size: " + mAdapter.getCount());
        JsonObjectRequest myReq = new JsonObjectRequest(Method.GET,
        		String.format("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=%s&start=%d&imgsz=medium", Uri.encode(searchText.getText().toString()), mAdapter.getCount()),
                        null,
                        createMyReqSuccessListener(),
                        createMyReqErrorListener());
        Log.i("REQUEST", myReq.toString());

       //mQueue.add(myReq);
        NetworkController.getInstance().addToRequestQueue(myReq);
    	
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_image_search);
        
        //mQueue = Volley.newRequestQueue(this);
        search = (Button) findViewById(R.id.search);
        historyButton = (Button) findViewById(R.id.history);
        searchText = (EditText) findViewById(R.id.search_text);
        
        search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
                //Store query in the model
                Query query = new Query(searchText.getText().toString());
                query.save();
                
            }
        });
        
        historyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), QueryHistoryActivity.class);
				startActivity(i);
			}
		});

       //grid view
        mGridView = (GridView) findViewById(R.id.results);
        mGridView.setNumColumns(3);
        
        //set reference to adapter
        mAdapter = new ImageAdapter(this);
        mGridView.setAdapter(mAdapter);
        
        Bundle extras = null;
        if (getIntent().getExtras() != null){
        	extras = getIntent().getExtras();
        	String queryString = extras.getString("query");
        	Log.i("INTENT QUERY", "query string: " + queryString);
        	searchText.setText(queryString);
        	search();
        }
    
    }

    private void search() {
    	
    	//Clear out previous search results
        mAdapter.clear();
        
        //load images
        loadMore();

       //searchText.setText("");
       // InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
       //imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
    }
 
    
    private Response.Listener<JSONObject> createMyReqSuccessListener() {
    	
        return new Response.Listener<JSONObject>() {
        	
            @Override
            public void onResponse(JSONObject response) {
            	
                try {
                	
                    JSONObject feed = response.getJSONObject("responseData");
                    JSONObject cursor = feed.getJSONObject("cursor");
                    JSONArray entries = feed.getJSONArray("results");
                    int estimatedTotal = cursor.getInt("estimatedResultCount");
                    mGridView.setOnScrollListener(new EndlessScrollListener(estimatedTotal));
                    
                    JSONObject entry;
                    for (int i = 0; i < entries.length(); i++) {
                        entry = entries.getJSONObject(i);
                        String url = null;
                        url = entry.getString("url");
                       /* Image image = new Image(url);
                        image.query = query;    
                        image.save();*/
                       
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
    
    
    public class EndlessScrollListener implements OnScrollListener {
        // how many entries earlier to start loading next page
        private int visibleThreshold = 4;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;
        private int totalItems = 0;
        public EndlessScrollListener() {
        }
        
        public EndlessScrollListener(int totalItems) {
            this.totalItems = totalItems;
        }
        
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                	Log.i("LOADING", "NOT LOADING");
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            	Log.i("LOADING MORE", "LOADING MORE");
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_search, menu);
        return true;
    }
    
}