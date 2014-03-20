package adapters;

import models.Query;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.uber.googleimage.R;

 public class QueryAdapter extends ArrayAdapter<Query> {
        public QueryAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	
            if (convertView == null){
            	  LayoutInflater inflater = LayoutInflater.from(getContext());
                  convertView = inflater.inflate(R.layout.query_item, null);
            }
            // find the image view
            TextView tv = (TextView) convertView.findViewById(R.id.tv_query_text);
            String queryString = getItem(position).getQuery();
            
            tv.setText(queryString);
            
            return convertView;
        }
    }