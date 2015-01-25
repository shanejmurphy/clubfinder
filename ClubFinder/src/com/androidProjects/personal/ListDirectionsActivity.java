package com.androidProjects.personal;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;

public class ListDirectionsActivity extends SherlockFragmentActivity {
	
	ArrayList<String> directions;
	ArrayList<Direction> directionList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/
        
        setContentView(R.layout.activity_list_directions);
        
        ActionBar topBar = getSupportActionBar();
        topBar.setDisplayHomeAsUpEnabled(true);
        
        //list button
        ImageButton map = (ImageButton) findViewById(R.id.list_button);
        map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	//end current activity - activities will propogate back up stack to previous running activity
            	finish();
            }

        });
        
        //directions = getIntent().getStringArrayListExtra(DisplayMapActivity.DIRECTIONS);
        directionList = getIntent().getParcelableArrayListExtra(DisplayMapActivity.DIRECTIONS);
        
        //set the text in the ActionBar
        //topBar.setTitle("Directions to " + directions.get(directions.size()-1));
        topBar.setTitle("Directions to " + directionList.get(directionList.size()-1).getInstruction());
        
        //get the list view 
        final ListView lv = (ListView) findViewById(R.id.mainListView);
        
        //MyArrayAdapter adapter = new MyArrayAdapter(this, directions);
        MyArrayAdapter adapter = new MyArrayAdapter(this, directionList);
        lv.setAdapter(adapter);
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
    
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }
    
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }
    
    private class MyArrayAdapter extends ArrayAdapter<Direction>{
    	private ArrayList<Direction> dirs;
    	private LayoutInflater mInflater;
    	private Context context;
    	
    	public MyArrayAdapter(Context context, ArrayList<Direction> directions) {
    	    super(context, R.layout.list_layout, directions);
    	    this.context = context;
    	    this.dirs = directions;
    	  }
    	
    	@Override
    	public int getCount() {
            return dirs.size();
        }
    	
    	@Override
		public View getView(int position, View convertView, ViewGroup Parent)
		{
    		//LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		//View v = inflater.inflate(R.layout.list_layout, Parent, false);
			View v = convertView;
			
			if(v == null) 
			{  
				LayoutInflater inflater = getLayoutInflater();
                v = inflater.inflate(R.layout.list_layout, null);  
			} 

			//assign variables to the various views in the List directions file
			TextView num = (TextView) v.findViewById(R.id.num);  
			TextView dis = (TextView) v.findViewById(R.id.distance);  
	        TextView ins = (TextView) v.findViewById(R.id.direction);
	        TextView ext = (TextView) v.findViewById(R.id.extra);
	        ImageView img = (ImageView) v.findViewById(R.id.destPin);
	        
			Direction direction = dirs.get(position);
			
			//set the image to alpha so the number is visible
			//img.setBackgroundResource(0);
			img.setVisibility(View.INVISIBLE);
			num.setText(String.valueOf(position+1));
			dis.setText(direction.getDistance());
			ins.setText(direction.getInstruction());
			ext.setText(direction.getExtraInfo());
			
			if(position == dirs.size()-1)
			{
				num.setText("");
				//num.setText(String.valueOf(position+1));
				img.setVisibility(View.VISIBLE);
				//img.setBackgroundResource(R.drawable.destination_pin);
			}

	        return v;
		}
    }
    
    /*private class MyArrayAdapter extends ArrayAdapter<String>{
    	private ArrayList<String> dirs;
    	private LayoutInflater mInflater;
    	private Context context;
    	
    	public MyArrayAdapter(Context context, ArrayList<String> directions) {
    	    super(context, R.layout.list_layout, directions);
    	    this.context = context;
    	    this.dirs = directions;
    	  }
    	
    	@Override
    	public int getCount() {
            return dirs.size();
        }
    	
    	@Override
		public View getView(int position, View convertView, ViewGroup Parent)
		{
    		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		View v = inflater.inflate(R.layout.list_layout, Parent, false);
			//View v = convertView;
			
			if(v == null) 
			{  
                v = mInflater.inflate(R.layout.list_layout, null);  
			}  

			//assign variables to the various views in the List directions file
			TextView num = (TextView) v.findViewById(R.id.num);  
			TextView dis = (TextView) v.findViewById(R.id.distance);  
	        TextView ins = (TextView) v.findViewById(R.id.direction);
	        TextView ext = (TextView) v.findViewById(R.id.extra);
	        ImageView img = (ImageView) v.findViewById(R.id.destPin);
	        
			String direction = dirs.get(position);
			
			//set the image to alpha so the number is visible
			//img.setBackgroundResource(0);
			img.setVisibility(View.INVISIBLE);
			num.setText(String.valueOf(position+1));
			
			if(direction.contains("m then"))
			{
				int substringDelim = (direction.indexOf("then") + 4);	//end of then 
				int midSubstring = direction.indexOf(".", substringDelim); //get the position of the 1st occorance of . after the initial substring
				int endSubstring = direction.lastIndexOf("."); //get the position of the last occorance of .
				//Log.d("log_tag", "midSubstring = " + midSubstring);
				//Log.d("log_tag", "endSubstring = " + endSubstring);
				
				//1st part
				String driveDistance = direction.substring(0, substringDelim); //"Drive 0.2 km then"
				dis.setText(driveDistance);
				
				//if only 2 parts this is the end
				if(midSubstring == endSubstring || midSubstring+1 >= endSubstring) //if the positions are the same or more or less it means there is only 1 sentence to process
				{
					String driveInstruction = direction.substring(substringDelim+1, endSubstring);
					ins.setText(driveInstruction);
					ext.setText("");
				}
				//if more than 2 parts we need to split it again
				else if(endSubstring > midSubstring)
				{
					
					 * EG
					 * direction = "Drive 0.2 km then take a right on to the R114. Destination will be on your left."
					 * substringDelim = 16;
					 * midSubstring = 45;
					 * endSubstring = 78;
					 * end EG
					
					
					String driveInstruction = direction.substring(substringDelim+1, midSubstring);
					String extraInstruction = direction.substring(midSubstring+2, endSubstring); //+2 so it doesnt take the space
					ins.setText(driveInstruction);
					ext.setText(extraInstruction);
				}
				
			}
			else
			{
				dis.setText("");
				ins.setText(direction);
				ext.setText("");
				if(position > 0)
				{
					num.setText("");
					//num.setText(String.valueOf(position+1));
					img.setVisibility(View.VISIBLE);
					//img.setBackgroundResource(R.drawable.destination_pin);
				}
			}

	        return v;
		}
    }*/
}
