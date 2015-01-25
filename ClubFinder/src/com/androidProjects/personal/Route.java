package com.androidProjects.personal;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Route implements Parcelable
{
	private String totalDistance;
	private String totalDuration;
	private List<LatLng> points;	//{startpoint,endpoint}
	private List<PlaceMark> placeMarks;	//steps containing [distance, duration, end_location, instructions]
	private String destination;
	//private String polyline;
	private List<String> polyStrings;
	
	//private static List<GeoPoint> pgp;
	
	//default constructor required
	public Route()
	{
		points = new ArrayList<LatLng>();
		placeMarks = new ArrayList<PlaceMark>();
		polyStrings = new ArrayList<String>();
	}
	
	public Route(String tDist, String tDur, List<LatLng> p, List<PlaceMark> pm, String dest, List<String> poly)
	{
		totalDistance = tDist;
		totalDuration = tDur;
		points = p;
		placeMarks = pm;
		destination = dest;
		polyStrings = poly;
	}
	
	public void setTotalDistance(String totalDistance) 
	{
		this.totalDistance = totalDistance;
	}

	public String getTotalDistance() 
	{
		return totalDistance;
	}
	
	public void setTotalDuration(String duration) 
	{
		this.totalDuration = duration;
	}

	public String getTotalDuration() 
	{
		return totalDuration;
	}
	
	public List<String> getPolyStringList() 
	{
		return polyStrings;
	}
	
	public List<LatLng> getLatLngPolyline() 
	{
		List<LatLng> polyPoints = new ArrayList<LatLng>();
		for(int i=0; i<polyStrings.size(); i++)
		{
			polyPoints.addAll(decodeLatLngPoly(polyStrings.get(i)));
		}
		return polyPoints;
	}
	
	public void setDestination(String dest) 
	{
		this.destination = dest;
	}

	public String getDestination() 
	{
		return destination;
	}

	public void setLatLngPoints(List<LatLng> points) 
	{
		this.points = points;
	}

	public List<LatLng> getLatLngPoints() 
	{
		return points;
	}
	
	public LatLng getStartPoint(){
		return points.get(0);
	}
	
	public void setPolyStringList(List<String> pList) 
	{
		polyStrings = pList;
	}
	
	private List<LatLng> decodeLatLngPoly(String encoded) 
	{

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) 
	    {
	        int b, shift = 0, result = 0;
	        do 
	        {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do 
	        {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        LatLng p = new LatLng(((double) lat / 1E5), ((double) lng / 1E5));
	        poly.add(p);
	    }

	    return poly;
	}
	
	/*public void setParcelableGeoPoints(ArrayList<ParcelableGeoPoint> geo) 
	{
		pgp = geo;
	}
	
	public ArrayList<ParcelableGeoPoint> getParcelableGeoPoints() 
	{
		return pgp;
	}*/

	public void addGeoPoint(LatLng point)
	{
		if (points == null) 
		{
			points = new ArrayList<LatLng>();
		}
		points.add(point);
	}

	public void setPlaceMarks(List<PlaceMark> placeMarks) 
	{
		this.placeMarks = placeMarks;
	}

	public List<PlaceMark> getPlaceMarks() 
	{
		return placeMarks;
	}

	public void addPlaceMark(PlaceMark mark)
	{
		if(placeMarks == null) 
		{
			placeMarks = new ArrayList<PlaceMark>();
		}
		placeMarks.add(mark);
	}
	
	//parcelable part
	
	public int describeContents() 
	{
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) 
    {
    	out.writeString(totalDistance);
        out.writeString(totalDuration);
        out.writeTypedList(placeMarks);
        out.writeString(destination);
        //out.writeString(polyline);
        out.writeStringList(polyStrings);
      //out.writeList(geoPoints);
    }

    public static final Parcelable.Creator<Route> CREATOR
            = new Parcelable.Creator<Route>() {
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    public Route(Parcel in) {
       
        this();
        totalDistance = in.readString();
        totalDuration = in.readString();
        in.readTypedList(placeMarks, PlaceMark.CREATOR);
        destination = in.readString();
        in.readStringList(polyStrings);
        //polyline = in.readString();
      //in.readList(geoPoints);
    }
}
