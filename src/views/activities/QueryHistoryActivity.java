package views.activities;

import java.util.List;
import models.Query;
import com.app.uber.googleimage.R;
import adapters.QueryAdapter;
import android.app.Activity;
import android.os.Bundle;
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
		//setupQueryItemListenter();
	}


	private void fetchQueryList() {
		List<Query> queryList = Query.recentItems();
		for (Query q : queryList){
			mQueryAdapter.add(q);
		}
		
	}

}
