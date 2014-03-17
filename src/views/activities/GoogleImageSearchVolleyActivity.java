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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
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
	private boolean mInError = false;
	private Button historyButton;
	private GridView mGridView;
	private String mCurrentQuery;

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

		fromHistoryActivity();
	}

	private boolean fromHistoryActivity() {
		boolean ret = false;
		Bundle extras = null;
		if (getIntent().getExtras() != null) {
			ret = true;
			extras = getIntent().getExtras();
			String queryString = extras.getString("query");
			Log.i("INTENT QUERY", "query string: " + queryString);
			search(queryString);
			setTitle("Searching - " + queryString);
		}
		return ret;
	}

	private void search(String query) {
		// set current search query
		mCurrentQuery = query;

		// Clear out previous search results
		mAdapter.clear();

		// load images
		loadMore();
	}
	
	private void loadMore() {
		setProgressBarIndeterminateVisibility(true);
		Log.i("LOAD", "LOAD CALLED: Size: " + mAdapter.getCount());
		JsonObjectRequest myReq = new JsonObjectRequest(
				Method.GET,
				String.format(
						"https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=%s&start=%d&imgsz=medium",
						Uri.encode(mCurrentQuery), mAdapter.getCount()), null,
				responseSuccessListener(), responseErrorListener());
		Log.i("REQUEST", myReq.toString());

		// mQueue.add(myReq);
		NetworkController.getInstance().addToRequestQueue(myReq);

	}

	private void showHistory() {
		Intent i = new Intent(getApplicationContext(), QueryHistoryActivity.class);
		startActivity(i);
	}

	protected void storeQuery(String queryString) {
		Query query = new Query(queryString);
		query.save();
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
		mInError = true;
		e.printStackTrace();

	}

	
	protected void processResponse(JSONObject response) {
		try {
			JSONObject feed = response.getJSONObject("responseData");
			JSONArray entries = feed.getJSONArray("results");
			mGridView.setOnScrollListener(new EndlessScrollListener());

			for (int i = 0; i < entries.length(); i++) {
				JSONObject entry = entries.getJSONObject(i);
				String url = entry.getString("url");
				
				mAdapter.add(url);
			}
			mAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			showErrorDialog(e);
		}
	}
	

	public class EndlessScrollListener implements OnScrollListener {
		// how many entries earlier to start loading next page
		private int visibleThreshold = 4;
		private int currentPage = 0;
		private int previousTotal = 0;
		private boolean loading = true;

		public EndlessScrollListener() {
			
		}
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
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
			//if we are not loading and the (total - visible in view) <=  (current top item position + the threshold value)
			if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
				Log.i("LOADING MORE", "LOADING MORE");
				loadMore();
				loading = true;
			}
		}

		public int getCurrentPage() {
			return currentPage;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_search, menu);

		final MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSubmitButtonEnabled(true);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String queryString) {
				storeQuery(queryString);
				search(queryString);
				searchItem.collapseActionView();
				setTitle("Searching - " + queryString);
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

}