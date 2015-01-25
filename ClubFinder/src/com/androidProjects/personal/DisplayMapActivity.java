package com.androidProjects.personal;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;

public class DisplayMapActivity extends SherlockFragmentActivity implements RoutesFragment.NoticeDialogListener, LayersFragment.NoticeDialogListener 
{
	//private List<GeoPoint> points = new ArrayList<GeoPoint>();
	private ArrayList<Route> routes = new ArrayList<Route>();
	private Club club;
	private LatLng startPoint;
	//private MapView mapView;
	private GoogleMap mapView;
	private Route selectedRoute;
	private ActionBar topBar;
	FragmentManager fragmentManager;
	RoutesFragment rDialog = new RoutesFragment();
	LayersFragment lDialog = new LayersFragment();
	
	public final static String DIRECTIONS = "com.clubfinder.pro.DIRECTIONS";
	public final static String CURRENTLOCATION = "com.clubfinder.pro.LOCATION";
	public final static String THECLUB = "com.clubfinder.pro.CLUB";
	public final static String BACK_BUTTON = "com.clubfinder.pro.BACK";
	
	//final variables used to zoom to span both locations on the map
	private final int PADDING = 50; //px
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	
    	getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
    	
        setContentView(R.layout.activity_display_map);
    	
        topBar = getSupportActionBar();
        topBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
        topBar.setDisplayHomeAsUpEnabled(true);
        
        fragmentManager = getSupportFragmentManager();
        
        mapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview)).getMap();
        mapView.getUiSettings().setMyLocationButtonEnabled(false);
        //for blue dot updates on map
    	//mapView.setMyLocationEnabled(true);
        
        //retrieve the passed info from previous avtivity
        //get the club
        club = getIntent().getParcelableExtra(LoadDirectionsTask.ACLUB);
        
        //route and directions
        ArrayList<Route> rt = getIntent().getParcelableArrayListExtra(LoadDirectionsTask.DRIVING_INFO);
        
        startPoint = getIntent().getParcelableExtra(LoadDirectionsTask.START_LOCATION);

        //make the route object including drawing and add to list of routes
        for(int x=0; x<rt.size(); x++)
        {
        	Route r = new Route();
        	r.setTotalDistance(rt.get(x).getTotalDistance());
	        r.setTotalDuration(rt.get(x).getTotalDuration());
	        r.setPlaceMarks(rt.get(x).getPlaceMarks());
	        r.setDestination(rt.get(x).getDestination());
	        r.setPolyStringList(rt.get(x).getPolyStringList());
	        //Log.i("log_tag","Distance " + x + ": " + route.getTotalDistance());
	        //Log.i("log_tag","Route " + x + ": " + route.getPolylineString());
	        routes.add(r);
        }
        
        //set selectedRoute to default to start with
        selectedRoute = routes.get(0);
        
        //set the radio button to 1st route every time activity is called
        rDialog.setCheckedItem(0);
        
        //now check for map data
        if(mapView.getMapType() == GoogleMap.MAP_TYPE_SATELLITE)//.isSatellite())
        {
        	lDialog.setCheckedItem(1);
        }
        else
        {
        	lDialog.setCheckedItem(0);
        }
        
        //Log.i("log_tag","There are " + routes.size() + " different routes!!!"); 
        //Log.i("log_tag","Route 0: " + routes.get(0).getPolylineString());
        //Log.i("log_tag","Route 1: " + routes.get(1).getPolylineString());
     
        //using .get(0) is fine as it will always be the 1st destination to start with
        topBar.setTitle("  Directions to " + selectedRoute.getDestination());
        topBar.setSubtitle("  Route 1: " + selectedRoute.getTotalDistance() + " - " + selectedRoute.getTotalDuration() + " with traffic");

        //routes button
        ImageButton route = (ImageButton) findViewById(R.id.map_routes_button);
        if(routes.size() > 1) {
        	route.setImageDrawable(getResources().getDrawable(R.drawable.route));
	        route.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	         		//RoutesFragment dialog = new RoutesFragment();
	         		rDialog.setRouteList(routes);
	               	rDialog.show(fragmentManager, "Routes");
	            }
	        });
        }
        else //if only 1 route - darken the button and dont make it clickable
        {
        	route.setImageDrawable(getResources().getDrawable(R.drawable.route_dark));
        }
        //layers button
        ImageButton layers = (ImageButton) findViewById(R.id.map_layers_button);
        layers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
         		LayersFragment dialog = new LayersFragment();
               	lDialog.show(fragmentManager, "Layers");
            }
        });
        //list button
        ImageButton next = (ImageButton) findViewById(R.id.map_list_button);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	new GetDirectionsTask(DisplayMapActivity.this).execute(selectedRoute);
            	/*Intent listIntent = new Intent(DisplayMapActivity.this, ListDirectionsActivity.class);
            	listIntent.putStringArrayListExtra(DIRECTIONS, getListDirections(selectedRoute));
                startActivity(listIntent);*/
            }

        });
        
      //navigation button
        ImageButton nav = (ImageButton) findViewById(R.id.map_nav_button);
        nav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
        		Route root = routes.get(0);
        		List<PlaceMark> places = root.getPlaceMarks();
        		PlaceMark p = places.get(places.size()-1);
        		LatLng l = p.getLatLng();
        		launchNavigation(l.latitude, l.longitude);
            }
        });
        
        showMap(selectedRoute);
    }
    
    @Override
    public void onBackPressed() {
	    // do something on back.
		// after a club is selected we want to take the user to the next activity - club location 
        Intent intent = new Intent(DisplayMapActivity.this, DisplayClubLocation.class);
        intent.setAction(BACK_BUTTON);
        intent.putExtra(THECLUB, club);
        
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        
        startActivity(intent);
	    return;
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        showMap(selectedRoute);
    }
    
    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    //take option selected from routes and perform action
    @Override
    public void onRouteDialogItemClick(DialogFragment dialog, int routeNumber) {
        // User touched the dialog's button
    	selectedRoute = routes.get(routeNumber);
    	topBar.setTitle("  Directions to " + selectedRoute.getDestination());
        topBar.setSubtitle("  Route " + (routeNumber+1) + ": " + selectedRoute.getTotalDistance() + " - " + selectedRoute.getTotalDuration() + " with traffic");
        showMap(selectedRoute);
        EasyTracker.getTracker().sendEvent("Button Clicked", "Route", "", (long) routeNumber);
    }
    
    //cater for layer button options
	@Override
	   public void onLayerDialogItemClick(DialogFragment dialog, int layerNumber) {
	       // User touched the dialog's button
			if(layerNumber == 0)
			{
				mapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				EasyTracker.getTracker().sendEvent("Button Clicked", "Map View", "", (long) 0);
			}
			else
				mapView.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				EasyTracker.getTracker().sendEvent("Button Clicked", "Satellite View", "", (long) 0);
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
    
    
    public boolean canHandleIntent(Context context, Intent intent){
    PackageManager packageManager = context.getPackageManager();
    List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    return activities.size() > 0;
}
 
public void launchNavigation(Double latitude, Double longitude){
    String uri = "geo:0,0?q=" + latitude + "," + longitude + " (" + club.getClubName() + ")"; //"google.navigation:q=" + latitude + "," + longitude + "&mode=d"; //"google.navigation:ll=%f,%f"; // "google.navigation:ll=%f,%f"; //Uri.parse("google.navigation:q=" + latitude + "," + longitude)
    //String uri = "google.navigation:q=" + latitude + "," + longitude + "&mode=d";
    //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(uri, latitude, longitude)));
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //startActivity(intent);
    if(canHandleIntent(getBaseContext(),intent))
    	startActivity(intent);
    else
        Toast.makeText(getBaseContext(), "Please install Google Maps", Toast.LENGTH_SHORT).show();
}
    
    
    private class GetDirectionsTask extends AsyncTask<Route, Void, ArrayList<Direction>>{
    	
    	private final ProgressDialog dialog;
    	
    	public GetDirectionsTask(Context c){
    		dialog = new ProgressDialog(c);
    	}
    	
    	@Override
        protected void onPreExecute()
        {
    		this.dialog.setMessage("Loading Directions...");
        	this.dialog.show();
        }
    	
    	@Override
    	protected ArrayList<Direction> doInBackground(Route... directionList){
    		String driveDistance = ""; 
    		String driveInstruction = ""; 
    		String extraInstruction = "";
    		ArrayList<String> directionStrings;
    		ArrayList<Direction> d = new ArrayList<Direction>();
    		
    		directionStrings = getListDirections(directionList[0]);
    		for(int i=0; i<directionStrings.size(); i++){
    			String direction = directionStrings.get(i);
    			
    			if(direction.contains("m then"))
    			{
    				int substringDelim = (direction.indexOf("then") + 4);	//end of then 
    				int midSubstring = direction.indexOf(".", substringDelim); //get the position of the 1st occorance of . after the initial substring
    				int endSubstring = direction.lastIndexOf("."); //get the position of the last occorance of .
    				//Log.d("log_tag", "midSubstring = " + midSubstring);
    				//Log.d("log_tag", "endSubstring = " + endSubstring);
    				
    				//1st part
    				driveDistance = direction.substring(0, substringDelim); //"Drive 0.2 km then"
    				
    				//if only 2 parts this is the end
    				if(midSubstring == endSubstring || midSubstring+1 >= endSubstring) //if the positions are the same or more or less it means there is only 1 sentence to process
    				{
    					driveInstruction = direction.substring(substringDelim+1, endSubstring);
    					extraInstruction = "";
    				}
    				//if more than 2 parts we need to split it again
    				else if(endSubstring > midSubstring)
    				{
    					/*
    					 * EG
    					 * direction = "Drive 0.2 km then take a right on to the R114. Destination will be on your left."
    					 * substringDelim = 16;
    					 * midSubstring = 45;
    					 * endSubstring = 78;
    					 * end EG
    					*/
    					
    					driveInstruction = direction.substring(substringDelim+1, midSubstring);
    					extraInstruction = direction.substring(midSubstring+2, endSubstring); //+2 so it doesnt take the space
    				}
    				
    			}
    			else
    			{
    				driveDistance = "";
    				driveInstruction = direction;
    				extraInstruction = "";
    			}
    			d.add(new Direction(driveDistance,  driveInstruction, extraInstruction));
    		}
    		return d;
    	}
    	
    	@Override
        protected void onPostExecute(ArrayList<Direction> dl){
    		this.dialog.dismiss();
    		Intent listIntent = new Intent(DisplayMapActivity.this, ListDirectionsActivity.class);
        	listIntent.putParcelableArrayListExtra(DIRECTIONS, dl);//.putStringArrayListExtra(DIRECTIONS, getListDirections(selectedRoute));
            startActivity(listIntent);
    	}
    	
    	public ArrayList<String> getListDirections(Route r)
        {
        	//now retrieve the driving instructions
            List<PlaceMark> pm = r.getPlaceMarks();
            ArrayList<String> listArray = new ArrayList<String>();
            
            for(int i = 0; i < pm.size(); i++)
            {
            	listArray.add(pm.get(i).getInstructions());
    			//Log.d("log_tag", "Directions: " + listArray.get(i));
            }
            
            //now add the destination/club name 
            listArray.add(r.getDestination());
            //Log.d("log_tag", "Directions: " + r.getDestination());
            
            return listArray;
        }
    }
    
    
    /**
     * Method to show the Map given a set of co-ordinates
     */
    @SuppressLint("NewApi")
	public void showMap(Route r)
    {    	
    	Display display = getWindowManager().getDefaultDisplay();
    	Point size = new Point();
    	if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
    	    size.set(display.getWidth(), display.getHeight());
    	} 
    	else{
    		try {
                display.getSize(size);
            } catch (java.lang.NoSuchMethodError ignore) { // Older device
            	size.x = display.getWidth();
            	size.y = display.getHeight();
            }
    	}
    	int width = size.x; 
    	int height = size.y - 300; //-120 for borders + pin object
    	//Log.i("Bounds", "height = " + height);
    	
    	mapView.clear();
    	mapView.addMarker(new MarkerOptions().position(startPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.source_pin))); //1st location = phone location
    	mapView.addMarker(new MarkerOptions().position(club.getLocationPoint()).icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_pin)));
    	
    	PolylineOptions route = new PolylineOptions();
    	for(LatLng l: r.getLatLngPolyline()){
    		route.add(l);
    	}
    	
    	Polyline polyline = mapView.addPolyline(route);
    	polyline.setColor(Color.argb(160, 255, 0, 0));//.setColor(Color.RED);
    	
    	LatLngBounds bounds = getBounds(r.getLatLngPolyline());
    	
    	int paddingInPixels = (int)(Math.min(width, height) * 0.1);
    	mapView.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, paddingInPixels));
    	
    }
    
    public LatLngBounds getBounds(List<LatLng> points){
    	double minLatitude = Double.MAX_VALUE;
    	double maxLatitude = Double.MIN_VALUE;
    	double minLongitude = Double.MAX_VALUE;
    	double maxLongitude = Double.NEGATIVE_INFINITY;

        // Find the boundaries of the item set
        for(LatLng pt : points) 
        {
            double lat = pt.latitude;
            double lon = pt.longitude;
            //Log.i("Bounds", "lat = " + lat);

            maxLatitude = Math.max(lat, maxLatitude);
            minLatitude = Math.min(lat, minLatitude);
            maxLongitude = Math.max(lon, maxLongitude);
            minLongitude = Math.min(lon, minLongitude);   
        }
        
        //Log.i("Bounds", "maxLatitude = " + maxLatitude);
        //Log.i("Bounds", "minLatitude = " + minLatitude);
        //Log.i("Bounds", "maxLongitude = " + maxLongitude);
        //Log.i("Bounds", "minLongitude = " + minLongitude);

        return new LatLngBounds(new LatLng(minLatitude, minLongitude), new LatLng(maxLatitude, maxLongitude));

    }
    
    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this); // Add this method.
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mapView.setMyLocationEnabled(false);
        Log.d("log_tag", "DisplayMapActivity onPause Event");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mapView.setMyLocationEnabled(true);
        //myLocationOverlay.enableCompass(); 
        //myLocationOverlay.enableMyLocation();
    }
    
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mapView.setMyLocationEnabled(false);
        EasyTracker.getInstance().activityStop(this);
        //myLocationOverlay.disableCompass(); 
        //myLocationOverlay.disableMyLocation();
        Log.d("log_tag", "DisplayMapActivity onStop Event");
    }
}