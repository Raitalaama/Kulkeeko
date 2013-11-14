package raitalaama.kulkeeko;

import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements
		StartMenu.startMenuCallbacks,
		DisruptionInfoFragment.DisruptionCallbacks,
		AreaInfoFragment.AreaInfoCallbacks {

	private TextView disruptionStatus, areaInfoStatus, gpsStatus;
	private StartMenu startMenu;
	private DisruptionInfoFragment mDisruptionSearchHolder;
	private AreaInfoFragment mAreaSearchHolder;

	private boolean mGotDisruptions = false;
	private boolean mGotAreaInfo = false;
	private boolean mGotLocation = false;

	private Location mLocation;
	private AreaInfo mAreaInfo;
	private List<DisruptionInfo> mDisruptionInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FragmentManager fm = getSupportFragmentManager();
		startMenu = (StartMenu) fm.findFragmentByTag("startMenu");

		// If the Fragment is non-null, then it is currently being
		// retained across a configuration change.
		if (startMenu == null) {
			startMenu = new StartMenu();
			fm.beginTransaction()
					.add(R.id.main_activity, startMenu, "startMenu").commit();
		}

		mDisruptionSearchHolder = (DisruptionInfoFragment) fm
				.findFragmentByTag("disruptions");
		mAreaSearchHolder = (AreaInfoFragment) fm.findFragmentByTag("areaInfo");

		if (mDisruptionSearchHolder == null) {
			mDisruptionSearchHolder = new DisruptionInfoFragment();
			fm.beginTransaction().add(mDisruptionSearchHolder, "disruptions")
					.commit();
		}

		disruptionStatus = (TextView) findViewById(R.id.disruption_status);
		areaInfoStatus = (TextView) findViewById(R.id.area_info_status);
		gpsStatus = (TextView) findViewById(R.id.gps_status);

		if (mAreaSearchHolder == null) {
			mAreaSearchHolder = new AreaInfoFragment();
			fm.beginTransaction().add(mAreaSearchHolder, "areaInfo").commit();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void startSearch(boolean useLocation, int searchRadius,
			String placename) {
		mDisruptionSearchHolder.start();
		if (!useLocation) {
			mAreaSearchHolder.start(searchRadius, placename);
		} else {
			if (mGotLocation) {
				mAreaSearchHolder.start(searchRadius, mLocation);
			}
		}
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction().remove(startMenu).addToBackStack(null).commit();
		
		

	}

	@Override
	public void searchCoordinates(boolean useLocation) {
		// TODO check if gps is running and update status text

	}

	@Override
	public void dInfoOnPostExecute(List<DisruptionInfo> disruptions) {
		mDisruptionInfo = disruptions;
		disruptionStatus.setText(R.string.check_disruptions_found);

		mGotDisruptions = true;
		if (mGotAreaInfo) {

		}
	}

	@Override
	public void dInfoOnPreExecute() {
		disruptionStatus.setText(R.string.check_disruptions);
	}

	@Override
	public void dInfoOnCanceled() {
		// TODO Auto-generated method stub
	}

	@Override
	public void aInfoOnPostExecute(AreaInfo areaInfo) {
		mAreaInfo = areaInfo;

		if (areaInfo.getStops() != null && areaInfo.getLines() != null) {
			areaInfoStatus.setText("Arrayst loyty pysakeit "
					+ areaInfo.getStops().size() + " ja linjoi "
					+ areaInfo.getLines().size());
		} else {
			areaInfoStatus.setText("Ei toiminu jssonni");

		}

		mGotAreaInfo = true;
		if (mGotDisruptions) {
		}
	}

	@Override
	public void aInfoOnPreExecute() {
		areaInfoStatus.setText(R.string.check_area_info);
	}

	@Override
	public void aInfoOnCanceled() {
		// TODO Auto-generated method stub

	}

}
