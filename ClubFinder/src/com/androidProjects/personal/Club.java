package com.androidProjects.personal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;
 
/*  
 * 
 *  pw.println("\t\"grounds\":" + "\"" + c.getGround() + "\"" + ",");
	pw.println("\t\"description\":" + "\"" + c.getDescription() + "\"" + ",");
	pw.println("\t\"colors\":" + "\"" + c.getColours() + "\"" + ",");
	pw.println("\t\"website\":"  + "\"" + c.getWebsite() + "\"" + ",");
	pw.println("\t\"facebook\":"  + "\"" + c.getFacebook() + "\"" + ",");
	pw.println("\t\"twitter\":"  + "\"" + c.getTwitter() + "\"" + ",");
	pw.println("\t\"contact\":"  + "\"" + c.getContact() + "\"" + ",");
	pw.println("\t\"phone\":"  + "\"" + c.getPhone() + "\"" + ",");
 * 
 */


public class Club implements Comparable<Club>, Parcelable{

	private String clubName;	//name of the club
	private String description;	//brief description of club/clubhouse
	private GeoPoint location;	//coordinates in longitude and latitude
	private String pitches;		//where the club is based
	private String colours;		//club colours
	private String website;		//club website
	private String facebook;	//facebook page
	private String twitter;		//twitter account
	private String email;		//email to conatct
	private String phone;		//phone number of contact
	
	private String latitude;
	private String longitude;
	private LatLng point;
	
	
	//constructor
	public Club(String name, String desc, GeoPoint loc, LatLng pt)
	{
		this.clubName = name;
		this.description = desc;
		this.location = loc;
		this.point = pt;
	}
	
	//method to return the club name
	public String getClubName()
	{
		return this.clubName;
	}
	
	//method that returns the brief description supplied with the club info
	public String getDescription()
	{
		return this.description;
	}
	
	/*
	//return String array with the location of the club represented as co-ordinates
	public String [] getCoordinates()
	{
		return this.coordinates;
	}
	*/
	
	public GeoPoint getLocation()
	{
		/*
		//get the clubs Coordinates
		String [] coords = getCoordinates();
		double lon = Double.parseDouble(coords[0]);
        double lat = Double.parseDouble(coords[1]);
        GeoPoint clubLocation = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
        */
        return this.location;
	}
	
	//club colours
	public String getColours()
	{
		if(this.colours != null)
		{
			return this.colours;
		}
		else
			return "";
		
	}
	
	//pitches and club grounds
	public String getPitches()
	{
		if(this.pitches != null)
		{
			return this.pitches;
		}
		else
			return "";
	}
	
	//club website
	public String getWebsite()
	{
		if(this.website != null)
		{
			return this.website;
		}
		else
			return "";
	}
	
	//facebook page
	public String getFacebook()
	{
		if(this.facebook != null)
		{
			return this.facebook;
		}
		else
			return "";
	}
	
	//twitter account info
	public String getTwitter()
	{
		if(this.twitter != null)
		{
			return this.twitter;
		}
		else
			return "";
	}
	
	//contact person
	public String getEmail()
	{
		if(this.email != null)
		{
			return this.email;
		}
		else
			return "";
	}
	
	//method that returns the phone no.
	public String getPhone()
	{
		if(this.phone != null)
		{
			return this.phone;
		}
		else
			return "";
	}
	
	//latitude
	public String getLatitude(){
		return latitude;
	}
	
	public void setLatitude(String l){
		latitude = l;
	}
	
	//longitude
	public String getLongitude(){
		return longitude;
	}
	
	public void setLongitude(String l){
		longitude = l;
	}
	
	public LatLng getLocationPoint()
	{
        return point;
	}
	
	public void setLocationPoint(LatLng p){
		point = p;
	}
	
	//override toString method
	public String toString()
	{
		return "Club: " + this.clubName + "\nDescription: " + this.description + "\nLocation: " + this.location.toString() + ", \n--------------------------------------------------------------------\n";
	}
	
	public int compareTo(Club c)
	{
			return clubName.compareTo(c.getClubName());
	}
	
	//Parcelable part
	public int describeContents() 
	{
        return 0;
    }
	
	public void writeToParcel(Parcel out, int flags) {
        out.writeString(getClubName());
        out.writeString(getDescription());
        out.writeParcelable(new ParcelableGeoPoint(getLocation()), flags);
        out.writeParcelable(point, flags);
        //out.writeString(latitude);
        //out.writeString(longitude);
    }

    public static final Parcelable.Creator<Club> CREATOR
            = new Parcelable.Creator<Club>() {
        public Club createFromParcel(Parcel in) {
            return new Club(in);
        }

        public Club[] newArray(int size) {
            return new Club[size];
        }
    };

    private Club(Parcel in) {
    	//
    	
        clubName = in.readString();
        description = in.readString();
        ParcelableGeoPoint loc = in.readParcelable(ParcelableGeoPoint.class.getClassLoader());
        location = loc.getGeoPoint();
        point = in.readParcelable(LatLng.class.getClassLoader());
        //latitude = in.readString();
        //longitude = in.readString();
    }
}