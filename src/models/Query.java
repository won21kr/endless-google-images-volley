package models;

import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
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
	 
	 public static void storeQuery(String queryString) {
			Query query = new Query(queryString);
			query.save();
	 }
	 
	 public static List<Query> getQueryList(int limit) {
	      return new Select().from(Query.class).orderBy("id DESC").limit("" + limit).execute();
	 }
	 
	 public static List<Query> getAllQueries(){
		 return new Select().from(Query.class).orderBy("id DESC").execute();
	 }
	 
	 public static List<Query> recentItems() {
	      return new Select().from(Query.class).orderBy("id DESC").limit("300").execute();
	 }
	 
	 public static Query getLastStoredItem(){
		 return new Select().from(Query.class).orderBy("id DESC").executeSingle();
	 }
	 
	
	 
	 public static void populateQueryList(int queryCount){
		 for (int i = 0; i < queryCount; i++){
			 Query.storeQuery(i + "query");
		 }
	 }
	 
	 public static void deleteAll(){
		 new Delete().from(Query.class).execute();
	 }
	 
}
