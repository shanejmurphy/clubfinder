package com.androidProjects.personal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/*
 * LoadDirectionsTask is a thread that forms the maps json url based on supplied parameters
 * and then parses the information to retrieve the various locations to be drawn on the map
 */

class LoadDirectionsTask extends AsyncTask<Void, Void, Void>
{
	private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
	
	public final static String MAP_COORDINATES = "com.clubfinder.pro.CO_ORDINATES";
	public final static String DRIVING_INFO = "com.clubfinder.pro.DRIVING";
	public final static String ACLUB = "com.clubfinder.pro.CLUB";
	public final static String START_LOCATION = "com.clubfinder.pro.START";
	
	private LatLng firstPoint;
	private Club aClub;
	private Context context;
	private final ProgressDialog dialog;
	private ArrayList<Route> routes = new ArrayList<Route>();
	
	
	public LatLng getDestinationPoint(Club gc)
	{
		return gc.getLocationPoint();//gc.getLocation();
	}
	
	public LoadDirectionsTask(LatLng startPoint, Club clubName, Context c)
	{
    	firstPoint = startPoint;
    	aClub = clubName;// = endPoint;
    	context = c;
    	dialog = new ProgressDialog(context);
	}
	
	
	@Override
    protected void onPreExecute()
    {
		this.dialog.setMessage("Loading Directions...");
    	this.dialog.show();
    }
    
	
	@Override
	protected Void doInBackground(Void... params)
	{
		// Connect to the Google Maps json service
		// containing the directions from one point to another.
		// start at current location end at destination
		try
		{
			getDirections(formURL());
		}
		catch(Exception e)
		{
			Log.e("ERROR:", "Could not retrieve the directions");
		}
		return null;
	}
	
	protected String formURL()
	{
		StringBuilder urlString = new StringBuilder();
		LatLng lastPoint = getDestinationPoint(aClub);
		urlString
			.append(BASE_URL)
			.append("origin=")
			.append(firstPoint.latitude)//getLatitudeE6() / 1E6)	//start point
			.append(",")
			.append(firstPoint.longitude)//.getLongitudeE6() / 1E6)
			.append("&destination=")
			.append(lastPoint.latitude)//getLatitudeE6() / 1E6)	//end point
			.append(",")
			.append(lastPoint.longitude)//getLongitudeE6() / 1E6)
			.append("&sensor=true&alternatives=true");//need to set sensor value to true as per google docs; alternatives = more routes
		
		return urlString.toString();
	}
	
	private void getDirections(String jsonURL) throws Exception
    {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(jsonURL);
		
		try 
		{
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode != HttpStatus.SC_OK) 
			{
				Log.e("log_tag", "Failed to download file " + statusCode + " for URL " + jsonURL);	
			} 
			else 
			{
				Log.d("log_tag", "Reading in the content");
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while((line = reader.readLine()) != null) 
				{
					builder.append(line);
				}
			}
		} 
		catch (MalformedURLException e)
		{
			Log.e("log_tag", "Malformed URL "+e.toString());
		}
		catch (ClientProtocolException e) 
		{
			Log.e("log_tag", "Client Protocol Exception "+e.toString());
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			Log.e("log_tag", "IO Exception "+e.toString());
			e.printStackTrace();
		}
		catch(Exception e)
		{
            Log.e("log_tag", "Error in http connection "+e.toString());
		}
		
