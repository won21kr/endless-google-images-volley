package models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Images")
public class Image extends Model{
	@Column(name = "Url")
	String url;
	
	@Column(name = "Query")
	public Query query;
	
	public Image(){
	  super();	
	}
	
	public Image(String u){
		super();
		url = u;
	}
	
	
	

}
