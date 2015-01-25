package com.androidProjects.personal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.androidProjects.personal.MyCurrentLocation.LocationResult;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

public class GAAClubFinderActivity extends SherlockFragmentActivity 
{
	public final static String MAP_COORDINATES = "com.clubfinder.pro.CO_ORDINATES";
	public final static String DRIVING_INFO = "com.clubfinder.pro.DRIVING";
	public final static String CLUB_NAME = "com.clubfinder.pro.CLUB";
	public final static String CURRENT_LOCATION = "com.clubfinder.pro.LOCATION";
	public final static String LOAD_MAP = "com.clubfinder.pro.MAP";
	public final static String ACCURACY = "com.clubfinder.pro.ACCURACY";
	private static final int REQUEST_CODE = 0;

	private static LatLng phoneLocation;
	//private static GeoPoint myLocation;
	private MyCurrentLocation myCurrentLocation; 
	private LocationResult locationResult;
	private ArrayList<Club> clubs = new ArrayList<Club>();
	
	ArrayList<String> items = new ArrayList<String>();
	
	private float bestAccuracy = Float.MAX_VALUE;
	//private TextView mTextView;
	
	private FragmentManager fragmentManager;
	
	//private ClubsJson json;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    { 
    	/*
    	 * StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	 * StrictMode.setThreadPolicy(policy);
    	 */
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setDisplayShowTitleEnabled(false);
        
        fragmentManager = getSupportFragmentManager();
        
        //createSelectionSpinnerItems();
        
        //get the stored preference for the drop down club list. if no preference set use 0
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int storedPreference = preferences.getInt("storedInt", 0);

