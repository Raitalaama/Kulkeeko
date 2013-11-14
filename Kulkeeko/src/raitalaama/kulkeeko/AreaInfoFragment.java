package raitalaama.kulkeeko;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public class AreaInfoFragment extends Fragment{
	private static final String TAG = AreaInfoFragment.class.getSimpleName();

	private static String REITTIOPAS_URL; 
	static interface AreaInfoCallbacks {
		public void aInfoOnPostExecute(AreaInfo areaInfo);
		public void aInfoOnPreExecute();
		public void aInfoOnCanceled();
	}
	
	  private AreaInfoCallbacks mCallbacks;
	  private parseInfo mTask;
	  private boolean mRunning;

	
	 /**
	   * Android passes us a reference to the newly created Activity by calling this
	   * method after each configuration change.
	   */
	  @Override
	  public void onAttach(Activity activity) {
	    Log.i(TAG, "onAttach(Activity)");
	    super.onAttach(activity);
	    if (!(activity instanceof AreaInfoCallbacks)) {
	      throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
	    }
	    
	    
	    REITTIOPAS_URL = "http://api.reittiopas.fi/hsl/prod/?user="+getString(R.string.user_name)+"&pass="+getString(R.string.password);
		

	    // Hold a reference to the parent Activity so we can report back the task's
	    // current progress and results.
	    mCallbacks = (AreaInfoCallbacks) activity;
	  }

	  /**
	   * This method is called only once when the Fragment is first created.
	   */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    Log.i(TAG, "onCreate(Bundle)");
	    super.onCreate(savedInstanceState);
	    setRetainInstance(true);
	  }

	  /**
	   * This method is <em>not</em> called when the Fragment is being retained
	   * across Activity instances.
	   */
	  @Override
	  public void onDestroy() {
	    Log.i(TAG, "onDestroy()");
	    super.onDestroy();
	    cancel();
	  }

	  /*****************************/
	  /***** TASK FRAGMENT API *****/
	  /*****************************/

	  /**
	   * Start the background task.
	   */
	  public void start(int searchRadius, String placename) {
	    if (!mRunning) {
	      mTask = new parseInfo(searchRadius, placename);
	      mTask.execute(REITTIOPAS_URL);
	      mRunning = true;
	    }
	  }
	  
	  public void start(int searchRadius, Location location){
		   if (!mRunning) {
			      mTask = new parseInfo(searchRadius, location);
			      mTask.execute(REITTIOPAS_URL);
			      mRunning = true;
			    }
	  }
	  /**
	   * Cancel the background task.
	   */
	  public void cancel() {
	    if (mRunning) {
	      mTask.cancel(false);
	      mTask = null;
	      mRunning = false;
	    }
	  }
	  public boolean isRunning() {
		    return mRunning;
		  }
	  private class parseInfo extends AsyncTask<String,Void,AreaInfo>{
		  
		private final boolean useLocation;
		private final int searchRadius;
		private  String placename;
		
		parseInfo(int searchRadius, String placename){
			this.useLocation = false;
			this.searchRadius = searchRadius;
			if(placename != null){
				this.placename = placename;
			}
			else{
				this.placename = "";
			}
		}
		
		parseInfo(int searchRadius, Location location){
			this.useLocation = true;
			this.searchRadius = searchRadius;
		}
		@Override
		protected void onPreExecute() {
			mCallbacks.aInfoOnPreExecute();
			mRunning = true;
		}
		
		private JSONArray getJsonFromUrl(String url){
			
			InputStream mJsonStream = null;
			String result = null;
			try{
				mJsonStream=downloadUrl(url);
				  // json is UTF-8 by default
			    BufferedReader reader = new BufferedReader(new InputStreamReader(mJsonStream, "UTF-8"), 8);
			    StringBuilder sb = new StringBuilder();

			    String line = null;
			    while ((line = reader.readLine()) != null)
			    {
			        sb.append(line + "\n");
			    }
			    result = sb.toString();
			    } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
			        if (mJsonStream != null) {
			        	try {
			        		mJsonStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        } 
			     }
				
			
			JSONArray json = null;
			try {
				json = new JSONArray(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;
		}

		@Override
		protected AreaInfo doInBackground(String... url) {
			
			String coords=""; 
			
			if(useLocation){
				//TODO coordinate transformations etc....
			}
			else{
				
				try {
				//Get coordinates corresponding to placename
				JSONArray places = getJsonFromUrl(REITTIOPAS_URL+"&request=geocode&key="+placename);
				if(places.length()>0){
					JSONObject first_place = places.getJSONObject(0);
					coords = first_place.getString("coords");
				}
				else{
					//TODO mit's kun ei loydy mitaan
				}
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
			}
			
			
			JSONArray json = getJsonFromUrl(REITTIOPAS_URL+"&request=reverse_geocode&result_contains=stop&limit=0&radius="+searchRadius+"&coordinate="+coords);
			//JSONArray json = getJsonFromUrl("http://api.reittiopas.fi/hsl/prod/?user=rkKulkeeko&pass=RiKa13kulkeeko&request=geocode&key=kampp&loc_types=stop");
			
			AreaInfo areaInfo = new AreaInfo(json);		
			return areaInfo;
		}
		

		@Override
		protected void onCancelled() {
			mCallbacks.aInfoOnCanceled();
			mRunning = false;
		}
		
		@Override
		protected void onPostExecute(AreaInfo areaInfo) {
			mCallbacks.aInfoOnPostExecute(areaInfo);
			mRunning = false;
		}

		
		
		// Given a string representation of a URL, sets up a connection and gets
		// an input stream.
		private InputStream downloadUrl(String urlString) throws IOException {
		    URL mUrl = new URL(urlString);
		    HttpURLConnection mConnection = (HttpURLConnection) mUrl.openConnection();
		    mConnection.setReadTimeout(10000 /* milliseconds */);
		    mConnection.setConnectTimeout(15000 /* milliseconds */);
		    mConnection.setRequestMethod("GET");
		    mConnection.setDoInput(true);
		    // Starts the query
		    mConnection.connect();
		    Log.d("Yhteyden tsekkailua", mConnection.getURL().toString());
		    Log.d("Yhteyden tsekkailua", mConnection.getResponseMessage().toString());

		    return mConnection.getInputStream();
		}
		  
	  }

}