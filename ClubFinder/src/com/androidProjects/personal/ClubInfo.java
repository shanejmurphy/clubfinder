package com.androidProjects.personal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

import security.*;


/**
 * @author shanem
 *
 *	We are trying to find and extract information about various clubs in Dublin from an xml file
 *	that displays the information as follows:
 *
 *	<Placemark>
 *   	<name>Ballyboden St. Endas GAA Club</name>
 *  	<Snippet>Pairc Ui Mhuricu - Ballyboden St. Endas GAA Club main pitch at clubhouse, opposi</Snippet>
 *   	<description><![CDATA[<div dir="ltr">Pairc Ui Mhuricu - Ballyboden St. Endas GAA Club main pitch at clubhouse, opposite side of Road is Cherryfield Park for Juvenile Games.</div>]]></description>
 *   	<styleUrl>#style47</styleUrl>
 *   	<Point>
 *     		<coordinates>-6.317428,53.289257,0.000000</coordinates>
 *   	</Point>
 * 	</Placemark>
 */

public class ClubInfo {
	
	ArrayList<Club> clubList = new ArrayList<Club>();
	
	// method to return the clubs in the array list
	public String [] getClubNames(ArrayList<Club> list)
	{
		String [] theClubs = new String[list.size()];

		for(int i=0; i<list.size(); i++)
		{
			theClubs[i] = list.get(i).getClubName();
		}
		return theClubs;
	}
	
	// method to return the clubs in the array list
	public ArrayList<String> getListClubNames(ArrayList<Club> list)
	{
		ArrayList<String> theClubs = new ArrayList<String>();
		for(int i=0; i<list.size(); i++)
		{
			//theClubs.set(i, list.get(i).getClubName());
			theClubs.add(list.get(i).getClubName());
		}
		return theClubs;
	}
	
	//method that reads in JSON files and translates info to the club class
	public ArrayList<Club> getClubJSONInfo(InputStream in)
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
		}
		catch(Exception e)
		{
			Log.e("log_tag", "Error with JSON:" + e.toString());
		}
		
		//decrypt contents
		if(builder.toString().contains("clubs")){
			//do nothing - file already in plain text
			content = builder.toString();
		}
		else {
			//decrypt
			content = SimpleProtector.decrypt(builder.toString());
		}
		
		return parseClubJSONInfo(content); 
	}
	
	//parse the data in the JSON file
	private ArrayList<Club> parseClubJSONInfo(String clubJSONInfo)
	{
		ArrayList<Club> clubs = new ArrayList<Club>();
		Club c;
		try
		{
			JSONObject jsonObject = new JSONObject(clubJSONInfo); //a big string of values
			
			//Club Data -- contains ALL clubs
    		JSONArray clubsArray = jsonObject.getJSONArray("clubs");
    		
    		if(clubsArray.length() > 0)
    		{
	    		// Grab the first club
    			for(int y = 0; y < clubsArray.length(); y++) 
				{
    				JSONObject club = clubsArray.getJSONObject(y);
				
					// Take all the info from the club JSON file
					
					//get the geopoint from the lat and lon
					double latitude = Double.parseDouble(club.getString("lat"));
			        double lonitude = Double.parseDouble(club.getString("lon"));
			        LatLng marker = new LatLng(latitude, lonitude);
					GeoPoint geo = new GeoPoint((int) (latitude * 1E6), (int) (lonitude * 1E6));
					
					String name = club.getString("name");
					String grounds = club.getString("grounds");
					String description = club.getString("description");
					String colours = club.getString("colours");
					String website = club.getString("website");
					String facebook = club.getString("facebook");
					String twitter = club.getString("twitter");
					String email = club.getString("email");
					String phone = club.getString("phone");
					
					//now fill out the description
					String info = ""; 
					if(grounds.length() != 0)
					{
						//Log.i("log_tag", "Grounds = \"" + grounds + "\"");
						if(info == "")
						{
							info += grounds.toString();				
						}
						else
							info += "\n" + grounds.toString();
					}
					if(description.length() != 0)
					{
						if(info == "")
						{
							info += description.toString();				
						}
						else
							info += "\n" + description.toString();
					}
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
					if(email.length() != 0)
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
					//Log.i("log_tag", "Info = \"" + info + "\"");
					c = new Club(name.toString(), info, geo, marker);
					clubs.add(c);
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
	
	
	/*
	 * Below method is a legacy method used to retieve the club info from the 
	 * kml files that were used originally. Code has been updated to
	 * parse the information from json files - getClubJSONInfo() is
	 * above
	 * given an inputstream of info, extract all info pertaining to clubs
	
	public ArrayList<Club> getClubInfo(InputStream is)
	{
		try
		{
			String clubName, snippet, description, tempDescription, location;
			int numClubs;
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(is);
			Element docEle = doc.getDocumentElement();
			NodeList placeMark = docEle.getElementsByTagName("Placemark");
			
			numClubs = placeMark.getLength(); //number of club items in the list
			Log.i("NUMCLUBS:", String.valueOf(numClubs));
			
			if(placeMark != null && numClubs > 0)
			{
				for(int i = 0; i < numClubs; i++)
				{
					Node node = placeMark.item(i);
						
					if(node.getNodeType() == Node.ELEMENT_NODE)
					{
						Element e = (Element) node;
						NodeList nl = e.getElementsByTagName("name");
						clubName = (String) nl.item(0).getChildNodes().item(0).getNodeValue();
							
						if(e.hasAttribute("Snippet")) //do a check for Snippet element as some clubs dont have this
						{
							nl = e.getElementsByTagName("Snippet");
							snippet = (String) nl.item(0).getChildNodes().item(0).getNodeValue();
							if(snippet.length() <= 60)
							{
								description = snippet;
							}
							else
							{
								nl = e.getElementsByTagName("description");
								tempDescription = (String) nl.item(0).getChildNodes().item(0).getNodeValue();
								if((tempDescription.indexOf("\">") > 0) && (tempDescription.indexOf("<",20) > 0))
								{
									tempDescription = tempDescription.substring((tempDescription.indexOf("\">")+2),tempDescription.length()-1);
									description = tempDescription.substring(0, tempDescription.indexOf("<"));
								}
								else
								{
									description = clubName;
								}
							}
						}
						else
						{
							nl = e.getElementsByTagName("description");
							tempDescription = (String) nl.item(0).getChildNodes().item(0).getNodeValue();
							if((tempDescription.indexOf("\">") > 0) && (tempDescription.indexOf("<",20) > 0))
							{
								tempDescription = tempDescription.substring((tempDescription.indexOf("\">")+2),tempDescription.length()-1);
								description = tempDescription.substring(0, tempDescription.indexOf("<"));
							}
							else
							{
								description = clubName;
							}
						}
							
						//get the actual coordinates supplied in xml document
						nl = e.getElementsByTagName("coordinates");
						location = (String) nl.item(0).getChildNodes().item(0).getNodeValue();
						String [] coordinates = location.split(","); //split the string up into arrays of Strings

						//get the geopoint from the coordinates
						double lon = Double.parseDouble(coordinates[0]);
				        double lat = Double.parseDouble(coordinates[1]);
				        GeoPoint clubLocation = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
						
						//call the Club constructor
						Club club = new Club(clubName, description, clubLocation);
						
						//Add the club to the ArrayList
						clubList.add(club);
						
						//Output data
						Log.i("Club\n", club.toString());
					}
				}
			}
			else
			{
				System.exit(1);
			}

			//Sort the list by natural order
			Collections.sort(clubList);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return clubList;
	}*/
}
