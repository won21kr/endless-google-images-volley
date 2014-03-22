package com.app.uber.googleimage.views.activities;

import java.util.ArrayList;

import com.app.uber.googleimage.models.Image;
import com.app.uber.googleimage.models.Query;

import org.json.JSONObject;

import com.app.uber.googleimage.adapters.ImageAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.SearchView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.uber.googleimage.R;
import com.app.uber.googleimage.controllers.GoogleImageClient;

public class GoogleImageSearchVolleyActivity extends Activity {
	public ImageAdapter mAdapter;
	private GridView mGridView;
	private String mCurrentQuery;
	private GoogleImageClient mClient;
	private EndlessScrollListener mEndlessListener;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  
		setContentView(R.layout.image_search_volley);

		// grid view
		mGridView = (GridView) findViewById(R.id.results);
		mGridView.setNumColumns(3);

		// set reference to adapter
		mAdapter = new ImageAdapter(this);
		mGridView.setAdapter(mAdapter);
		
		mEndlessListener = new EndlessScrollListener();
		mGridView.setOnScrollListener(mEndlessListener);
		
		mClient = new GoogleImageClient();

		searchFromHistoryActivity();
	}

	private void search(String query) {
		//reset totals in scroll listener 
		mEndlessListener.resetTotals();
		
		// set current search query
		mCurrentQuery = query;
		
		//log query in history
		Query.storeQuery(mCurrentQuery);
		
		//set title
		setTitle("Searching - " + mCurrentQuery);

		// Clear out previous search results
		mAdapter.clear();

		// load images
		loadMore();
	}
	
	private void loadMore() {
		setProgressBarIndeterminateVisibility(true);
		
		//Use GoogleImageClient to make request
		mClient.makeSearchRequest(mCurrentQuery, mAdapter.getCount(),
											responseSuccessListener(), responseErrorListener());
	}
	
	
	private Response.Listener<JSONObject> responseSuccessListener() {	
		return new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				processResponse(response);
				setProgressBarIndeterminateVisibility(false);
			}
		};
	}
	

	private Response.ErrorListener responseErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				showErrorDialog(error);
			}
		};
	}

	private void showErrorDialog(Exception e) {
		e.printStackTrace();
	}
	
	protected void processResponse(JSONObject response) {	
		ArrayList<Image> imageList = Image.fromJSON(response);
		for (Image image : imageList){
			mAdapter.add(image.url);
		}
		mAdapter.notifyDataSetChanged();
	}
	
	private boolean searchFromHistoryActivity() {
		boolean ret = false;
		Bundle extras = null;
		if (getIntent().getExtras() != null) {
			ret = true;
			extras = getIntent().getExtras();
			
			String queryString = extras.getString("query");
			search(queryString);
		}
		return ret;
	}
	
	private void showHistory() {
		Intent i = new Intent(getApplicationContext(), QueryHistoryActivity.class);
		startActivity(i);
	}

	public ImageAdapter getCurrentAdapater(){
		ImageAdapter ret = null;
		if (mAdapter!= null){
			ret = mAdapter;
		}
		return ret;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_search, menu);

		final MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSubmitButtonEnabled(true);
		
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String queryString) {
				//perform search
				search(queryString);
				
				//collapse  search 
				searchItem.collapseActionView();

				return true;
			}

			@Override
			public boolean onQueryTextChange(String currentText) {
				return false;
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search_history:
			showHistory();
			break;
		default:
			break;
		}
		return true;
	}
	
	public class EndlessScrollListener implements OnScrollListener {
		private int visibleThreshold = 4;
		private int currentPage = 0;
		private int previousTotal = 0;
		private boolean loading = true;

		public EndlessScrollListener() {
			
		}
		
		public void resetTotals(){
			previousTotal = 0;
		}
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
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
			//if we are not loading and the (total - visible in view) <=  (current top item position + the threshold value)
			if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
				loadMore();
				loading = true;
			}
		}

		public int getCurrentPage() {
			return currentPage;
		}

	}
}