        //create the spinner adapter for the sports categories
        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list, R.layout.spinner_layout);
        //SpinnerAdapter mSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, createSelectionSpinnerItems());
        
        OnNavigationListener mOnNavigationListener = new OnNavigationListener() {

        	SharedPreferences navPreferences = PreferenceManager.getDefaultSharedPreferences(GAAClubFinderActivity.this);

	    	@Override
	    	public boolean onNavigationItemSelected(int position, long itemId) {
	    		  
	    		//Write value of stored preference to prefernce manager
	    		SharedPreferences.Editor editor = navPreferences.edit();
	    		editor.putInt("storedInt", position); // value to store
	    		editor.commit();
	    		
	    		//Log.d("ITEMS", items.get(position));
	    		//getClubs(items.get(position));

	    		switch(position)
	    		{
	        	    case(0):
	        	    {
	        	    	//Leinster Rugby
	        	    	getClubList("ConnachtRugby.json");
	        	    	break;
	        	    }
	        	    case(1):
	        	    {
	        	    	//Leinster Rugby
	        	    	getClubList("LeinsterRugby.json");
	        	    	break;
	        	    }
	        	    case(2):
	        	    {
	        	    	//Leinster Hockey
	        	    	getClubList("MunsterRugby.json");
	        	    	break;
	        	    }
	        	    case(3):
	        	    {
	        	    	//Leinster Rugby
	        	    	getClubList("UlsterRugby.json");
	        	    	break;
	        	    }
	        	    case(4):
	        	    {
	        	    	//Leinster Hockey
	        	    	getClubList("ConnachtHockey.json");
	        	    	break;
	        	    }
	        	    case(5):
	        	    {
	        	    	//Ulster Hockey
	        	    	getClubList("LeinsterHockey.json");
	        	    	break;
	        	    }
	        	    case(6):
	        	    {
	        	    	//Munster Hockey
	        	    	getClubList("MunsterHockey.json");
	        	    	break;
	        	    }
	        	    case(7):
	        	    {
	        	    	//Connacht Hockey
	        	    	getClubList("UlsterH.json");
	        	    	break;
	        	    }
	        	    case(8):
	        	    {
	        	    	//Antrim GAA
	        	    	getClubList("Antrim.json");
	        	    	break;
	        	    }
	        	    case(9):
	        	    {
	        	    	//Armagh GAA
	        	    	getClubList("Armagh.json");
	        	    	break;
	        	    }
	        	    case(10):
	        	    {
	        	    	//Carlow GAA
	        	    	getClubList("Carlow.json");
	        	    	break;
	        	    }
	        	    case(11):
	        	    {
	        	    	//Cavan GAA
	        	    	getClubList("Cavan.json");
	        	    	break;
	        	    }
	        	    case(12):
	        	    {
	        	    	//Clare GAA
	        	    	getClubList("Clare.json");
	        	    	break;
	        	    }
	        	    case(13):
	        	    {
	        	    	//Cork GAA
	        	    	getClubList("Cork.json");
	        	    	break;
	        	    }
	        	    case(14):
	        	    {
	        	    	//Derry GAA
	        	    	getClubList("Derry.json");
	        	    	break;
	        	    }
	        	    case(15):
	        	    {
	        	    	//Donegal GAA
	        	    	getClubList("Donegal.json");
	        	    	break;
	        	    }
	        	    case(16):
	        	    {
	        	    	//Down GAA
	        	    	getClubList("Down.json");
	        	    	break;
	        	    }
	        	    case(17):
	        	    {
	        	    	//Dublin GAA
	        	    	getClubList("dublin_gaa.json");
	        	    	break;
	        	    }
	        	    case(18):
	        	    {
	        	    	//Fermanagh GAA
	        	    	getClubList("Fermanagh.json");
	        	    	break;
	        	    }
	        	    case(19):
	        	    {
	        	    	//Galway GAA
	        	    	getClubList("Galway.json");
	        	    	break;
	        	    }
	        	    case(20):
	        	    {
	        	    	//Kerry GAA
	        	    	getClubList("Kerry.json");
	        	    	break;
	        	    }
	        	    case(21):
	        	    {
	        	    	//Kildare GAA
	        	    	getClubList("Kildare.json");
	        	    	break;
	        	    }
	        	    case(22):
	        	    {
	        	    	//Kilkenny GAA
	        	    	getClubList("Kilkenny.json");
	        	    	break;
	        	    }
	        	    case(23):
	        	    {
	        	    	//Laois GAA
	        	    	getClubList("Laois.json");
	        	    	break;
	        	    }
	        	    case(24):
	        	    {
	        	    	//Leitrim GAA
	        	    	getClubList("Leitrim.json");
	        	    	break;
	        	    }
	        	    case(25):
	        	    {
	        	    	//Limerick GAA
	        	    	getClubList("Limerick.json");
	        	    	break;
	        	    }
	        	    case(26):
	        	    {
	        	    	//Longford GAA
	        	    	getClubList("Longford.json");
	        	    	break;
	        	    }
	        	    case(27):
	        	    {
	        	    	//Louth GAA
	        	    	getClubList("Louth.json");
	        	    	break;
	        	    }
	        	    case(28):
	        	    {
	        	    	//Mayo GAA
	        	    	getClubList("Mayo.json");
	        	    	break;
	        	    }
	        	    case(29):
	        	    {
	        	    	//Meath GAA
	        	    	getClubList("Meath.json");
	        	    	break;
	        	    }
	        	    case(30):
	        	    {
	        	    	//Monaghan GAA
	        	    	getClubList("Monaghan.json");
	        	    	break;
	        	    }
	        	    case(31):
	        	    {
	        	    	//Offaly GAA
	        	    	getClubList("Offaly.json");
	        	    	break;
	        	    }
	        	    case(32):
	        	    {
	        	    	//Roscommon GAA
	        	    	getClubList("Roscommon.json");
	        	    	break;
	        	    }
	        	    case(33):
	        	    {
	        	    	//Sligo GAA
	        	    	getClubList("Sligo.json");
	        	    	break;
	        	    }
	        	    case(34):
	        	    {
	        	    	//Tipperary GAA
	        	    	getClubList("Tipperary.json");
	        	    	break;
	        	    }
	        	    case(35):
	        	    {
	        	    	//Tyrone GAA
	        	    	getClubList("Tyrone.json");
	        	    	break;
	        	    }
	        	    case(36):
	        	    {
	        	    	//Waterford GAA
	        	    	getClubList("Waterford.json");
	        	    	break;
	        	    }
	        	    case(37):
	        	    {
	        	    	//Westmeath GAA
	        	    	getClubList("Westmeath.json");
	        	    	break;
	        	    }
	        	    case(38):
	        	    {
	        	    	//Wexford GAA
	        	    	getClubList("wexford_gaa.json");
	        	    	break;
	        	    }
	        	    case(39):
	        	    {
	        	    	//Wicklow GAA
	        	    	getClubList("Wicklow.json");
	        	    	break;
	        	    }
	    		}
	    		return true;
    	  	}
    	};
    	
    	//set up the actionbar to reflect choices
    	actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
    	actionBar.setSelectedNavigationItem(storedPreference);
    	 
    	// Look up the AdView as a resource and load a request. 
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    	//start polling for location updates
    	myCurrentLocation = new MyCurrentLocation(this);
    	locationResult = new LocationResult(){
    	    @Override
    	    public void gotLocation(Location location){
    	        //Got the location!
    	    	float accuracy = location.getAccuracy();
    	    	if(accuracy < bestAccuracy){
    	    		bestAccuracy = accuracy;
    	    		phoneLocation = new LatLng(location.getLatitude(), location.getLongitude());
    	    	}
    	    	//myLocation = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
    	    	//Log.d("log_tag", "Location Found " + phoneLocation.toString());
    	    }
    	};
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getSupportMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        MenuItem aboutMenuItem = menu.findItem(R.id.menu_about);
        aboutMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new AboutFragment().show(fragmentManager, "About");
                EasyTracker.getTracker().sendEvent("Button Clicked", "About", "", (long) 0);
                return false;
            }
        });
        /*MenuItem removeAdsMenuItem = menu.findItem(R.id.menu_remove_ads);
        removeAdsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            	new RemoveAdsFragment().show(fragmentManager, "Remove Ads");
            	EasyTracker.getTracker().sendEvent("Button Clicked", "Remove Ads", "", (long) 0);
                return false;
            }
        });*/
        MenuItem rateMenuItem = menu.findItem(R.id.menu_rate);
        rateMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            	try {
            	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
            	} catch (android.content.ActivityNotFoundException anfe) {
            	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+getPackageName())));
            	}
            	EasyTracker.getTracker().sendEvent("Button Clicked", "Rate App", "", (long) 0);
                //startActivity(new Intent(GAAClubFinderActivity.this, MyMusicActivity.class));
                return false;
            }
        });

        return true;
    }
    
    
    /*
	 * Get the club information and populate the listView
	 * 
	 * online kml file for GAA Clubs can be found in resevoirdubs.ie
	 * https://maps.google.ie/maps/ms?ie=UTF8&hl=en&authuser=0&msa=0&output=kml&msid=208402889968711818069.0004894dd8bf581253c15
	 * This file has been updated and saved in the assets folder
	 * 
	 * online kml file for Rugby Clubs can be found at leinsterFans.com/links
	 * https://maps.google.com/maps/ms?ie=UTF8&hl=en&msa=0&msid=110253138783110582892.0004545cf66431b9531dd&t=h
	 * This file has been updated and saved in the assets folder
	 * 
	 * hockey club information is taken from leinsterhockey.ie/clubLocator/province:leinster
	 * once its updated I might use the html source as an asset file to speed searches up
	 * 
	 * if using online html file to get hockey information use the following command and uncomment class below
	 * new RetrieveHockeyInfo(this).execute(); //Hockey
	 */
    
    public void getClubList(String resource)
    {
    	ClubInfo clubInfo = new ClubInfo();
    	try
    	{
    		//assign a file from the assets folder to read in
    		InputStream i = getResources().getAssets().open(resource);
    	
    		//extract information and assign it to clubs list
    		clubs = clubInfo.getClubJSONInfo(i);
    		//clubs = clubInfo.getClubInfo(i);
    	}
    	catch(Exception e)
    	{
    		Toast.makeText(this, "Error retrieving data.", Toast.LENGTH_LONG).show();
        	Log.e("Exception",e.toString());
        	//finish();
    	}
    	
    	// call method to populate list view
    	setClubList(clubs);
    }
    
    //populate the list view on initial page
    public void setClubList(ArrayList<Club> theClubs)
    {
    	final ArrayList<Club> dc = theClubs; //created so it can be accessed inside nested class
    	
    	//ListView stuff
    	ListView listView = (ListView) findViewById(R.id.clubname); 	
    	listView.setFastScrollEnabled(true);
    	listView.setAdapter(new AlphabeticalAdapter(getApplicationContext(), R.layout.club_list, new ClubInfo().getListClubNames(theClubs))); 
		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				//get the club that is selected from the list
				Club club = dc.get(position);

				// after a club is selected we want to take the user to the next activity - club location 
	            Intent intent = new Intent(GAAClubFinderActivity.this, DisplayClubLocation.class);
	            intent.setAction(LOAD_MAP);
	            intent.putExtra(CLUB_NAME, club);
	            if(phoneLocation != null)
	            {
	            	intent.putExtra(CURRENT_LOCATION, phoneLocation);
	            	intent.putExtra(ACCURACY, bestAccuracy);
	            }
	            startActivity(intent);
	            EasyTracker.getTracker().sendEvent("Club Selected", club.getClubName(), "", (long) 0);
			}
		});
    }
    
    
    private String[] createSelectionSpinnerItems() {
    	//open the json file for reading
    	try
    	{
    		//assign a file from the assets folder to read in
    		InputStream i = getResources().getAssets().open("allclubs.json");
    	
    		//extract information and assign it to clubs list
    		items = new ClubsJson().getSportSelectionItems(i);
    		//clubs = clubInfo.getClubInfo(i);
    	}
    	catch(Exception e)
    	{
    		Toast.makeText(this, "Error retrieving data.", Toast.LENGTH_LONG).show();
        	Log.e("Exception",e.toString());
    	}   	
    	
    	String[] results = items.toArray(new String[0]);
    	return results;
    }
    
