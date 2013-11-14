/**
 * 
 */
package raitalaama.kulkeeko;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;


/**
 * @author Raitalaama
 *
 */
public class DisruptionInfoFragment extends Fragment {
	private static final String TAG = DisruptionInfoFragment.class.getSimpleName();
	//TODO Add multiple language support
	public static String DISRUPTIONINFO_URL;
	
	static interface DisruptionCallbacks {
		public void dInfoOnPostExecute(List<DisruptionInfo> disruptions);
		public void dInfoOnPreExecute();
		public void dInfoOnCanceled();
	}
	
	  private DisruptionCallbacks mCallbacks;
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
	    if (!(activity instanceof DisruptionCallbacks)) {
	      throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
	    }
	    if(activity.getResources().getConfiguration().locale.getLanguage().equalsIgnoreCase("fi"))
	    {
	    	DISRUPTIONINFO_URL  = "http://www.poikkeusinfo.fi/xml/v2/fi";
	    }
	    else
	    {
	    	DISRUPTIONINFO_URL  = "http://www.poikkeusinfo.fi/xml/v2/en";

	    }
	    // Hold a reference to the parent Activity so we can report back the task's
	    // current progress and results.
	    mCallbacks = (DisruptionCallbacks) activity;
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
	  public void start() {
	    if (!mRunning) {
	      mTask = new parseInfo();
	      mTask.execute(DISRUPTIONINFO_URL);
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
	  private class parseInfo extends AsyncTask<String,Void,List<DisruptionInfo>>{
		  
		@Override
		protected void onPreExecute() {
			mCallbacks.dInfoOnPreExecute();
			mRunning = true;
		}

		@Override
		protected List<DisruptionInfo> doInBackground(String... url) {
			InputStream mXml = null;
			List<DisruptionInfo> disruptions = null;
			DisruptionParser parser = new DisruptionParser();
			try{
			mXml=downloadUrl(url[0]);
			
		
			
			disruptions = parser.parse(mXml);
			// Makes sure that the InputStream is closed after the app is
		    // finished using it.
			
		    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				Log.d("xmlparseri ", e.getMessage());
				e.printStackTrace();
			} finally {
		        if (mXml != null) {
		        	try {
						mXml.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        } 
		     }
			
			return disruptions;
		}
		

		@Override
		protected void onCancelled() {
			mCallbacks.dInfoOnCanceled();
			mRunning = false;
		}
		
		@Override
		protected void onPostExecute(List<DisruptionInfo> disruptions) {
			mCallbacks.dInfoOnPostExecute(disruptions);
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
		    return mConnection.getInputStream();
		}
		  
	  }

}
