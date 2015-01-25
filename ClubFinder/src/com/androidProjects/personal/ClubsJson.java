package com.androidProjects.personal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import security.SimpleProtector;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

public class ClubsJson {
	ArrayList<Club> clubList = new ArrayList<Club>();
	
	
	//method that reads in JSON files and translates info to the club class
	public String getFileContent(InputStream in)
	{
		StringBuilder builder = new StringBuilder();
		String content = null;
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null) 
			{
				builder.append(line);
			}
			
			//decrypt contents
			if(builder.toString().contains("club")){
				//do nothing - file already in plain text
				content = builder.toString();
			}
			else {
				//decrypt
				content = SimpleProtector.decrypt(builder.toString());
			}
			
		}
		catch(Exception e)
		{
			Log.e("log_tag", "Error with JSON:" + e.toString());
		}
	
		return content; 
	}
	
	//method that reads in JSON files and translates sport and location info into an array of strings for spinner slection
	public ArrayList<String> getSportSelectionItems(InputStream in)
	{
		return parseSports(getFileContent(in));
	}
	
	private ArrayList<String> parseSports(String content) {
		ArrayList<String> results = new ArrayList<String>();
		try
		{
			JSONObject jsonObject = new JSONObject(content); //a big string of values
    		JSONArray sportsArray = jsonObject.getJSONArray("sport");
    		if(sportsArray.length() > 0)
    		{
    			Log.i("log_tag", "sportsArray = " + sportsArray.length());
	    		// Grab the first club
    			for(int x = 0; x < sportsArray.length(); x++) 
				{			
					Log.i("log_tag", "sportsArray = " + sportsArray.length());
					JSONObject sportObject = sportsArray.getJSONObject(x);
					String sport = sportObject.getString("sportName");
					
					JSONArray regionArray = sportObject.getJSONArray("region");
					Log.i("log_tag", "regionsArray = " + regionArray.length());
					for(int y=0; y<regionArray.length(); y++) {
						JSONObject regionObject = regionArray.getJSONObject(y);
						String region = regionObject.getString("regionName");
						
						String result = region + " " + sport;
						results.add(result);
					}
	    		}
    		}		
		}
		catch(Exception e)
		{
			Log.e("log_tag", "Error creating JSON Objects:" + e.toString());
		}
		
		return results;
	}
	
	//method that reads in JSON files and translates info to the club class
	public ArrayList<Club> getClubInfo(InputStream in, String sportType)
	{
		return parseClubs(getFileContent(in), sportType); 
	}
	
	//parse the data in the JSON file
	private ArrayList<Club> parseClubs(String content, String sportType)
	{
		String sportTypeRegion = sportType.substring(0, sportType.lastIndexOf(" "));
		Log.d("sportTypeRegion", sportTypeRegion);
		String sportTypeSport = sportType.substring(sportType.lastIndexOf(" ") + 1, sportType.length());
		Log.d("sportTypeSport", sportTypeSport);
		ArrayList<Club> clubs = new ArrayList<Club>();
		try
		{
			JSONObject jsonObject = new JSONObject(content); //a big string of values
			
			//Club Data -- contains ALL clubs
    		JSONArray sportsArray = jsonObject.getJSONArray("sport");
    		
    		if(sportsArray.length() > 0)
    		{
	    		// Grab the first club
    			for(int x = 0; x < sportsArray.length(); x++) 
				{
					JSONObject sportObject = sportsArray.getJSONObject(x);
					String sport = sportObject.getString("sportName");
					
					if(sport.equalsIgnoreCase(sportTypeSport)) {
						JSONArray regionArray = sportObject.getJSONArray("region");
						for(int y=0; y<regionArray.length(); y++) {
							JSONObject regionObject = regionArray.getJSONObject(y);
							String region = regionObject.getString("regionName");
							if(region.equalsIgnoreCase(sportTypeRegion)) {
								JSONArray clubArray = regionObject.getJSONArray("club");
								Log.d("found Region", sportTypeRegion + sportTypeSport);
								clubs = extractClubInfo(clubArray);
							}
						}
					}
					
	    		}
    			
    			Collections.sort(clubs);
    		}
			
		}
		catch(Exception e)
		{
			Log.e("log_tag", "Error creating JSON Objects:" + e.toString());
		}
		
		return clubs;
	}
		
	private ArrayList<Club> extractClubInfo(JSONArray clubsArray) {
		ArrayList<Club> clubs = new ArrayList<Club>();
		Club c;
		Log.d("extractClubInfo", "in extractInfo()");
		if(clubsArray.length() > 0)
		{
    		// Grab the first club
			for(int y = 0; y < clubsArray.length(); y++) 
			{
				try {
					JSONObject club = clubsArray.getJSONObject(y);
					String clubID = club.getString("clubID");
					String clubName = club.getString("clubName");
					Log.d("extractClubInfo CLUBS", clubName);
					//get the geopoint from the lat and lon
					double latitude = Double.parseDouble(club.getString("Latitude"));
			        double lonitude = Double.parseDouble(club.getString("Longitude"));
			        LatLng marker = new LatLng(latitude, lonitude);
					GeoPoint geo = new GeoPoint((int) (latitude * 1E6), (int) (lonitude * 1E6));
					//String grounds = club.getString("Grounds");
					String colours = club.getString("Colours");
					String website = club.getString("Website");
					String facebook = club.getString("Facebook");
					String twitter = club.getString("Twitter");
					String email = club.getString("Email");
					String phone = club.getString("Phone");
	
			
					//now fill out the description
					String info = ""; 
					/*			if(grounds.length() != 0)
					{
						//Log.i("log_tag", "Grounds = \"" + grounds + "\"");
						if(info == "")
						{
							info += grounds.toString();				
						}
						else
							info += "\n" + grounds.toString();
					}*/
					if(colours.length() != 0)
					{
						if(info == "")
						{
							info += "Colours: " + colours.toString();				
						}
						else
							info += "\nColours: " + colours.toString();
					}
					if(website.length() != 0)
					{
						if(info == "")
						{
							info += "Web: " + website.toString();				
						}
						else
							info += "\nWeb: " + website.toString();
					}
					if(facebook.length() != 0)
					{
						if(info == "")
						{
							info += "Facebook: " + facebook.toString();				
						}
						else
							info += "\nFacebook: " + facebook.toString();
					}
					if(twitter.length() != 0)
					{
						if(info == "")
						{
							info += "Twitter: " + twitter.toString();				
						}
						else
							info += "\nTwitter: " + twitter.toString();
					}
					if(email.length() != 0 || !(email.equals("null")))
					{
						if(info == "")
						{
							info += "Email: " + email.toString();				
						}
						else
							info += "\nEmail: " + email.toString();
					}
					if(phone.length() != 0)
					{
						if(info == "")
						{
							info += "Phone: " + phone.toString();				
						}
						else
							info += "\nPhone: " + phone.toString();
					}
			
					c = new Club(clubName, info, geo, marker);
					clubs.add(c);
				}
				catch(Exception e) {
					Log.e("CLubsJson", "extractClubInfo " + e.toString());
				}
			}
		}
		return clubs;
	}		
}
