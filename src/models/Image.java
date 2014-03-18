package models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Images")
public class Image extends Model{
	@Column(name = "Url")
	public String url;
	
	@Column(name = "Query")
	public Query query;
	
	public Image(){
	  super();	
	}
	
	public Image(String u){
		super();
		url = u;
	}
	
	public static ArrayList<Image> fromJSON(JSONObject response){
		ArrayList<Image> retList = new ArrayList<Image>();
		try {
			JSONObject feed = response.getJSONObject("responseData");
			JSONArray entries = feed.getJSONArray("results");
			
			for (int i = 0; i < entries.length(); i++) {
				JSONObject entry = entries.getJSONObject(i);
				String url = entry.getString("url");
				Image image = new Image(url);
				retList.add(image);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retList;
	}
	
	
	

}
