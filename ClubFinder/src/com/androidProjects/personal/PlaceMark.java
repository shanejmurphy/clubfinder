package com.androidProjects.personal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

public class PlaceMark implements Parcelable
{
	private GeoPoint location;
	private LatLng llLocation;
	private String instructions;
	private String distance;
	private String duration;
		
	//default constructor
	PlaceMark(){}
	
	public PlaceMark(GeoPoint g, String dis, String dur, String ins)
	{
		location = g;
		distance = dis;
		duration = dur;
		instructions = ins;
	}
	
	public PlaceMark(LatLng l, String dis, String dur, String ins)
	{
		llLocation = l;
		distance = dis;
		duration = dur;
		instructions = ins;
	}
	
	public void setLocation(GeoPoint location) 
	{
		this.location = location;
	}
		
	public GeoPoint getLocation() 
	{
		return location;
	}
		
	public void setInstructions(String instructions) 
	{
		this.instructions = instructions;
	}
		
	public String getInstructions() 
	{
		return instructions;
	}
		
	public void setDistance(String distance) 
	{
		this.distance = distance;
	}
	
	public String getDistance() 
	{
		return distance;
	}
	
	public void setDuration(String dur) 
	{
		this.duration = dur;
	}
	
	public String getDuration() 
	{
		return duration;
	}

	public LatLng getLatLng(){
		return llLocation;
	}
	
	public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
    	//ParcelableGeoPoint gp = new ParcelableGeoPoint(location);
    	//out.writeParcelable(gp, flags);
    	out.writeParcelable(llLocation, flags);
        out.writeString(instructions);
        out.writeString(distance);
        out.writeString(duration);
    }

    public static final Parcelable.Creator<PlaceMark> CREATOR
            = new Parcelable.Creator<PlaceMark>() {
        public PlaceMark createFromParcel(Parcel in) {
            return new PlaceMark(in);
        }

        public PlaceMark[] newArray(int size) {
            return new PlaceMark[size];
        }
    };

    private PlaceMark(Parcel in) {
        //ParcelableGeoPoint loc = in.readParcelable(ParcelableGeoPoint.class.getClassLoader());
        //location = loc.getGeoPoint();
    	llLocation = in.readParcelable(com.google.android.gms.maps.model.LatLng.class.getClassLoader());
        instructions = in.readString();
        distance = in.readString();
        duration = in.readString();
    }
}
