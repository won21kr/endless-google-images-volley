package adapters;

import java.util.ArrayList;

import com.app.uber.googleimage.R;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

 public class ImageAdapter extends ArrayAdapter<String> {
        private ArrayList<String> mData;

		public ImageAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	
            if (convertView == null){
            	  LayoutInflater inflater = LayoutInflater.from(getContext());
                  convertView = inflater.inflate(R.layout.google_image, null);
            }
            // find the image view
            final ImageView iv = (ImageView) convertView.findViewById(R.id.image);

            Picasso.with(getContext())
            .load(getItem(position))
            .centerCrop()
            .resize(256,  256)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .into(iv);


            return convertView;
        }
    }