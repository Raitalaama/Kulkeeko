package raitalaama.kulkeeko;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;

public class MainActivity extends FragmentActivity {

	private StartMenu startMenu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		FragmentManager fm = getSupportFragmentManager();
		startMenu=(StartMenu) fm.findFragmentByTag("startMenu");
		
		// If the Fragment is non-null, then it is currently being
		// retained across a configuration change.
		if (startMenu == null) {
			startMenu = new StartMenu();
			fm.beginTransaction().add(R.id.main_activity,startMenu, "startMenu")
					.commit();
		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