/*    private void getClubs(String sportType) {
    	try
    	{
    		//assign a file from the assets folder to read in
    		InputStream i = getResources().getAssets().open("allclubs.json");
    	
        	// call method to populate list view
        	setClubList(new ClubsJson().getClubInfo(i, sportType));
    	}
    	catch(Exception e)
    	{
    		Toast.makeText(this, "Error retrieving clubs", Toast.LENGTH_LONG).show();
        	Log.e("Exception",e.toString());
    	}   	
    }*/
    
    
    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this); // Add this method.
        //myCurrentLocation.getNetworkLocation(this, locationResult);
        Log.d("log_tag", "GAAClubFinder onStart Event: Listener Updates Started");
    }
    
    /** Register for the updates when Activity is in foreground */
	
    @Override
	protected void onResume() {
		super.onResume();
		myCurrentLocation.getNetworkLocation(this, locationResult);
		//myCurrentLocation.getLocation(this, locationResult);
		//Log.d("log_tag", "GAAClubFinder onResume Event: Listener Updates Started");
		
		//check for google play services before proceeding
    	int googleServicesResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	if(googleServicesResult != ConnectionResult.SUCCESS){
    		Dialog dialog = GooglePlayServicesUtil.getErrorDialog(googleServicesResult, GAAClubFinderActivity.this, REQUEST_CODE);
            dialog.setCancelable(false); 
            //dialog.setOnDismissListener(getOnDismissListener());
            dialog.show();
    	}
		//myCurrentLocation.getLocation(this, locationResult);
		//Log.i("Google", "googleServicesResult = " + googleServicesResult);
		//Log.i("Google", "SUCCESS = " + ConnectionResult.SUCCESS);
	}

	/** Stop the updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();

		//myCurrentLocation.removeListenerUpdates();
		//Log.d("log_tag", "GAAClubFinder onPause Event: Listener Updates Removed");
	}
	
	/** Stop the updates when Activity is stopped */
    @Override
	protected void onStop() {
	    super.onStop();
	    myCurrentLocation.removeListenerUpdates();
	    EasyTracker.getInstance().activityStop(this);
	    Log.d("log_tag", "GAAClubFinder onStop Event: Listener Updates Removed");
	}


    
    /*
     * AlphabeticalAdapter class allows for quick scrolling on lists
     */
    private class AlphabeticalAdapter extends ArrayAdapter<String> implements SectionIndexer
    {
        private HashMap<String, Integer> alphaIndexer;
        private String[] sections;

        public AlphabeticalAdapter(Context c, int resource, List<String> data)
        {
            super(c, resource, data);
            alphaIndexer = new HashMap<String, Integer>();
            for (int i = 0; i < data.size(); i++)
            {
            	String letter = "";
            	String s = data.get(i);
                if(s.length() > 1)
                	letter = s.substring(0, 1).toUpperCase();
                if (!alphaIndexer.containsKey(letter))
                    alphaIndexer.put(letter, i);
            }

            Set<String> sectionLetters = alphaIndexer.keySet();
            ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
            Collections.sort(sectionList);
            sections = new String[sectionList.size()];
            for (int i = 0; i < sectionList.size(); i++)
                sections[i] = sectionList.get(i);   
        }

        public int getPositionForSection(int section)
        {   
            return alphaIndexer.get(sections[section]);
        }

        public int getSectionForPosition(int position)
        {
            return 1;
        }

        public Object[] getSections()
        {
            return sections;
        }
    }
}