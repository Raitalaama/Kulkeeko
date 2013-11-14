package raitalaama.kulkeeko;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class StartMenu extends Fragment implements OnClickListener {
	
	
	private static final String TAG = StartMenu.class.getSimpleName();

	private boolean mUseLocation;
	// private String placename;
	private int mSearchRadius;

	private SeekBar mSearchRadiusSelector;
	private EditText mPlacenameSelector;
	private TextView mSearchRadiusView;
	private SharedPreferences mSettings;
	private SharedPreferences.Editor mSettingsEditor;

	public static final String PREFERENCES_USE_LOCATION = "use_location";
	public static final String PREFERENCES_SEARCH_RADIUS = "search_radius";

	@Override
	public void onAttach(Activity activity) {
		Log.i(TAG, "onAttach(Activity)");
		super.onAttach(activity);
		
	
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate(Bundle)");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		

        mSettings = getActivity().getPreferences(0);
        mSettingsEditor = mSettings.edit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView()");
		View v = inflater.inflate(R.layout.start_menu, container, false);

		mSearchRadiusSelector = (SeekBar)v.findViewById(R.id.search_range_seeker);
		mPlacenameSelector=(EditText)v.findViewById(R.id.edit_placename);
		
		
		mSearchRadiusView=(TextView)v.findViewById(R.id.search_range_value);
		
		Button searchButton=(Button)v.findViewById(R.id.search_button);
		RadioButton radioLocation = (RadioButton)v.findViewById(R.id.radio_location);
		RadioButton radioPlace = (RadioButton)v.findViewById(R.id.radio_placename);
		
		searchButton.setOnClickListener(this);
		radioPlace.setOnClickListener(this);
		radioLocation.setOnClickListener(this);
		
		mUseLocation =mSettings.getBoolean(PREFERENCES_USE_LOCATION, true);
		if(mUseLocation){
			radioLocation.setChecked(true);
		}
		else{
			radioPlace.setChecked(true);
		}

		mSearchRadiusSelector.setProgress(mSettings.getInt(PREFERENCES_SEARCH_RADIUS, 50));
		updateSeekBar(mSettings.getInt(PREFERENCES_SEARCH_RADIUS, 50));
		mSearchRadiusSelector.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            	updateSeekBar(progress);
            }
 
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
 
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
		return v;

	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "onActivityCreated(Bundle)");

	
	}
	private void updateSeekBar(int progress) {
		mSearchRadius = progress;
		mSearchRadiusView.setText(String.valueOf(mSearchRadius));

		mSettingsEditor.putInt(PREFERENCES_SEARCH_RADIUS, progress);
		mSettingsEditor.commit();
	}



	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.radio_location:
			if(((RadioButton)v).isChecked()){
				mUseLocation = true;
				mSettingsEditor.putBoolean(PREFERENCES_USE_LOCATION,mUseLocation);
			}
			break;
		case R.id.radio_placename:
			if(((RadioButton)v).isChecked()){
				mUseLocation = false;
				mSettingsEditor.putBoolean(PREFERENCES_USE_LOCATION,mUseLocation);
			}
			break;
		case R.id.search_button:
			break;
	
		}
	}

}