		Log.d("log_tag", "Parsing the content");
		parseResponse(builder.toString()); //return the web content
	}
	
	private void parseResponse(String directions) throws Exception
	{
		// Parse the KML file returned by the Google Maps web service
		// using the default XML DOM parser.
		//
		
		List<LatLng> pointList = new ArrayList<LatLng>();	//array for the geopoints in the route object
		//Route route = new Route();
		
		//add starting location to the list
		pointList.add(firstPoint);
		
		try 
		{
    		JSONObject jsonObject = new JSONObject(directions); //a big string of values
    		
    		//Route Data -- contains ALL routes
    		JSONArray routesArray = jsonObject.getJSONArray("routes");
    		
    		if(routesArray.length() > 0)
    		{
    			for(int j=0; j<routesArray.length(); j++)
	    		{
    				List<PlaceMark> placeMarkList = new ArrayList<PlaceMark>();
    				List<String> polyStrings = new ArrayList<String>();
    				
    				// Grab the first route
					JSONObject theRoute = routesArray.getJSONObject(j);
						
					// Take all legs from the route
					JSONArray legs = theRoute.getJSONArray("legs");
					
					// Grab first leg
					JSONObject leg = legs.getJSONObject(0);
		    		
					JSONObject distanceObject = leg.getJSONObject("distance");
					String distance = distanceObject.getString("text");
		    		//route.setTotalDistance(distanceObject.getString("text"));
					
					JSONObject durationObject = leg.getJSONObject("duration");
					String duration = durationObject.getString("text");
		    		//route.setTotalDuration(durationObject.getString("text"));
		    		
		    		//Log.d("log_tag", "The Total Distance of this journey is: " + route.getTotalDistance());
		    		//Log.d("log_tag", "The Total Duration of this journey is: " + route.getTotalDuration());
		    		
		    		JSONArray steps = leg.getJSONArray("steps");
		    		Log.d("log_tag", "The Total size Of Steps is: " + steps.length());
	
		    		for(int i = 0; i < steps.length(); i++) 
					{
		    			//create Movie Object
		    			//PlaceMark placemark = new PlaceMark();
		    			
		    			JSONObject stepObject = steps.getJSONObject(i);
		    			
		    			//placemark distance
		    			JSONObject stepDistanceObject = stepObject.getJSONObject("distance");
		    			String placemarkDistance = stepDistanceObject.getString("text");
		    			//placemark.setDistance(stepDistanceObject.getString("text"));
					
						//placemark duration
						JSONObject stepDurationObject = stepObject.getJSONObject("duration");
						String placemarkDuration = stepDurationObject.getString("text");
		    			//placemark.setDuration(stepDurationObject.getString("text"));
		    			
		    			//placemark location/co-ordinates
		    			JSONObject locationObject = stepObject.getJSONObject("end_location");
		    			LatLng placemark = new LatLng(Double.parseDouble(locationObject.getString("lat")), Double.parseDouble(locationObject.getString("lng")));
		    			//GeoPoint placemarkLocation = new GeoPoint((int)(Double.parseDouble(locationObject.getString("lat")) * 1E6),(int)(Double.parseDouble(locationObject.getString("lng")) * 1E6));
		    			//placemark.setLocation(new GeoPoint((int)(Double.parseDouble(locationObject.getString("lat")) * 1E6),(int)(Double.parseDouble(locationObject.getString("lng")) * 1E6)));
	
		    			//now add that location to the geoPoint List in Route
		    			pointList.add(placemark);
		    			
		    			//Log.d("log_tag", "Location: " + (placemark.getLocation().getLatitudeE6() / 1E6)+ ", " + (placemark.getLocation().getLongitudeE6() / 1E6));
		    			
		    			//placemark instructions
		    			//need to manipulate the result of this to get rid of html tags
		    			stepObject.getString("html_instructions");
		    			String fixedString = stepObject.getString("html_instructions").replaceAll("<div[^>]+>", ". ");
		    			String [] dirs = fixedString.split("<[^>]+>");
		    			String instructions = "";
		    			for(String s: dirs)
		    			{
		    				instructions = instructions.concat(s);
		    			}
		    			
		    			//setInstructions below
		    			//Log.d("log_tag", "Drive " + placemark.getDistance() + " then " + instructions.substring(0, 1).toLowerCase() + instructions.substring(1, instructions.length()));
		    			
		    			//placemark polyline
						JSONObject polyObject = stepObject.getJSONObject("polyline");
		    			String polyLines = polyObject.getString("points");	//returns List<GeoPoint> Object
		    			polyStrings.add(polyLines);
		    			
		    			//now set Driving Instructions
		    			//do a test to make sure we avoid NPE
		    			String placemarkInstructions = "";
		    			if(i > 0)
		    			{
		    				placemarkInstructions = "Drive " + placeMarkList.get(i-1).getDistance() + " then " + instructions/*.substring(0, 1).toLowerCase() + instructions.substring(1, instructions.length())*/ + ".";
		    				//Log.d("log_tag", "Drive " + placeMarkList.get(i-1).getDistance() + " then " + instructions.substring(0, 1).toLowerCase() + instructions.substring(1, instructions.length()));
		    			}
		    			else
		    			{
		    				placemarkInstructions = instructions;
		    				//Log.d("log_tag", instructions);
		    			}
		    			
		    			//add the placemark to the List of placeMarks
		    			placeMarkList.add(new PlaceMark(/*placemarkLocation*/placemark, placemarkDistance, placemarkDuration, placemarkInstructions));
					}
		    		
		    		//JSONObject polyObject = theRoute.getJSONObject("overview_polyline");
		    		//String polyString = polyObject.getString("points");
		    		
		    		String destination = aClub.getClubName();
		    		
		    		Log.d("log_tag", "POLYSTRINGS SIZE = " + polyStrings.size());
		    		
		    		//now add that last location to the geoPoint List in Route and set the ROute
	    			pointList.add(getDestinationPoint(aClub));
	    			Log.i("Route", "pointList Size = " + pointList.size());
	    			routes.add(new Route(distance, duration, pointList, placeMarkList, destination, polyStrings));//polyString));
    			}
    		}
    		else
    		{
    			Log.d("log_tag", "Directions are not available at this moment.");
    		}
		} 
		catch (Exception e) 
		{
			Log.e("log_tag", "Exception creating JSONArray " + e.toString());
			e.printStackTrace();
		}
    }
	
	@Override
    protected void onPostExecute(Void directions)
    {
    	
    	//ArrayList<ParcelableGeoPoint> pointsExtra = new ArrayList<ParcelableGeoPoint>();
		Log.d("log_tag", "ROUTES SIZE =" + routes.size());
    	if(routes.size() > 0)
    	{
    		
    		//create a new intent and pass the required info to it
    		Intent intent = new Intent(context, DisplayMapActivity.class);
    		intent.putExtra(ACLUB, aClub);
    		intent.putParcelableArrayListExtra(DRIVING_INFO, routes);//.putExtra(DRIVING_INFO, routes);
    		intent.putExtra(START_LOCATION, firstPoint);

	    	//get rid of the loading dialog
    		if(this.dialog.isShowing())
	    	{
	    		this.dialog.dismiss();
	    	}
	    	
	    	context.startActivity(intent);
    	}
    	else
    	{
    		Log.e("log_tag", "NULLPOINTER in Route ");
    		Toast.makeText(context, "Cannot get the directions. Please make sure the phone has an Internet connection", Toast.LENGTH_LONG).show();
    		this.dialog.dismiss();
    	}
    }
}
