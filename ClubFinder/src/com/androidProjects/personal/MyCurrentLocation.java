package com.androidProjects.personal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MyCurrentLocation
{
    LocationManager lm;
    Context context;
    private AlertDialog alert;
    private LocationResult locationResult;
    Criteria criteria;
    String provider;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    public static boolean isEnableLocationSelected = true;
    
    public MyCurrentLocation(Context c)
    {
    	context = c;
    }

    public boolean getLocation(Context context, LocationResult result)
    {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult=result;
        if(lm == null)
        {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        
        //exceptions will be thrown if provider is not permitted.
        try
        {
        	gps_enabled =lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex){}
        
        try
        {
        	network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex){}
        

        //don't start listeners if no provider is enabled
        if(!gps_enabled && !network_enabled)
        {
        	if(isEnableLocationSelected == true)
        		showGPSDisabledAlertToUser();
        	return false;
        }
        
        if(gps_enabled)
        {
        	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListenerGps);
            Log.d("log_tag", "GPS Listener Activated");
        }
        if(network_enabled)
        {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListenerNetwork);
            Log.d("log_tag", "Network Listener Activated");
        }
        
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() 
    {
        public void onLocationChanged(Location location) 
        {
            locationResult.gotLocation(location);
            Log.d("log_tag", "GPS Location Changed " + location.toString());
        }
        public void onProviderDisabled(String provider) {
        	Log.d("log_tag", "GPS Disabled " + provider.toString());
        }
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

   LocationListener locationListenerNetwork = new LocationListener() 
   {
        public void onLocationChanged(Location location) 
        {
            locationResult.gotLocation(location);
            Log.d("log_tag", "Network Location Changed " + location.toString());
        }
        public void onProviderDisabled(String provider) {
        	Log.d("log_tag", "Network Provider Disabled " + provider.toString());
        }
        public void onProviderEnabled(String provider) {
        	Log.d("log_tag", "Network Provider Enabled " + provider.toString());
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };
    
    public void removeListenerUpdates()
    {
    	lm.removeUpdates(locationListenerNetwork);
    	lm.removeUpdates(locationListenerGps);
    }
    
    public boolean getEnableLocationButtonState()
    {
    	return isEnableLocationSelected;
    }
    
    //class GetLastLocation extends TimerTask 
    //{
    	//@Override
        //public void run() 
        //{
    		//context.runOnUiThread(new Runnable() {

            //@Override
            public void updateWithLastLocation() {

             //lm.removeUpdates(locationListenerGps);
             //lm.removeUpdates(locationListenerNetwork);

             Location net_loc=null, gps_loc=null;
             if(gps_enabled)
             {
                 gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             }
                 
             if(network_enabled)
             {
                 net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
             }
             
             //if there are both values use the latest one
             if(gps_loc!=null && net_loc!=null)
             {
                 if(gps_loc.getTime() > net_loc.getTime())
                 {
                     locationResult.gotLocation(gps_loc);
                 }
                 else
                 {
                     locationResult.gotLocation(net_loc);
                 }
                 return;
             }

             if(gps_loc != null)
             {
                 locationResult.gotLocation(gps_loc);
                 return;
             }
             if(net_loc != null)
             {
                 locationResult.gotLocation(net_loc);
                 return;
             }
             
             locationResult.gotLocation(null);
        
        }
    
    public boolean getNetworkLocation(Context context, LocationResult result)
    {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult=result;
        if(lm == null)
        {
        	Log.d("log_tag", "Setting Location Manager Criteria");
        	
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            
            criteria = new Criteria();
            criteria.setAccuracy( Criteria.ACCURACY_COARSE );
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW); 
            
        }
        
        provider = lm.getBestProvider( criteria, true );
        try {
        	network_enabled=lm.isProviderEnabled(provider);
        }catch(Exception ex){}

        if (provider == null) 
        {
        	if(isEnableLocationSelected == true)
        		showGPSDisabledAlertToUser();
        	Log.e("log_tag", "No location provider found!" );
        	return false;
        }

       //don't start listeners if no provider is enabled
        if(!network_enabled)
        {
        	if(isEnableLocationSelected == true)
        		showGPSDisabledAlertToUser();
        	return false;
        }
        else
        {
        	lm.requestLocationUpdates(provider,1000, 0,locationListenerNetwork);  
        	Log.i(provider.toString() + " PROVIDER", "ENABLED");
        	Log.d("log_tag", "Requesting Location Updates from " + provider.toString());
        }

        return true;
    }

    
    public void showGPSDisabledAlertToUser()
	{
		//final Context ctx = c;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
    	alertDialogBuilder.setMessage("Location Services have been disabled. They need to be enabled in order to get Directions. Would you like to enable them?").setCancelable(false).setPositiveButton("Settings",
    	new DialogInterface.OnClickListener()
    	{
    		public void onClick(DialogInterface dialog, int id){
    			try
    			{
    				Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    				isEnableLocationSelected = true;
    				context.startActivity(callGPSSettingIntent);
    			}
    			catch(NullPointerException e)
    			{
    				Toast.makeText(context, "Unable to go to Settings Page", Toast.LENGTH_SHORT).show();			
    				Log.e("log_tag", "Null Pointer Exception in MyCurrentLocation.java");
    			}
    		}
    	});
    	alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialog, int id){
    			isEnableLocationSelected = false;
    			dialog.cancel();
    		}
    	});
    	alert = alertDialogBuilder.create();
    	alert.show();
    }

    public static abstract class LocationResult
    {
        public abstract void gotLocation(Location location);
    }
}