package views.activities;

import java.util.List;

import models.Query;

import com.app.uber.googleimage.R;

import adapters.QueryAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class QueryHistoryActivity extends Activity {
	private ListView mLVQueries;
	private QueryAdapter mQueryAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_history_container);
		
		mLVQueries = (ListView) findViewById(R.id.lv_query_list_view);
		mQueryAdapter = new QueryAdapter(getApplicationContext());
		mLVQueries.setAdapter(mQueryAdapter);
		
		fetchQueryList();
		setupQueryItemListenter();
	}


	private void setupQueryItemListenter() {
		mLVQueries.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View itemView, int position,
					long id) {
				Intent i = new Intent(getApplicationContext(), GoogleImageSearchVolleyActivity.class);
				Bundle b = new Bundle();
				b.putString("query", Query.recentItems().get(position).getQuery());
				i.putExtras(b);
				startActivity(i);
				
			}
		});
		
	}


	private void fetchQueryList() {
		List<Query> queryList = Query.recentItems();
		for (Query q : queryList){
			mQueryAdapter.add(q);
		}
		
	}

}
