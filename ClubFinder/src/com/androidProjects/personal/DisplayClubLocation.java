package com.androidProjects.personal;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.androidProjects.personal.MyCurrentLocation.LocationResult;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DisplayClubLocation extends SherlockFragmentActivity implements LayersFragment.NoticeDialogListener{
	
	private Club club;
	private Timer timer;
 
	protected static LatLng phoneLocation;
	private MyCurrentLocation myCurrentLocation;
	private LocationResult locationResult;

	private boolean timerRunning = false;
	LayersFragment lDialog = new LayersFragment();
	
	public final static String DIRECTIONS = "com.clubfinder.pro.DIRECTIONS";
	public final static String MYCURRENTLOCATION = "com.clubfinder.pro.LOCATION";
	public final static String CURRENT_LOCATION = "com.clubfinder.pro.LOCATION";
	public final static String LOAD_MAP = "com.clubfinder.pro.MAP";
	public final static String CLUB_NAME = "com.clubfinder.pro.CLUB";

	private GoogleMap mapView;
	
	private float bestAccuracy = Float.MAX_VALUE;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	
    	getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY); 
        setContentView(R.layout.display_club_location);
        
        ActionBar topBar = getSupportActionBar();
        topBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
        topBar.setDisplayHomeAsUpEnabled(true);
        
        /*
         * footer for this View shows progress dialog while waiting for the location
         * After the location is found this disappears and is replaced by a button
         */
        final ProgressBar locationProgress = (ProgressBar) findViewById(R.id.my_location_progress);
        final TextView waitingText = (TextView) findViewById(R.id.waiting_text);
        final ImageButton getDirections = (ImageButton) findViewById(R.id.directions_button);
        final ImageButton getLayers = (ImageButton) findViewById(R.id.layers_button);
        final View divider = (View) findViewById(R.id.map_divider);
        getDirections.setVisibility(View.GONE);
        getLayers.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
        
        //listener for getdirections
        getDirections.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if(phoneLocation != null)
            	{
            		new LoadDirectionsTask(phoneLocation, club, DisplayClubLocation.this).execute();
            	}
            	else
            	{
            		if(myCurrentLocation.getEnableLocationButtonState()) //true - settings have possibly been turned on
            		{
            			Intent intent = new Intent(DisplayClubLocation.this, DisplayClubLocation.class);
        	            intent.setAction(LOAD_MAP);
        	            intent.putExtra(CLUB_NAME, club);
        	            if(phoneLocation != null)
        	            {
        	            	intent.putExtra(CURRENT_LOCATION, phoneLocation);
        	            }
        	            startActivity(intent);
            		}
            		else
            			myCurrentLocation.showGPSDisabledAlertToUser();
            	}
            }
        });
        
      //listener for getdirections
        getLayers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	FragmentManager fragmentManager = getSupportFragmentManager();
         		//LayersFragment dialog = new LayersFragment();
               	lDialog.show(fragmentManager, "Layers");
            }
        });
        
        //find out which activity we are coming from
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals(DisplayMapActivity.BACK_BUTTON))
        {
        	//club and possible myLocation value
            club = getIntent().getParcelableExtra(GAAClubFinderActivity.CLUB_NAME);
            
    		locationProgress.setVisibility(View.GONE);
    		waitingText.setVisibility(View.GONE);

    		getDirections.setVisibility(View.VISIBLE);
    		getLayers.setVisibility(View.VISIBLE);
    		divider.setVisibility(View.VISIBLE);
        }
        else //coming from GAAClubFinderActivity
        {
	        //club and possible myLocation value
	        club = getIntent().getParcelableExtra(GAAClubFinderActivity.CLUB_NAME);
	        //phoneLocation = getIntent().getParcelableExtra(GAAClubFinderActivity.CURRENT_LOCATION);
	        getIntent().getFloatExtra(GAAClubFinderActivity.ACCURACY, bestAccuracy);//.getParcelableExtra(GAAClubFinderActivity.ACCURACY);

        	if(getIntent().getParcelableExtra(GAAClubFinderActivity.CURRENT_LOCATION) != null){
        		phoneLocation = getIntent().getParcelableExtra(GAAClubFinderActivity.CURRENT_LOCATION);
        		locationProgress.setVisibility(View.GONE);
        		waitingText.setVisibility(View.GONE);

        		getDirections.setVisibility(View.VISIBLE);
        		getLayers.setVisibility(View.VISIBLE);
        		divider.setVisibility(View.VISIBLE);
        	}
        	else {
        		
        		if(isLocationProviderAvailable())
        		{
        			/*
	                 * start a Timer to ensure that after 25 seconds we return the last known location for
	                 * either the GPS or network device if one has not been retrieved 
	                 */
        			startTimeoutTimer();
        		}
        		else
        		{
        			locationProgress.setVisibility(View.GONE);
            		waitingText.setVisibility(View.GONE);
            		getDirections.setVisibility(View.VISIBLE);
            		getLayers.setVisibility(View.VISIBLE);
            		divider.setVisibility(View.VISIBLE);
            		//Toast.makeText(DisplayClubLocation.this, "No Directions Available. Please check your location settings.", Toast.LENGTH_LONG).show();
        		}
        	}
        }
        
        //ActionBar title
        topBar.setTitle(club.getClubName());
        
        //Mapview creation
        mapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.clubview)).getMap();

        //mapView = (MapView) findViewById(R.id.clubview); 
        
        //now check for map data and update static checkedItem
        if(mapView.getMapType() == GoogleMap.MAP_TYPE_SATELLITE)//.isSatellite())
        {
        	lDialog.setCheckedItem(1);
        }
        else
        {
        	lDialog.setCheckedItem(0);
        }
        
        //mapView.setMyLocationEnabled(true);

        //show the club position on the map
        showMap(club);
        
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
    	    	
    	    	//update the footer bar
    	    	locationProgress.setVisibility(View.GONE);
        		waitingText.setVisibility(View.GONE);
    	    	getDirections.setVisibility(View.VISIBLE);
    	    	getLayers.setVisibility(View.VISIBLE);
    	    	divider.setVisibility(View.VISIBLE);
    	    	if(timerRunning == true)
    	    	{
    	    		cancelTimer();
    	    	}
    	    }
    	};
    	
    	myCurrentLocation.getLocation(this, locationResult);
    	Log.d("log_tag", "DisplayClubLocation onCreate Event: Listener Updates Started");
    }
    
	@Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    	switch (item.getItemId()) 
    	{
	        case android.R.id.home:
	        {
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, GAAClubFinderActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
	            
	            startActivity(intent);
	            return true;
	        }
	        default:
	        {
	        	return super.onOptionsItemSelected(item);
	        }
    	}
    }
	
	@Override
	   public void onLayerDialogItemClick(DialogFragment dialog, int layerNumber) {
	       // User touched the dialog's button
			if(layerNumber == 0)
			{	
				mapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);//.setSatellite(false);
				EasyTracker.getTracker().sendEvent("Button Clicked", "Map View", "", (long) 0);
			}
			else
			{
				mapView.setMapType(GoogleMap.MAP_TYPE_SATELLITE);//.setSatellite(true);
				EasyTracker.getTracker().sendEvent("Button Clicked", "Satellite View", "", (long) 0);
			}
	    }
    
	//check to see that locationproviders are available
    public boolean isLocationProviderAvailable()
    {
    	boolean gpsEnabled = false;
    	boolean networkEnabled = false;
    	LocationManager locMan = (LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
		try
        {
			gpsEnabled = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex){}
        
        try
        {
        	networkEnabled = locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex){}
        
        if(!gpsEnabled && !networkEnabled)
        {
        	return false;
        }
        else
        	return true;
    }
    
    public void showMap(Club c)
    {    	 
        mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(c.getLocationPoint(), 12));
    	Marker clubMarker = mapView.addMarker(new MarkerOptions().position(c.getLocationPoint()));
    	clubMarker.setTitle(c.getClubName());
    	clubMarker.setSnippet(c.getDescription());
    	mapView.setInfoWindowAdapter(new InfoPopupAdapter(getLayoutInflater()));
    	clubMarker.showInfoWindow();
    }

    
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();  
        myCurrentLocation.removeListenerUpdates();
        
        Log.d("log_tag", "DisplayClubLocation onPause Event");//: Listener Updates Removed");
    }
    
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        
        myCurrentLocation.removeListenerUpdates();
        EasyTracker.getInstance().activityStop(this);
        Log.d("log_tag", "DisplayClubLocation onStop Event: Listener Updates Removed");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();   
        if(mapView.isMyLocationEnabled()){
        	mapView.setMyLocationEnabled(false);
        }
        myCurrentLocation.getLocation(this, locationResult);
        
        Log.d("log_tag", "DisplayClubLocation onResume Event");// Listener Updates Started");
    }
    
    public void startTimeoutTimer()
    {
    	timer=new Timer();
    	timer.schedule(new GetLastLocation(), 25000);
    	timerRunning = true;
    }
    
    public void cancelTimer() { 
    	timer.cancel(); 
    	timerRunning = false;
    }
    
    class GetLastLocation extends TimerTask 
    {
    	@Override
        public void run() 
        {
    		DisplayClubLocation.this.runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	            	try
	            	{
	            		myCurrentLocation.updateWithLastLocation();
	            	}
	            	catch(NullPointerException e)
	            	{
	            		Toast.makeText(DisplayClubLocation.this, "No Directions Available. Please check your location settings.", Toast.LENGTH_LONG).show();
	            		Log.e("log_tag", "NPE: Could not retrieve any location as location providers are turned off.");
	            	}
	            }
    		});
        }
    };
}
