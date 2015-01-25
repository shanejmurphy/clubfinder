package com.androidProjects.personal;

import android.os.Parcel;
import android.os.Parcelable;

public class Direction implements Parcelable{
	private String distance;
	private String instruction;
	private String extraInfo;
	
	//constructor
	public Direction(String d, String i, String e){
		distance = d;
		instruction = i;
		extraInfo = e;
	}
	
	//getters and setters
	public String getDistance(){
		return distance;
	}
	
	public String getInstruction(){
		return instruction;
	}
	
	public String getExtraInfo(){
		return extraInfo;
	}
	
	//parcelable part
	
		public int describeContents() 
		{
	        return 0;
	    }

	    public void writeToParcel(Parcel out, int flags) 
	    {
	    	out.writeString(distance);
	        out.writeString(instruction);
	        out.writeString(extraInfo);
	    }

	    public static final Parcelable.Creator<Direction> CREATOR
	            = new Parcelable.Creator<Direction>() {
	        public Direction createFromParcel(Parcel in) {
	            return new Direction(in);
	        }

	        public Direction[] newArray(int size) {
	            return new Direction[size];
	        }
	    };

	    public Direction(Parcel in) {
	        distance = in.readString();
	        instruction = in.readString();
	        extraInfo = in.readString();
	    }
}
