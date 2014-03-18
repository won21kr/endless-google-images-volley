package models;

import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Queries")
public class Query extends Model {
	@Column(name = "Query")
	String query;

	public Query(){
		super();
	}
	
	public Query(String queryString){
		super();
		this.query = queryString;
	}
	
	 public List<Image> queries() {
	        return getMany(Image.class, "Query");
	 }
	 
	 public String getQuery(){
		 return query;
	 }
	 
	 public static List<Query> recentItems() {
	      return new Select().from(Query.class).orderBy("id DESC").limit("300").execute();
	 }
	 
	 public static void storeQuery(String queryString) {
			Query query = new Query(queryString);
			query.save();
	 }
	 
	 
}
